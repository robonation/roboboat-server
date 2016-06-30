package com.felixpageau.roboboat.mission.server;

import java.time.LocalDateTime;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

@ThreadSafe
@Immutable
@ParametersAreNonnullByDefault
@ReturnValuesAreNonNullByDefault
public class TimeSlot {
  public static final TimeSlot DEFAULT_TIMESLOT = new TimeSlot(Course.openTest, LocalDateTime.now(), LocalDateTime.now().plusMinutes(20));
  private final Course course;
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;

  @JsonCreator
  public TimeSlot(@JsonProperty(value = "course") Course course, @JsonProperty(value = "startTime") LocalDateTime startTime,
      @JsonProperty(value = "endTime") LocalDateTime endTime) {
    this.course = Preconditions.checkNotNull(course);
    this.startTime = Preconditions.checkNotNull(startTime);
    this.endTime = Preconditions.checkNotNull(endTime);
  }

  /**
   * @return the course
   */
  public Course getCourse() {
    return course;
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
    if (!(obj instanceof TimeSlot)) {
      return false;
    }
    TimeSlot other = (TimeSlot) obj;

    return Objects.equal(course, other.course) && Objects.equal(startTime, other.startTime) && Objects.equal(endTime, other.endTime);
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(course, startTime, endTime);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("course", course).add("startTime", startTime.format(Config.DATE_FORMATTER.get()))
        .add("endTime", endTime.format(Config.DATE_FORMATTER.get())).toString();
  }
}
