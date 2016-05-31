package com.felixpageau.roboboat.mission.structures;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission.server.RunArchiver;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
@ReturnValuesAreNonNullByDefault
public class DisplayReport {
  public static final DisplayReport NO_REPORT_A = buildNoReport(Course.courseA);
  public static final DisplayReport NO_REPORT_B = buildNoReport(Course.courseB);
  public static final DisplayReport NO_REPORT_OT = buildNoReport(Course.openTest);
  private final Course course;
  private final TeamCode teamCode;
  private final Position currentPosition;
  private final long lastHeartbeat;
  private final Challenge currentChallenge;
  private final GateCode assignedGateCode;
  private final boolean requestedGateCode;
  private final DockingSequence dockingSequence;
  private final boolean requestedDockingSequence;
  private final BuoyColor activePinger;
  private final BuoyColor reportedPinger;
  private final String uploadedImage;
  private final Shape activeShape;
  private final Shape reportedShape;

  @JsonCreator
  public DisplayReport(@JsonProperty(value = "course") Course course, @Nullable @JsonProperty(value = "teamCode") TeamCode teamCode,
      @Nullable @JsonProperty(value = "currentPosition") Position currentPosition, @JsonProperty(value = "lastHeartbeat") long lastHeartbeat,
      @JsonProperty(value = "currentChallenge") @Nullable Challenge currentChallenge,
      @Nullable @JsonProperty(value = "assignedGateCode") GateCode assignedGateCode, @JsonProperty(value = "requestedGateCode") boolean requestedGateCode,
      @Nullable @JsonProperty(value = "dockingSequence") DockingSequence dockingSequence,
      @JsonProperty(value = "requestedDockingSequence") boolean requestedDockingSequence,
      @Nullable @JsonProperty(value = "activePinger") BuoyColor activePinger, @Nullable @JsonProperty(value = "reportedPinger") BuoyColor reportedPinger,
      @Nullable @JsonProperty(value = "uploadedImage") String uploadedImage, @Nullable @JsonProperty(value = "activeShape") Shape activeShape,
      @Nullable @JsonProperty(value = "reportedShape") Shape reportedShape) {
    this.course = Preconditions.checkNotNull(course, "course cannot be null");
    this.teamCode = teamCode;
    this.currentPosition = currentPosition;
    this.lastHeartbeat = lastHeartbeat;
    this.currentChallenge = currentChallenge;
    this.assignedGateCode = assignedGateCode;
    this.requestedGateCode = requestedGateCode;
    this.dockingSequence = dockingSequence;
    this.requestedDockingSequence = requestedDockingSequence;
    this.activePinger = activePinger;
    this.reportedPinger = reportedPinger;
    this.uploadedImage = uploadedImage;
    this.activeShape = activeShape;
    this.reportedShape = reportedShape;
  }

  /**
   * @param course
   *          the course which to create a *no report* {@link DisplayReport}
   * @return a {@link DisplayReport} instance
   */
  private static final DisplayReport buildNoReport(Course course) {
    Preconditions.checkNotNull(course, "course cannot be null");
    return new DisplayReport(course, null, null, 0, null, null, false, null, false, null, null, null, null, null);
  }

  /**
   * Build a {@link DisplayReport} based on a {@link RunArchiver} instance.
   * 
   * @param course
   *          the course for which a {@link DisplayReport} should be generated
   * @param archiver
   *          the {@link RunArchiver} instance instance. May be null
   * @return an instance of {@link DisplayReport}
   */
  public static DisplayReport buildDisplayReport(Course course, @Nullable RunArchiver archiver) {
    Preconditions.checkNotNull(course, "course cannot be null");
    if (archiver == null) {
      return DisplayReport.buildNoReport(course);
    }
    return new DisplayReport(archiver.getRunSetup().getCourse(), archiver.getRunSetup().getActiveTeam(), archiver.getLastHeartbeat()
        .map(HeartbeatReport::getPosition).orElse(null), archiver.getLastHeartbeat().map(hr -> hr.getTimestamp().getTimeAsLong()).orElse(0L), archiver
        .getLastHeartbeat().map(HeartbeatReport::getChallenge).orElse(null), archiver.getRunSetup().getActiveGateCode(), archiver.hasRequestedGateCode(),
        archiver.getRunSetup().getActiveDockingSequence(), archiver.hasRequestedDockingSequence(), archiver.getRunSetup().getActivePinger().getBuoyColor(),
        archiver.getReportedPinger().map(BeaconReport::getBuoyColor).orElse(null), archiver.getUploadedImage().orElse(null), archiver.getRunSetup()
            .getActiveInteropShape(), archiver.getReportedInterop().map(InteropReport::getShape).orElse(null));
  }

  /**
   * @return the course
   */
  public Course getCourse() {
    return course;
  }

  /**
   * @return the teamCode
   */
  @CheckForNull
  public TeamCode getTeamCode() {
    return teamCode;
  }

  /**
   * @return the currentPosition
   */
  @CheckForNull
  public Position getCurrentPosition() {
    return currentPosition;
  }

  /**
   * @return the lastHeartbeat
   */
  public long getLastHeartbeat() {
    return lastHeartbeat;
  }

  /**
   * @return the currentChallenge
   */
  @CheckForNull
  public Challenge getCurrentChallenge() {
    return currentChallenge;
  }

  /**
   * @return the assignedGateCode
   */
  @CheckForNull
  public GateCode getAssignedGateCode() {
    return assignedGateCode;
  }

  /**
   * @return the requestedGateCode
   */
  public boolean isRequestedGateCode() {
    return requestedGateCode;
  }

  /**
   * @return the dockingSequence
   */
  @CheckForNull
  public DockingSequence getDockingSequence() {
    return dockingSequence;
  }

  /**
   * @return the requestedDockingSequence
   */
  public boolean isRequestedDockingSequence() {
    return requestedDockingSequence;
  }

  /**
   * @return the activePinger
   */
  @CheckForNull
  public BuoyColor getActivePinger() {
    return activePinger;
  }

  /**
   * @return the reportedPinger
   */
  @CheckForNull
  public BuoyColor getReportedPinger() {
    return reportedPinger;
  }

  /**
   * @return the uploadedImage
   */
  @CheckForNull
  public String getUploadedImage() {
    return uploadedImage;
  }

  /**
   * @return the activeShape
   */
  @CheckForNull
  public Shape getActiveShape() {
    return activeShape;
  }

  /**
   * @return the reportedShape
   */
  @CheckForNull
  public Shape getReportedShape() {
    return reportedShape;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("course", course).add("teamCode", teamCode).add("currentPosition", currentPosition)
        .add("lastHeartbeat", lastHeartbeat).add("currentChallenge", currentChallenge).add("assignedGateCode", assignedGateCode)
        .add("requestedGateCode", requestedGateCode).add("dockingSequence", dockingSequence).add("requestedDockingSequence", requestedDockingSequence)
        .add("activePinger", activePinger).add("reportedPinger", reportedPinger).add("activeShape", activeShape).add("reportedShape", reportedShape).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(course, teamCode, currentPosition, lastHeartbeat, currentChallenge, assignedGateCode, requestedGateCode, dockingSequence,
        requestedDockingSequence, activePinger, reportedPinger, activeShape, reportedShape);
  }

  @JsonIgnore
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj instanceof DisplayReport) {
      DisplayReport other = (DisplayReport) obj;
      return Objects.equal(course, other.course) && Objects.equal(teamCode, other.teamCode) && Objects.equal(currentPosition, other.currentPosition)
          && Objects.equal(lastHeartbeat, other.lastHeartbeat) && Objects.equal(currentChallenge, other.currentChallenge)
          && Objects.equal(assignedGateCode, other.assignedGateCode) && Objects.equal(requestedGateCode, other.requestedGateCode)
          && Objects.equal(dockingSequence, other.dockingSequence) && Objects.equal(requestedDockingSequence, other.requestedDockingSequence)
          && Objects.equal(activePinger, other.activePinger) && Objects.equal(reportedPinger, other.reportedPinger)
          && Objects.equal(activeShape, other.activeShape) && Objects.equal(reportedShape, other.reportedShape);
    }
    return false;
  }
}
