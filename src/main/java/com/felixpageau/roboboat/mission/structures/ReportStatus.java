package com.felixpageau.roboboat.mission.structures;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class ReportStatus {
  private final boolean success;

  @JsonCreator
  public ReportStatus(@JsonProperty(value = "success") boolean success) {
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof ReportStatus)) return false;
    ReportStatus other = (ReportStatus) obj;
    return Objects.equals(success, other.success);
  }

  @Override
  public int hashCode() {
    return Objects.hash(success);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(ReportStatus.class).add("success", success).toString();
  }
}
