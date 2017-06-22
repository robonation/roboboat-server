package com.felixpageau.roboboat.mission;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.felixpageau.roboboat.mission.nmea.NMEASentence;
import com.felixpageau.roboboat.mission.nmea.SentenceDefinition;
import com.felixpageau.roboboat.mission.nmea.SentenceRegistry;
import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.server.Config;
import com.felixpageau.roboboat.mission.structures.Challenge;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.DockingSequence;
import com.felixpageau.roboboat.mission.structures.HeartbeatReport;
import com.felixpageau.roboboat.mission.structures.LeaderSequence;
import com.felixpageau.roboboat.mission.structures.Position;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.felixpageau.roboboat.mission.structures.Timestamp;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public class NMEAServer implements Runnable {
  private static final Logger LOG = LoggerFactory.getLogger(NMEAServer.class);
  private final int port;
  private final SentenceRegistry registry;
  private final AtomicBoolean abort = new AtomicBoolean(false);
  private final AtomicBoolean debug = new AtomicBoolean(false);
  private final CompetitionManager competitionManager;
  private final Executor executor = Executors.newFixedThreadPool(50);

  public NMEAServer(CompetitionManager competitionManager, int port, SentenceRegistry registry, boolean debug) {
    Preconditions.checkArgument(port > 0, "port must be > 0");
    this.competitionManager = Preconditions.checkNotNull(competitionManager, "competitionManager cannot be null");
    this.registry = Preconditions.checkNotNull(registry, "registry cannot be null");
    this.port = port;
    this.debug.set(debug);
  }

  @Override
  public void run() {
    try (ServerSocket ss = new ServerSocket(port)) {
      while (!abort.get()) {
        Socket client = ss.accept();
        System.out.println("New NMEA connection open on client socket" + client.getLocalPort());
        executor.execute(new NMEAWorker(competitionManager, registry, client));
      }
    } catch (IOException e) {
      throw new RuntimeException("NMEAServer failed to start on port: " + port, e);
    }
  }

  public void start() {
    executor.execute(this);
    System.out.println("\n\nNMEA Server started on:" + getPort());
  }

  public void stop() {
    abort.set(true);
    System.out.println("\n\nNMEA Server stopping on:" + getPort());
  }

  public int getPort() {
    return port;
  }

  private class NMEAWorker implements Runnable {
    private final Socket s;
    private final SentenceRegistry registry;
    private final CompetitionManager competitionManager;

    public NMEAWorker(CompetitionManager competitionManager, SentenceRegistry registry, Socket s) {
      this.s = Preconditions.checkNotNull(s, "socket cannot be null");
      this.registry = Preconditions.checkNotNull(registry, "registry cannot be null");
      this.competitionManager = Preconditions.checkNotNull(competitionManager, "competitionManager cannot be null");
    }

    public String createResponse(String talkerId, String sentenceId, Boolean... value) {
      return createResponse(talkerId, sentenceId, Arrays.stream(value).map(b -> Boolean.toString(b)).collect(Collectors.toList()));
    }

    public String timestamp() {
      return LocalDateTime.now().format(Config.NMEA_DATE_FORMATTER.get());
    } 

    public List<String> baySequence(DockingSequence seq) {
      return seq.getDockingBaySequence().stream().map(b -> b.getCode().getValue()).collect(Collectors.toList());
    }

    public String createResponse(String talkerId, String sentenceId, List<String> fields) {
      Preconditions.checkNotNull(talkerId, "talkerId cannot be null");
      Preconditions.checkNotNull(sentenceId, "sentenceId cannot be null");
      Preconditions.checkNotNull(fields, "fields cannot be null");
      SentenceDefinition sd = registry.getDefinition(talkerId, sentenceId);
      if (sd == null) {
        throw new IllegalArgumentException(String.format("There is no definition for talker %s and sentence %s", talkerId, sentenceId));
      }
      return new NMEASentence(sentenceId, talkerId, sd, fields).toString();
    }

    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY")
    @Override
    public void run() {
      try (BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream(), App.APP_CHARSET));
          BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), App.APP_CHARSET))) {
        while (!abort.get()) {
          String line = r.readLine();
          if (line == null || line.isEmpty() || "\n".equals(line)) {
            // Empty line. Continue
            continue;
          } else {
            try {
              if (debug.get()) {
                System.out.println("** SERVER READ: " + line);
              }
              NMEASentence sentence = NMEASentence.parse(registry, line);
              ReportStatus status;
              Course course = Course.fromString(sentence.getField(0));
              TeamCode team = new TeamCode(sentence.getField(1));
              switch (sentence.getTalkerId() + sentence.getSentenceId()) {
              case "SVSTR":
                status = competitionManager.startRun(course, team);
                w.write(createResponse("TD", sentence.getSentenceId(), status.isSuccess()));
                break;
              case "SVEND":
                status = competitionManager.endRun(course, team);
                w.write(createResponse("TD", sentence.getSentenceId(), status.isSuccess()));
                break;
              case "SVHRT":
                Challenge c = Challenge.fromString(sentence.getField(3));
                Position p = Position.fromNMEA(sentence.getField(4), sentence.getField(5));
                status = competitionManager.reportHeartbeat(course, team, new HeartbeatReport(new Timestamp(), c, p));
                w.write(createResponse("TD", sentence.getSentenceId(), Arrays.asList(timestamp(), Boolean.toString(status.isSuccess()))));
                break;
              case "SVDOC":
                DockingSequence seq = competitionManager.getDockingSequence(course, team);
                List<String> args = new ArrayList<>();
                args.add(timestamp());
                args.addAll(baySequence(seq));
                w.write(createResponse("TD", sentence.getSentenceId(), args));
                break;
              case "SVFOL":
                LeaderSequence ls = competitionManager.getLeaderSequence(
                    course,
                    team);
                w.write(createResponse("TD", sentence.getSentenceId(), Arrays.asList(timestamp(), ls.getValue())));
                break;
              case "TDSTR":
              case "TDEND":
              case "TDHRT":
              case "TDOBS":
              case "TDDOC":
              case "TDPIN":
              case "TDUAV":
              default:
                w.write("** ILLEGAL TALKER ID in message (You should use 'SV' instead of 'TD'): " + sentence.toString());
                break;
              }
            } catch (IllegalArgumentException | WebApplicationException e) {
              if (debug.get()) {
                w.write("** ERROR: " + e.getMessage());
              }
              LOG.debug("NMEA Exception with message {}", line, e);
            }
            w.write("\n");
            w.flush();
          }
        }
      } catch (IOException e) {
        LOG.warn("IOException in NMEA", e);
      }
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("port", port).add("debug", debug.get()).add("abort", abort.get()).toString();
  }
}
