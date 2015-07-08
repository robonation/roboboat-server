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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import javax.ws.rs.WebApplicationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felixpageau.roboboat.mission2015.server.Competition;
import com.felixpageau.roboboat.mission2015.server.CompetitionManager;
import com.felixpageau.roboboat.mission2015.server.RunArchiver;
import com.felixpageau.roboboat.mission2015.server.RunSetup;
import com.felixpageau.roboboat.mission2015.server.StructuredEvent;
import com.felixpageau.roboboat.mission2015.server.TimeSlot;
import com.felixpageau.roboboat.mission2015.structures.BeaconReport;
import com.felixpageau.roboboat.mission2015.structures.Challenge;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.DisplayReport;
import com.felixpageau.roboboat.mission2015.structures.DisplayStatus;
import com.felixpageau.roboboat.mission2015.structures.DockingSequence;
import com.felixpageau.roboboat.mission2015.structures.GateCode;
import com.felixpageau.roboboat.mission2015.structures.HeartbeatReport;
import com.felixpageau.roboboat.mission2015.structures.ImageUploadDescriptor;
import com.felixpageau.roboboat.mission2015.structures.InteropReport;
import com.felixpageau.roboboat.mission2015.structures.Pinger;
import com.felixpageau.roboboat.mission2015.structures.ReportStatus;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.felixpageau.roboboat.mission2015.structures.Timestamp;
import com.felixpageau.roboboat.mission2015.structures.UploadStatus;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

/**
 * @author felixpageau
 *
 */
@ThreadSafe
@Immutable
@ParametersAreNonnullByDefault
public class CompetitionManagerImpl implements CompetitionManager {
  // TODO Find a way to plumb the jersey instance
  private final ObjectMapper om = new ObjectMapper();
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
    this.sourcePath = new File(new File("/tmp/roboboat2015-images/source/").toURL().toURI());
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
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive != null) {
      throw new WebApplicationException(String.format("There is already a run active on course %s! Try doing a POST against /run/endRun/%s/%s", course, course,
          teamCode), 400);
    }
    TimeSlot slot = competition.findCurrentTimeSlot(course);
    RunSetup r = competition.startNewRun(slot, teamCode);
    competition.getActiveRuns().get(course).addEvent(new StructuredEvent(course, teamCode, Challenge.none, String.format("Run started - new setup (%s)", r)));
    return new ReportStatus(true);
  }

  @Override
  public ReportStatus endRun(Course course, TeamCode teamCode) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive != null && !archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationException(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode, course), 400);
    }
    if (archive != null && archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      competition.endRun(course, teamCode);
      archive.addEvent(new StructuredEvent(course, teamCode, Challenge.none, "Run ended"));
    }
    return new ReportStatus(true);
  }

  @Override
  public ReportStatus reportHeartbeat(Course course, TeamCode teamCode, HeartbeatReport report) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive == null) {
      throw new WebApplicationException(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode), 400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationException(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode, course), 400);
    }
    boolean success = true;// report.getShape().equals(archive.getRunSetup().getActiveInteropShape());
    archive.addHeartbeatEvent(new StructuredEvent(course, teamCode, report.getChallenge(), "Heartbeat report"));
    archive.setLastHeartbeat(report);
    return new ReportStatus(success);
  }

  @Override
  public GateCode getObstacleCourseCode(Course course, TeamCode teamCode) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive == null) {
      throw new WebApplicationException(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode), 400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationException(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode, course), 400);
    }
    GateCode code = archive.getRunSetup().getActiveGateCode();
    archive.addEvent(new StructuredEvent(course, teamCode, Challenge.obstacles, String.format("request gatecode (%s)", code)));
    archive.requestedGateCode();
    return code;
  }

  @Override
  public DockingSequence getDockingSequence(Course course, TeamCode teamCode) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive == null) {
      throw new WebApplicationException(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode), 400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationException(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode, course), 400);
    }
    DockingSequence sequence = archive.getRunSetup().getActiveDockingSequence();
    archive.addEvent(new StructuredEvent(course, teamCode, Challenge.docking, String.format("request bay (%s)", sequence)));
    archive.requestedDockingSequence();
    return sequence;
  }

  @Override
  public ReportStatus reportPinger(Course course, TeamCode teamCode, BeaconReport payload) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive == null) {
      throw new WebApplicationException(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode), 400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationException(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode, course), 400);
    }
    Pinger reportedPinger = new Pinger(payload.getBuoyColor());
    boolean success = reportedPinger.getBuoyColor().equals(archive.getRunSetup().getActivePinger().getBuoyColor());
    archive.addEvent(new StructuredEvent(course, teamCode, Challenge.pinger, String.format("reported pinger (%s) -> %s", payload, success ? "success"
        : "incorrect")));
    archive.reportPinger(payload);
    return new ReportStatus(success);
  }

  @Override
  public ReportStatus reportInterop(Course course, TeamCode teamCode, InteropReport report) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive == null) {
      throw new WebApplicationException(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode), 400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationException(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode, course), 400);
    }
    boolean success = report.getShape().equals(archive.getRunSetup().getActiveInteropShape());
    archive.addEvent(new StructuredEvent(course, teamCode, Challenge.interop, String.format("reported shape (%s) -> %s", report, success ? "success"
        : "incorrect")));
    archive.reportInterop(report);
    return new ReportStatus(success);
  }

  @Override
  public List<String> listInteropImages(Course course, TeamCode teamCode) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive == null) {
      throw new WebApplicationException(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode), 400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationException(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode, course), 400);
    }
    archive.addEvent(new StructuredEvent(course, teamCode, Challenge.interop, "listed image"));
    archive.requestedImageListing();
    return ImmutableList.copyOf(fileCache.asMap().keySet());
  }

  @Override
  public Optional<byte[]> getInteropImage(Course course, TeamCode teamCode, String filename) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive == null) {
      throw new WebApplicationException(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode), 400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationException(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode, course), 400);
    }
    archive.addEvent(new StructuredEvent(course, teamCode, Challenge.interop, String.format("requested image %s", filename)));
    archive.requestedImage();
    return Optional.ofNullable(fileCache.getIfPresent(filename));
  }

  @Override
  public UploadStatus uploadInteropImage(Course course, TeamCode teamCode, byte[] content) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive == null) {
      throw new WebApplicationException(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode), 400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationException(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode, course), 400);
    }
    UUID imageId = UUID.randomUUID();

    String path = null;
    try {
      File f = new File(basePath, imageId.toString() + getExtension(content));
      f.createNewFile();
      path = f.getAbsolutePath();
      ImageUploadDescriptor iud = new ImageUploadDescriptor(course, teamCode, new Timestamp(), imageId.toString(), f.getName());
      File fDesc = new File(basePath, imageId.toString() + ".desc");
      f.createNewFile();

      try (OutputStream os = new BufferedOutputStream(new FileOutputStream(f)); OutputStream oDesc = new BufferedOutputStream(new FileOutputStream(fDesc))) {
        os.write(content);
        oDesc.write(om.writeValueAsBytes(iud));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    archive.addEvent(new StructuredEvent(course, teamCode, Challenge.interop, String.format("uploaded image (%s)", imageId)));
    archive.uploadedImage(path);
    return new UploadStatus(imageId.toString());
  }

  @Override
  public DisplayStatus getDisplayStatus() {
    RunArchiver raA = competition.getActiveRun(Course.courseA);
    RunArchiver raB = competition.getActiveRun(Course.courseB);

    DisplayReport reportA = new DisplayReport(
        raA.getRunSetup().getCourse(), 
        raA.getRunSetup().getActiveTeam(), 
        raA.getLastHeartbeat().map(HeartbeatReport::getPosition).orElse(null), 
        raA.getLastHeartbeat().map(hr -> hr.getTimestamp().getTimeAsLong()).orElse(null), 
        raA.getLastHeartbeat().map(HeartbeatReport::getChallenge).orElse(null), 
        raA.getRunSetup().getActiveGateCode(), 
        raA.hasRequestedGateCode(), 
        raA.getRunSetup().getActiveDockingSequence(), 
        raA.hasRequestedDockingSequence(),
        raA.getRunSetup().getActivePinger().getBuoyColor(), 
        raA.getReportedPinger().map(BeaconReport::getBuoyColor).orElse(null),
        raA.hasRequestedImageListing(),
        raA.hasRequestedImage(),
        raA.getUploadedImage().orElse(null),
        raA.getReportedInterop().map(InteropReport::getShape).orElse(null),
        raA.getRunSetup().getActiveInteropShape());
    DisplayReport reportB = new DisplayReport(
        raB.getRunSetup().getCourse(), 
        raB.getRunSetup().getActiveTeam(), 
        raB.getLastHeartbeat().map(HeartbeatReport::getPosition).orElse(null), 
        raB.getLastHeartbeat().map(hr -> hr.getTimestamp().getTimeAsLong()).orElse(null), 
        raB.getLastHeartbeat().map(HeartbeatReport::getChallenge).orElse(null), 
        raB.getRunSetup().getActiveGateCode(), 
        raB.hasRequestedGateCode(), 
        raB.getRunSetup().getActiveDockingSequence(), 
        raB.hasRequestedDockingSequence(),
        raB.getRunSetup().getActivePinger().getBuoyColor(), 
        raB.getReportedPinger().map(BeaconReport::getBuoyColor).orElse(null),
        raB.hasRequestedImageListing(),
        raB.hasRequestedImage(),
        raB.getUploadedImage().orElse(null),
        raB.getReportedInterop().map(InteropReport::getShape).orElse(null),
        raB.getRunSetup().getActiveInteropShape());

    return new DisplayStatus(ImmutableMap.of(Course.courseA, reportA, Course.courseB, reportB));
  }
}
