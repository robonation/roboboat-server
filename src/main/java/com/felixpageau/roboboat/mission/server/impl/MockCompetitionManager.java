/**
 * 
 */
package com.felixpageau.roboboat.mission.server.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felixpageau.roboboat.mission.WebApplicationExceptionWithContext;
import com.felixpageau.roboboat.mission.server.Competition;
import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.server.RunArchiver;
import com.felixpageau.roboboat.mission.server.RunSetup;
import com.felixpageau.roboboat.mission.server.StructuredEvent;
import com.felixpageau.roboboat.mission.server.TimeSlot;
import com.felixpageau.roboboat.mission.structures.Challenge;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.DisplayReport;
import com.felixpageau.roboboat.mission.structures.DisplayStatus;
import com.felixpageau.roboboat.mission.structures.DockingSequence;
import com.felixpageau.roboboat.mission.structures.HeartbeatReport;
import com.felixpageau.roboboat.mission.structures.ImageUploadDescriptor;
import com.felixpageau.roboboat.mission.structures.LeaderSequence;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.felixpageau.roboboat.mission.structures.Timestamp;
import com.felixpageau.roboboat.mission.structures.UploadStatus;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

/**
 * @author felixpageau
 *
 */
@ThreadSafe
@Immutable
@ParametersAreNonnullByDefault
public class MockCompetitionManager implements CompetitionManager {
  private static final Logger LOG = LoggerFactory.getLogger(MockCompetitionManager.class);
  protected final ObjectMapper om;
  protected final Competition competition;
  protected final File basePath;

  public MockCompetitionManager(Competition competition, ObjectMapper om) {
    this(competition, om, new File("/etc/roboboat/roboboat2018-images/uploads/" + DateTimeFormatter.ofPattern("YYYYMMdd/").format(LocalDateTime.now())));
  }

  protected MockCompetitionManager(Competition competition, ObjectMapper om, final File basePath) {
    this.competition = Preconditions.checkNotNull(competition, "competition cannot be null");
    this.om = Preconditions.checkNotNull(om, "om cannot be null");
    this.basePath = Preconditions.checkNotNull(basePath);
    Preconditions.checkArgument(basePath.exists() || basePath.mkdirs(), "Could not create directory: " + basePath);
  }

  @Override
  public Competition getCompetition() {
    return competition;
  }

  @Override
  public ReportStatus startRun(Course course, TeamCode teamCode) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive != null) {
      throw new WebApplicationExceptionWithContext(String.format("There is already a run active on course %s! Try doing a POST against /run/endRun/%s/%s",
          course, course, teamCode), 400);
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
      throw new WebApplicationExceptionWithContext(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode,
          course), 400);
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
      throw new WebApplicationExceptionWithContext(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode),
          400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationExceptionWithContext(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode,
          course), 400);
    }
    boolean success = true;// report.getShape().equals(archive.getRunSetup().getActiveInteropShape());
    archive.addHeartbeatEvent(new StructuredEvent(course, teamCode, report.getChallenge(), "Heartbeat report"));
    archive.setLastHeartbeat(report);
    return new ReportStatus(success);
  }

  @Override
  public DockingSequence getDockingSequence(Course course, TeamCode teamCode) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive == null) {
      throw new WebApplicationExceptionWithContext(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode),
          400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationExceptionWithContext(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode,
          course), 400);
    }
    DockingSequence sequence = archive.getRunSetup().getActiveDockingSequence();
    archive.addEvent(new StructuredEvent(course, teamCode, Challenge.docking, String.format("request bay (%s)", sequence)));
    archive.requestedCarouselCode();
    return sequence;
  }

  @Override
  public LeaderSequence getLeaderSequence(Course course, TeamCode teamCode) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive == null) {
      throw new WebApplicationExceptionWithContext(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode),
          400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationExceptionWithContext(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode,
          course), 400);
    }
    
    LeaderSequence sequence = archive.getRunSetup().getActiveLeaderSequence();
    archive.addEvent(new StructuredEvent(course, teamCode, Challenge.docking, String.format("request bay (%s)", sequence)));
    archive.requestedCarouselCode();
    return sequence;
  }

  @Override
  public UploadStatus uploadDockingImage(Course course, TeamCode teamCode, byte[] content) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive == null) {
      throw new WebApplicationExceptionWithContext(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode),
          400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationExceptionWithContext(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode,
          course), 400);
    }
    UUID imageId = UUID.randomUUID();
    try {
      File f = new File(basePath, imageId.toString() + getExtension(content));
      ImageUploadDescriptor iud = new ImageUploadDescriptor(course, teamCode, new Timestamp(), imageId.toString(), f.getName());
      File fDesc = new File(basePath, imageId.toString() + ".desc");
      if (!f.createNewFile()) {
        LOG.warn("Selected UUID already exist: {}", imageId);
        throw new RuntimeException(String.format("Selected UUID already exist: %s", imageId));
      }

      try (OutputStream os = new BufferedOutputStream(new FileOutputStream(f)); OutputStream oDesc = new BufferedOutputStream(new FileOutputStream(fDesc))) {
        os.write(content);
        oDesc.write(om.writeValueAsBytes(iud));
      }
    } catch (IOException e) {
      LOG.warn("IOExcepton while uploading an image for team {} on course {}", teamCode, course, e);
    }

    archive.addEvent(new StructuredEvent(course, teamCode, Challenge.docking, String.format("uploaded image (%s)", imageId)));
    archive.uploadedImage(imageId.toString());
    return new UploadStatus(imageId.toString());
  }

  @Nonnull
  @Override
  public Optional<byte[]> getUploadedImage(String imageId) {
    try {
      File f = new File(basePath, imageId + ".jpg");
      if (!f.exists()) {
        f = new File(basePath, imageId + ".png");
      }
      if (!f.exists()) {
        return Optional.empty();
      }

      try (InputStream is = new BufferedInputStream(new FileInputStream(f))) {
        return Optional.ofNullable(ByteStreams.toByteArray(is));
      }
    } catch (IOException e) {
      LOG.warn("IOExcepton while accessing image {}", imageId, e);
      return Optional.empty();
    }
  }

  @Override
  public DisplayStatus getDisplayStatus(List<Course> courses) {
    Preconditions.checkNotNull(courses, "courses cannot be null");
    return new DisplayStatus(courses.stream().collect(
        Collectors.toMap(Function.identity(), c -> DisplayReport.buildDisplayReport(c, competition.getActiveRun(c)))));
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(om, competition, basePath);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null) return false;
    if (!(obj instanceof MockCompetitionManager)) return false;
    MockCompetitionManager other = (MockCompetitionManager) obj;
    return Objects.equal(om, other.om) && Objects.equal(competition, other.competition) && Objects.equal(basePath, other.basePath);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("om", om).add("competition", competition).add("basePath", basePath).toString();
  }
}
