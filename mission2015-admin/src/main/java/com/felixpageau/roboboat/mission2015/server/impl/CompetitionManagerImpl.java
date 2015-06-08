/**
 * 
 */
package com.felixpageau.roboboat.mission2015.server.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.felixpageau.roboboat.mission2015.server.Competition;
import com.felixpageau.roboboat.mission2015.server.CompetitionManager;
import com.felixpageau.roboboat.mission2015.server.Event;
import com.felixpageau.roboboat.mission2015.server.Pinger;
import com.felixpageau.roboboat.mission2015.server.RunArchiver;
import com.felixpageau.roboboat.mission2015.server.RunSetup;
import com.felixpageau.roboboat.mission2015.server.TimeSlot;
import com.felixpageau.roboboat.mission2015.structures.BeaconReport;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.DockingSequence;
import com.felixpageau.roboboat.mission2015.structures.GateCode;
import com.felixpageau.roboboat.mission2015.structures.InteropReport;
import com.felixpageau.roboboat.mission2015.structures.ReportStatus;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.felixpageau.roboboat.mission2015.structures.UploadStatus;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

/**
 * @author felixpageau
 *
 */
@ThreadSafe
@Immutable
@ParametersAreNonnullByDefault
public class CompetitionManagerImpl implements CompetitionManager {
  private final Competition competition;
  private final File basePath;
  private final File sourcePath;
  private final LoadingCache<String, byte[]> fileCache = CacheBuilder.newBuilder().build(new CacheLoader<String, byte[]>() {
    @Override
    public byte[] load(String key) throws Exception {
      return Files.toByteArray(sourcePath.toPath().resolve(key).toFile());
    }
  });

  public CompetitionManagerImpl(Competition competition) throws URISyntaxException, MalformedURLException {
    this.competition = Preconditions.checkNotNull(competition);
    this.basePath = new File("/tmp/roboboat2015-images/uploads/" + DateTimeFormatter.ofPattern("YYYYMMdd/").format(LocalDateTime.now()));
    URL url = Optional.ofNullable(CompetitionManagerImpl.class.getResource("/roboboat2015-images/source/")).orElse(
        new File("roboboat2015-images/source/").toURL());
    this.sourcePath = new File(url.toURI());
    Preconditions.checkArgument(basePath.exists() || basePath.mkdirs(), "Could not create directory: " + basePath);
    Preconditions.checkArgument(sourcePath.exists(), "Could not find source image directory: " + sourcePath.getAbsolutePath());
    for (String file : sourcePath.list()) {
      System.out.println("Loading source image: " + file);
      try {
        fileCache.get(file);
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public Competition getCompetition() {
    return competition;
  }

  @Override
  public ReportStatus startRun(Course course, TeamCode teamCode) {
    TimeSlot slot = competition.findCurrentTimeSlot(course);
    RunSetup r = competition.startNewRun(slot, teamCode);
    competition.getActiveRuns().get(course).addEvent(new Event(String.format("%s - %s - Run started - new setup (%s)", course, teamCode, r)));
    return new ReportStatus(true);
  }

  @Override
  public ReportStatus endRun(Course course, TeamCode teamCode) {
    competition.endRun(course, teamCode);
    competition.getActiveRuns().get(course).addEvent(new Event(String.format("%s - %s - Run ended", course, teamCode)));
    return new ReportStatus(true);
  }

  @Override
  public ReportStatus reportHeartbeat(Course course, TeamCode teamCode) {
    // TODO Handle this
    return new ReportStatus(true);
  }

  @Override
  public GateCode getObstacleCourseCode(Course course, TeamCode teamCode) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    GateCode code = archive.getRunSetup().getActiveGateCode();
    competition.getActiveRuns().get(course).addEvent(new Event(String.format("%s - %s - ObstacleAvoidance - request gatecode (%s)", course, teamCode, code)));
    return code;
  }

  @Override
  public DockingSequence getDockingSequence(Course course, TeamCode teamCode) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    DockingSequence sequence = archive.getRunSetup().getActiveDockingSequence();
    competition.getActiveRuns().get(course).addEvent(new Event(String.format("%s - %s - AutomatedDocking - request bay (%s)", course, teamCode, sequence)));
    return sequence;
  }

  @Override
  public ReportStatus reportPinger(Course course, TeamCode teamCode, BeaconReport payload) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    Pinger reportedPinger = new Pinger(payload.getBuoyColor());
    boolean success = reportedPinger.getBuoyColor().equals(archive.getRunSetup().getActivePinger().getBuoyColor());
    Event e = new Event(String.format("%s - %s - ObstacleAvoidance - reported pinger (%s) -> %s", course, teamCode, payload, success ? "success" : "incorrect"));
    competition.getActiveRuns().get(course).addEvent(e);
    return new ReportStatus(success);
  }

  @Override
  public ReportStatus reportInterop(Course course, TeamCode teamCode, InteropReport report) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    boolean success = report.getShape().equals(archive.getRunSetup().getActiveInteropShape());
    Event e = new Event(String.format("%s - %s - Interop - reported shape (%s) -> %s", course, teamCode, report, success ? "success" : "incorrect"));
    competition.getActiveRuns().get(course).addEvent(e);
    return new ReportStatus(success);
  }

  @Override
  public List<String> listInteropImages(Course course, TeamCode teamCode) {
    Event e = new Event(String.format("%s - %s - Interop - listed image", course, teamCode));
    competition.getActiveRuns().get(course).addEvent(e);
    return ImmutableList.copyOf(fileCache.asMap().keySet());
  }

  @Override
  public Optional<byte[]> getInteropImage(String filename) {
    Event e = new Event(String.format("? - ? - Interop - requested image %s", filename));
    competition.getActiveRuns().get(Course.courseA).addEvent(e);
    return Optional.ofNullable(fileCache.getIfPresent(filename));
  }

  @Override
  public UploadStatus uploadInteropImage(Course course, TeamCode teamCode, byte[] content) {
    RunArchiver ra = competition.getActiveRuns().get(course);
    UUID imageId = UUID.randomUUID();

    try {
      File f = new File(basePath, imageId.toString() + getExtension(content));
      f.createNewFile();

      try (OutputStream os = new BufferedOutputStream(new FileOutputStream(f))) {
        os.write(content);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    ra.addEvent(new Event(String.format("%s - %s - Interop - uploaded image (%s)", course, teamCode, imageId)));
    return new UploadStatus(imageId.toString());
  }
}
