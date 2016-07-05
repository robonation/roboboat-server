package com.felixpageau.roboboat.mission.server;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.DockingBay;
import com.felixpageau.roboboat.mission.structures.DockingSequence;
import com.felixpageau.roboboat.mission.structures.GateCode;
import com.felixpageau.roboboat.mission.structures.Pinger;
import com.felixpageau.roboboat.mission.structures.Run;
import com.felixpageau.roboboat.mission.structures.Shape;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Setup of a {@link Run}
 *
 */
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
@ReturnValuesAreNonNullByDefault
public class RunSetup {
  public static final RunSetup NO_RUN = new RunSetup("", Course.openTest, new TeamCode("NONE"), GateCode.generateRandomGateCode(),
      DockingSequence.generateRandomDockingSequence(), ImmutableSet.of(Pinger.NO_PINGER), Shape.generateRandomInteropShape());
  private final String runId;
  private final TeamCode activeTeam;
  private final Course course;
  private final GateCode activeGateCode;
  private final DockingSequence activeDockingSequence;
  private final Set<Pinger> activePingers;
  private final Shape activeInteropShape;

  @JsonCreator
  public RunSetup(@JsonProperty(value = "runId") String runId, @JsonProperty(value = "course") Course course,
      @JsonProperty(value = "activeTeam") TeamCode activeTeam, @JsonProperty(value = "activeGateCode") GateCode activeGateCode,
      @JsonProperty(value = "activeDockingSequence") DockingSequence activeDockingSequence, @JsonProperty(value = "activePinger") Set<Pinger> activePingers,
      @JsonProperty(value = "activeInteropShape") Shape activeInteropShape) {
    this.runId = Preconditions.checkNotNull(runId);
    this.course = Preconditions.checkNotNull(course);
    this.activeTeam = Preconditions.checkNotNull(activeTeam);
    this.activeGateCode = Preconditions.checkNotNull(activeGateCode);
    this.activeDockingSequence = Preconditions.checkNotNull(activeDockingSequence);
    this.activePingers = Preconditions.checkNotNull(activePingers);
    this.activeInteropShape = Preconditions.checkNotNull(activeInteropShape);
  }

  public static RunSetup generateRandomSetup(CourseLayout courseLayout, TeamCode teamCode, String runId) {
    List<Pinger> availablePingers = new ArrayList<>(courseLayout.getPingers());
    Set<Pinger> activePingers = ImmutableSet.of(availablePingers.remove(new SecureRandom().nextInt(availablePingers.size())),
        availablePingers.remove(new SecureRandom().nextInt(availablePingers.size())));
    List<DockingBay> availableBays = new ArrayList<>(courseLayout.getDockingBays());
    List<DockingBay> bays = ImmutableList.of(availableBays.remove(new SecureRandom().nextInt(availableBays.size())),
        availableBays.remove(new SecureRandom().nextInt(availableBays.size())));
    return new RunSetup(runId, courseLayout.getCourse(), teamCode, GateCode.generateRandomGateCode(), new DockingSequence(bays), activePingers,
        Shape.generateRandomInteropShape());
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
  public GateCode getActiveGateCode() {
    return activeGateCode;
  }

  /**
   * @return the activeDockingSequence
   */
  public DockingSequence getActiveDockingSequence() {
    return activeDockingSequence;
  }

  /**
   * @return the activePinger
   */
  public Set<Pinger> getActivePingers() {
    return activePingers;
  }

  /**
   * @return the runId
   */
  public String getRunId() {
    return runId;
  }

  /**
   * @return the activeLightSequence
   */
  public Shape getActiveInteropShape() {
    return activeInteropShape;
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
        && Objects.equal(activeGateCode, other.activeGateCode) && Objects.equal(activeDockingSequence, other.activeDockingSequence)
        && Objects.equal(activePingers, other.activePingers) && Objects.equal(activeInteropShape, other.activeInteropShape);
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(runId, activeTeam, course, activeGateCode, activeDockingSequence, activePingers, activeInteropShape);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("runId", runId).add("course", course).add("activeTeam", activeTeam).add("activeGateCode", activeGateCode)
        .add("activeDockingBay", activeDockingSequence).add("activePingers", activePingers).add("activeInteropShape", activeInteropShape).toString();
  }
}
