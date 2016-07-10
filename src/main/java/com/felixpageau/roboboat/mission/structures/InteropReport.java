package com.felixpageau.roboboat.mission.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class InteropReport {
  private final Course course;
  private final TeamCode team;
  private final Shape shape;
  private final String imageId;

  @JsonCreator
  public InteropReport(@JsonProperty(value = "course") Course course, @JsonProperty(value = "team") TeamCode team, @JsonProperty(value = "shape") Shape shape,
      @JsonProperty(value = "imageId") String imageId) {
    this.course = Preconditions.checkNotNull(course, "the course cannot be null");
    this.team = Preconditions.checkNotNull(team, "the team cannot be null");
    this.shape = Preconditions.checkNotNull(shape, "the shape cannot be null");
    this.imageId = Preconditions.checkNotNull(imageId, "the imageId cannot be null");
  }

  public Course getCourse() {
    return course;
  }

  public TeamCode getTeam() {
    return team;
  }

  public Shape getShape() {
    return shape;
  }

  public String getImageId() {
    return imageId;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("course", course).add("team", team).add("shape", shape.getValue()).add("imageId", imageId).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(course, team, shape, imageId);
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
    if (obj instanceof InteropReport) {
      InteropReport other = (InteropReport) obj;
      return Objects.equal(course, other.course) && Objects.equal(team, other.team) && Objects.equal(shape, other.shape)
          && Objects.equal(imageId, other.imageId);
    }
    return false;
  }
}
