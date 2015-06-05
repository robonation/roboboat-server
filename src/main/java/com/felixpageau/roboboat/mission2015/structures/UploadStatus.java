package com.felixpageau.roboboat.mission2015.structures;

import jersey.repackaged.com.google.common.base.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadStatus {
  private final String imageID;

  public UploadStatus(@JsonProperty(value = "imageID") String imageID) {
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
    // TODO Auto-generated method stub
    return Objects.toStringHelper(this).add("imageID", imageID).toString();
  }
}
