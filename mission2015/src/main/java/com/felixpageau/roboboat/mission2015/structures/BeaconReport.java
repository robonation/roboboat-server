package com.felixpageau.roboboat.mission2015.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class BeaconReport {
  private final Course course;
  private final TeamCode team;
  private final BuoyColor buoyColor;

  @JsonCreator
  public BeaconReport(@JsonProperty(value = "course") Course course, @JsonProperty(value = "team") TeamCode team,
      @JsonProperty(value = "buoyColor") BuoyColor buoyColor) {
    this.course = Preconditions.checkNotNull(course);
    this.team = Preconditions.checkNotNull(team);
    this.buoyColor = Preconditions.checkNotNull(buoyColor);
  }

  public Course getCourse() {
    return course;
  }

  public TeamCode getTeam() {
    return team;
  }

  public BuoyColor getBuoyColor() {
    return buoyColor;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("course", course).add("team", team).add("buoyColor", buoyColor).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(course, team, buoyColor);
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
      return Objects.equal(course, other.course) && Objects.equal(team, other.team) && Objects.equal(buoyColor, other.buoyColor);
    }
    return false;
  }

  public static void main(String[] args) {
    System.out.println(new BeaconReport(Course.courseA, new TeamCode("AUVSI"), BuoyColor.blue));
  }
}
