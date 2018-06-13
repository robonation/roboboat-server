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

/**
 * Represent information to show on the course display
 */
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
  private final LeaderSequence assignedLeaderSequence;
  private final boolean requestedCarouselCode;
  private final DockingSequence dockingSequence;
  private final String uploadedImage;

  @JsonCreator
  public DisplayReport(
      @JsonProperty(value = "course") Course course, 
      @Nullable @JsonProperty(value = "teamCode") TeamCode teamCode,
      @Nullable @JsonProperty(value = "currentPosition") Position currentPosition, 
      @JsonProperty(value = "lastHeartbeat") long lastHeartbeat,
      @Nullable @JsonProperty(value = "currentChallenge") Challenge currentChallenge,
      @Nullable @JsonProperty(value = "assignedLeaderSequence") LeaderSequence assignedLeaderSequence, 
      @JsonProperty(value = "requestedCarouselCode") boolean requestedCarouselCode,
      @Nullable @JsonProperty(value = "dockingSequence") DockingSequence dockingSequence,
      @Nullable @JsonProperty(value = "uploadedImage") String uploadedImage) {
    this.course = Preconditions.checkNotNull(course, "course cannot be null");
    this.teamCode = teamCode;
    this.currentPosition = currentPosition;
    this.lastHeartbeat = lastHeartbeat;
    this.currentChallenge = currentChallenge;
    this.assignedLeaderSequence = assignedLeaderSequence;
    this.requestedCarouselCode = requestedCarouselCode;
    this.dockingSequence = dockingSequence;
    this.uploadedImage = uploadedImage;
  }

  /**
   * @param course
   *          the course which to create a *no report* {@link DisplayReport}
   * @return a {@link DisplayReport} instance
   */
  private static final DisplayReport buildNoReport(Course course) {
    Preconditions.checkNotNull(course, "course cannot be null");
    return new DisplayReport(course, null, null, 0, null, null, false, null, null);
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
        .getLastHeartbeat().map(HeartbeatReport::getChallenge).orElse(null), archiver.getRunSetup().getActiveLeaderSequence(), archiver.hasRequestedCarouselCode(),
        archiver.getRunSetup().getActiveDockingSequence(), archiver.getUploadedImage().orElse(null));
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
  public LeaderSequence getAssignedLeaderSequence() {
    return assignedLeaderSequence;
  }

  /**
   * @return the dockingSequence
   */
  @CheckForNull
  public DockingSequence getDockingSequence() {
    return dockingSequence;
  }

  /**
   * @return the uploadedImage
   */
  @CheckForNull
  public String getUploadedImage() {
    return uploadedImage;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("course", course).add("teamCode", teamCode).add("currentPosition", currentPosition)
        .add("lastHeartbeat", lastHeartbeat).add("currentChallenge", currentChallenge).add("assignedLeaderSequence", assignedLeaderSequence)
        .add("requestedCarouselCode", requestedCarouselCode).add("dockingSequence", dockingSequence)
        .toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(course, teamCode, currentPosition, lastHeartbeat, currentChallenge, assignedLeaderSequence, requestedCarouselCode, dockingSequence);
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
          && Objects.equal(assignedLeaderSequence, other.assignedLeaderSequence)
          && Objects.equal(requestedCarouselCode, other.requestedCarouselCode)
          && Objects.equal(dockingSequence, other.dockingSequence);
    }
    return false;
  }
}
