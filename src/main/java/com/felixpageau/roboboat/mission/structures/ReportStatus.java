package com.felixpageau.roboboat.mission.structures;

import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.MoreObjects;

/**
 * Defines the status of a report operation
 */
@ReturnValuesAreNonNullByDefault
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public class ReportStatus {
  private final boolean success;
  private final String error;

  @Deprecated
  public ReportStatus(@JsonProperty(value = "success") boolean success) {
    this(success, null);
  }
  
  @JsonCreator
  public ReportStatus(@JsonProperty(value = "success") boolean success, @Nullable @JsonProperty(value = "error") String error) {
    this.success = success;
    this.error = error;
  }

  public boolean isSuccess() {
    return success;
  }
  
  @CheckForNull
  public String getError() {
    return error;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof ReportStatus)) return false;
    ReportStatus other = (ReportStatus) obj;
    return Objects.equals(success, other.success) && Objects.equals(error, other.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(success, error);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(ReportStatus.class).add("success", success).add("error", error).toString();
  }
}
