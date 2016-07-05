package com.felixpageau.roboboat.mission.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class BeaconReport {
  private final Course course;
  private final TeamCode team;
  private final BuoyColor buoyColor1;
  private final int frequency1;
  private final BuoyColor buoyColor2;
  private final int frequency2;

  @JsonCreator
  public BeaconReport(@JsonProperty(value = "course") Course course, @JsonProperty(value = "team") TeamCode team,
      @JsonProperty(value = "buoyColor1") BuoyColor buoyColor1, @JsonProperty(value = "frequency1") int frequency1,
      @JsonProperty(value = "buoyColor2") BuoyColor buoyColor2, @JsonProperty(value = "frequency2") int frequency2) {
    this.course = Preconditions.checkNotNull(course);
    this.team = Preconditions.checkNotNull(team);
    this.buoyColor1 = Preconditions.checkNotNull(buoyColor1);
    this.frequency1 = frequency1;
    this.buoyColor2 = Preconditions.checkNotNull(buoyColor2);
    this.frequency2 = frequency2;
  }

  public Course getCourse() {
    return course;
  }

  public TeamCode getTeam() {
    return team;
  }

  public BuoyColor getBuoyColor1() {
    return buoyColor1;
  }

  public BuoyColor getBuoyColor2() {
    return buoyColor2;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("course", course).add("team", team).add("buoyColor1", buoyColor1).add("buoyColor2", buoyColor2).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(course, team, buoyColor1, buoyColor2);
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
    if (obj instanceof BeaconReport) {
      BeaconReport other = (BeaconReport) obj;
      return Objects.equal(course, other.course) && Objects.equal(team, other.team) && Objects.equal(buoyColor1, other.buoyColor1)
          && Objects.equal(buoyColor2, other.buoyColor2);
    }
    return false;
  }

  public static void main(String[] args) {
    System.out.println(new BeaconReport(Course.courseA, new TeamCode("AUVSI"), BuoyColor.blue, 24, BuoyColor.red, 36));
  }
}
