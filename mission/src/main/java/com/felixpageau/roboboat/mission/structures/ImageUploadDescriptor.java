package com.felixpageau.roboboat.mission.structures;

import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@ThreadSafe
@Immutable
@ParametersAreNonnullByDefault
public class ImageUploadDescriptor {
  private final Course course;
  private final TeamCode team;
  private final Timestamp timestamp;
  private final String imageId;
  private final String filename;
  
  @JsonCreator
  public ImageUploadDescriptor(@JsonProperty(value = "course") Course course, 
      @JsonProperty(value = "team") TeamCode team,
      @JsonProperty(value = "timestamp") Timestamp timestamp, 
      @JsonProperty(value = "imageId") String imageId,
      @JsonProperty(value = "file") String file) {
    this.course = Preconditions.checkNotNull(course, "The provided course cannot be null");
    this.team = Preconditions.checkNotNull(team, "The provided team cannot be null");
    this.timestamp = Preconditions.checkNotNull(timestamp, "The provided timestamp cannot be null");
    this.imageId = Preconditions.checkNotNull(imageId, "The provided imageId cannot be null");
    this.filename = Preconditions.checkNotNull(file, "The provided file cannot be null");
  }

  /**
   * @return the course
   */
  public Course getCourse() {
    return course;
  }

  /**
   * @return the team
   */
  public TeamCode getTeam() {
    return team;
  }

  /**
   * @return the timestamp
   */
  public Timestamp getTimestamp() {
    return timestamp;
  }

  /**
   * @return the imageId
   */
  public String getImageId() {
    return imageId;
  }
  
  /**
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof ImageUploadDescriptor)) return false;
    ImageUploadDescriptor other = (ImageUploadDescriptor) obj;
    return Objects.equals(course, other.course) && Objects.equals(team, other.team) && Objects.equals(timestamp, other.timestamp) && Objects.equals(imageId, other.imageId) && Objects.equals(filename, other.filename);
  }

  @Override
  public int hashCode() {
    return Objects.hash(course, team, timestamp, imageId, filename);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(ImageUploadDescriptor.class).add("course", course).add("team", team).add("timestamp", timestamp).add("imageId", imageId).add("filename", filename).toString();
  }
}
