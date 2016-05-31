package com.felixpageau.roboboat.mission.structures;

import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

/**
 * Represents the state of the competition that is displayable on the team
 * monitors.
 *
 */
@ThreadSafe
@Immutable
@ParametersAreNonnullByDefault
public class DisplayStatus {
  private final Map<Course, DisplayReport> reports;

  @JsonCreator
  public DisplayStatus(@JsonProperty("reports") Map<Course, DisplayReport> reports) {
    this.reports = ImmutableMap.copyOf(Preconditions.checkNotNull(reports, "Reports cannot be null"));
  }

  @JsonValue
  public Map<Course, DisplayReport> getReports() {
    return reports;
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
    if (!(obj instanceof DisplayStatus)) {
      return false;
    }
    DisplayStatus other = (DisplayStatus) obj;

    return Objects.equal(reports, other.reports);
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(reports);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("reports", reports).toString();
  }
}
