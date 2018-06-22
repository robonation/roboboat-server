package com.felixpageau.roboboat.mission.server;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.DockingSequence;
import com.felixpageau.roboboat.mission.structures.LeaderSequence;
import com.felixpageau.roboboat.mission.structures.Run;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Setup of a {@link Run}
 *
 */
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
@ReturnValuesAreNonNullByDefault
public class RunSetup {
  public static final RunSetup NO_RUN = new RunSetup("", Course.openTest, new TeamCode("NONE"), LeaderSequence.none, DockingSequence.NONE);
  private final String runId;
  private final TeamCode activeTeam;
  private final Course course;
  private final LeaderSequence activeLeaderSequence;
  private final DockingSequence activeDockingSequence;

  @JsonCreator
  public RunSetup(@JsonProperty(value = "runId") String runId, @JsonProperty(value = "course") Course course,
      @JsonProperty(value = "activeTeam") TeamCode activeTeam, @JsonProperty(value = "activeLeaderSequence") LeaderSequence activeLeaderSequence,
      @JsonProperty(value = "activeDockingSequence") DockingSequence activeDockingSequence) {
    this.runId = Preconditions.checkNotNull(runId);
    this.course = Preconditions.checkNotNull(course);
    this.activeTeam = Preconditions.checkNotNull(activeTeam);
    this.activeLeaderSequence = Preconditions.checkNotNull(activeLeaderSequence);
    this.activeDockingSequence = Preconditions.checkNotNull(activeDockingSequence);
  }

  public static RunSetup generateRandomSetup(CourseLayout courseLayout, TeamCode teamCode, String runId) {
    Preconditions.checkNotNull(courseLayout, "courseLayout cannot be null");
    return new RunSetup(runId, courseLayout.getCourse(), teamCode, LeaderSequence.generateRandomLeaderSequence(), DockingSequence.generateRandomDockingSequence());
  }

  /**
   * @return the activeTeam
   */
  public TeamCode getActiveTeam() {
    return activeTeam;
  }

  /**
   * @return the course
   */
  public Course getCourse() {
    return course;
  }

  /**
   * @return the activeGateCode
   */
  public LeaderSequence getActiveLeaderSequence() {
    return activeLeaderSequence;
  }

  /**
   * @return the activeDockingSequence
   */
  public DockingSequence getActiveDockingSequence() {
    return activeDockingSequence;
  }

  /**
   * @return the runId
   */
  public String getRunId() {
    return runId;
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
    if (!(obj instanceof RunSetup)) {
      return false;
    }
    RunSetup other = (RunSetup) obj;

    return Objects.equal(runId, other.runId) && Objects.equal(activeTeam, other.activeTeam) && Objects.equal(course, other.course)
        && Objects.equal(activeLeaderSequence, other.activeLeaderSequence) && Objects.equal(activeDockingSequence, other.activeDockingSequence);
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(runId, activeTeam, course, activeLeaderSequence, activeDockingSequence);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("runId", runId).add("course", course).add("activeTeam", activeTeam).add("activeLeaderSequence", activeLeaderSequence)
        .add("activeDockingBay", activeDockingSequence).toString();
  }
}
