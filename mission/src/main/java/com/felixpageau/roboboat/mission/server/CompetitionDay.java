package com.felixpageau.roboboat.mission.server;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class CompetitionDay {
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;

  @JsonCreator
  public CompetitionDay(@JsonProperty(value = "startTime") LocalDateTime startTime, @JsonProperty(value = "endTime") LocalDateTime endTime) {
    this.startTime = Preconditions.checkNotNull(startTime);
    this.endTime = Preconditions.checkNotNull(endTime);
  }

  @JsonIgnore
  public String getDay() {
    return new SimpleDateFormat("EEE").format(startTime);
  }

  /**
   * @return the startTime
   */
  public LocalDateTime getStartTime() {
    return startTime;
  }

  /**
   * @return the endTime
   */
  public LocalDateTime getEndTime() {
    return endTime;
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
    if (!(obj instanceof CompetitionDay)) {
      return false;
    }
    CompetitionDay other = (CompetitionDay) obj;

    return Objects.equal(startTime, other.startTime) && Objects.equal(endTime, other.endTime);
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(startTime, endTime);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("day", getDay()).add("startTime", startTime.format(Config.DATE_FORMATTER.get()))
        .add("endTime", endTime.format(Config.DATE_FORMATTER.get())).toString();
  }
}
