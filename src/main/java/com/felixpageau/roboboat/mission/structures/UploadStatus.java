package com.felixpageau.roboboat.mission.structures;

import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * Represents the status of an uploaded image.
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonNullByDefault
@Immutable
@ThreadSafe
public class UploadStatus {
  private final String imageID;

  public UploadStatus(@JsonProperty(value = "imageId") String imageID) {
    this.imageID = Preconditions.checkNotNull(imageID, "imageID cannot be null");
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
    return Objects.equals(imageID, other.imageID);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("imageID", imageID).toString();
  }
}
