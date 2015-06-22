package com.felixpageau.roboboat.mission2015;

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

import com.felixpageau.roboboat.mission2015.nmea.NMEASentence;
import com.felixpageau.roboboat.mission2015.nmea.SentenceDefinition;
import com.felixpageau.roboboat.mission2015.nmea.SentenceRegistry;
import com.felixpageau.roboboat.mission2015.server.CompetitionManager;
import com.felixpageau.roboboat.mission2015.server.Config;
import com.felixpageau.roboboat.mission2015.structures.BeaconReport;
import com.felixpageau.roboboat.mission2015.structures.BuoyColor;
import com.felixpageau.roboboat.mission2015.structures.Challenge;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.DockingSequence;
import com.felixpageau.roboboat.mission2015.structures.GateCode;
import com.felixpageau.roboboat.mission2015.structures.HeartbeatReport;
import com.felixpageau.roboboat.mission2015.structures.InteropReport;
import com.felixpageau.roboboat.mission2015.structures.Position;
import com.felixpageau.roboboat.mission2015.structures.ReportStatus;
import com.felixpageau.roboboat.mission2015.structures.Shape;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.felixpageau.roboboat.mission2015.structures.Timestamp;
import com.google.common.base.Preconditions;

@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public class NMEAServer implements Runnable {
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
      e.printStackTrace();
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
      return seq.getDockingBaySequence().stream().flatMap(b -> Arrays.asList(b.getSymbol().toString(), b.getColor().toString()).stream())
          .collect(Collectors.toList());
    }

    public String createResponse(String talkerId, String sentenceId, List<String> fields) {
      Preconditions.checkNotNull(talkerId, "talkerId cannot be null");
      Preconditions.checkNotNull(sentenceId, "sentenceId cannot be null");
      Preconditions.checkNotNull(fields, "fields cannot be null");
      SentenceDefinition sd = registry.getDefinition(talkerId, sentenceId);
      return new NMEASentence(sentenceId, talkerId, sd, fields).toString();
    }

    @Override
    public void run() {
      try (BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
          BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
        while (!abort.get()) {
          String line = r.readLine();
          if (line == null || line.isEmpty() || line.equals("\n")) {
            // System.err.println("Read empty line");
            continue;
          } else {
            try {
              if (debug.get()) {
                System.out.println("** SERVER READ: " + line);
              }
              NMEASentence sentence = NMEASentence.parse(registry, line);
              ReportStatus status;
              Course course = Course.fromString(sentence.getFields().get(0));
              TeamCode team = new TeamCode(sentence.getFields().get(1));
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
                Challenge c = Challenge.fromString(sentence.getFields().get(3));
                Position p = Position.fromNMEA(sentence.getFields().get(4), sentence.getFields().get(5));
                status = competitionManager.reportHeartbeat(course, team, new HeartbeatReport(new Timestamp(), c, p));
                w.write(createResponse("TD", sentence.getSentenceId(), Arrays.asList(timestamp(), Boolean.toString(status.isSuccess()))));
                break;
              case "SVOBS":
                GateCode code = competitionManager.getObstacleCourseCode(course, team);
                w.write(createResponse("TD", sentence.getSentenceId(), Arrays.asList(timestamp(), Integer.toString(code.getEntrance()), code.getExit())));
                break;
              case "SVDOC":
                DockingSequence seq = competitionManager.getDockingSequence(course, team);
                List<String> args = new ArrayList<>();
                args.add(timestamp());
                args.addAll(baySequence(seq));
                w.write(createResponse("TD", sentence.getSentenceId(), args));
                break;
              case "SVPIN":
                status = competitionManager.reportPinger(course, team, new BeaconReport(course, team, BuoyColor.valueOf(sentence.getFields().get(2))));
                w.write(createResponse("TD", sentence.getSentenceId(), Arrays.asList(timestamp(), Boolean.toString(status.isSuccess()))));
                break;
              case "SVUAV":
                InteropReport ir = new InteropReport(course, team, Shape.fromString(sentence.getFields().get(2)), sentence.getFields().get(3));
                status = competitionManager.reportInterop(course, team, ir);
                w.write(createResponse("TD", sentence.getSentenceId(), Arrays.asList(timestamp(), Boolean.toString(status.isSuccess()))));
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
            } catch (IllegalArgumentException e) {
              if (debug.get()) {
                w.write("** ERROR: " + e.getMessage());
              }
            }
            w.write("\n");
            w.flush();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
