package com.felixpageau.roboboat.mission.structures;

import jersey.repackaged.com.google.common.base.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class UploadStatus {
  private final String imageID;

  public UploadStatus(@JsonProperty(value = "imageId") String imageID) {
    this.imageID = imageID;
  }

  public String getImageId() {
    return imageID;
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(imageID);
  }

  @JsonIgnore
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null) return false;
    if (!(obj instanceof UploadStatus)) return false;
    UploadStatus other = (UploadStatus) obj;
    return Objects.equal(imageID, other.imageID);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("imageID", imageID).toString();
  }
}
