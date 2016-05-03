package com.felixpageau.roboboat.mission2015.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.DockingBay;
import com.felixpageau.roboboat.mission2015.structures.DockingSequence;
import com.felixpageau.roboboat.mission2015.structures.GateCode;
import com.felixpageau.roboboat.mission2015.structures.Pinger;
import com.felixpageau.roboboat.mission2015.structures.Shape;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class RunSetup {
  public static final RunSetup NO_RUN = new RunSetup("", Course.openTest, new TeamCode("NONE"), GateCode.generateRandomGateCode(),
      DockingSequence.generateRandomDockingSequence(), Pinger.NO_PINGER, Shape.generateRandomInteropShape());
  private final String runId;
  private final TeamCode activeTeam;
  private final Course course;
  private final GateCode activeGateCode;
  private final DockingSequence activeDockingSequence;
  private final Pinger activePinger;
  private final Shape activeInteropShape;

  @JsonCreator
  public RunSetup(@JsonProperty(value = "runId") String runId, @JsonProperty(value = "course") Course course,
      @JsonProperty(value = "activeTeam") TeamCode activeTeam, @JsonProperty(value = "activeGateCode") GateCode activeGateCode,
      @JsonProperty(value = "activeDockingSequence") DockingSequence activeDockingSequence, @JsonProperty(value = "activePinger") Pinger activePinger,
      @JsonProperty(value = "interopShape") Shape interopShape) {
    this.runId = Preconditions.checkNotNull(runId);
    this.course = Preconditions.checkNotNull(course);
    this.activeTeam = Preconditions.checkNotNull(activeTeam);
    this.activeGateCode = Preconditions.checkNotNull(activeGateCode);
    this.activeDockingSequence = Preconditions.checkNotNull(activeDockingSequence);
    this.activePinger = Preconditions.checkNotNull(activePinger);
    this.activeInteropShape = Preconditions.checkNotNull(interopShape);
  }

  public static RunSetup generateRandomSetup(CourseLayout courseLayout, TeamCode teamCode, String runId) {
    List<Pinger> pingers = courseLayout.getPingers();
    Pinger activePinger = new ArrayList<Pinger>(pingers).get(new Random().nextInt(pingers.size()));
    List<DockingBay> availableBays = new ArrayList<>(courseLayout.getDockingBays());
    List<DockingBay> bays = ImmutableList.of(availableBays.remove(new Random().nextInt(availableBays.size())),
        availableBays.remove(new Random().nextInt(availableBays.size())));
    return new RunSetup(runId, courseLayout.getCourse(), teamCode, GateCode.generateRandomGateCode(), new DockingSequence(bays), activePinger,
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
  public Pinger getActivePinger() {
    return activePinger;
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
        && Objects.equal(activePinger, other.activePinger) && Objects.equal(activeInteropShape, other.activeInteropShape);
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(runId, activeTeam, course, activeGateCode, activeDockingSequence, activePinger, activeInteropShape);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("runId", runId).add("course", course).add("activeTeam", activeTeam).add("activeGateCode", activeGateCode)
        .add("activeDockingBay", activeDockingSequence).add("activePinger", activePinger).add("activeInteropShape", activeInteropShape).toString();
  }
}
