package com.felixpageau.roboboat.mission.server;

import java.io.Serializable;
import java.util.Comparator;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.felixpageau.roboboat.mission.structures.Course;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Compares two {@link TimeSlot} objects and sort them according to
 * chronological order (reversable with a constructor argument) with secondary
 * sort on {@link Course}
 */
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public class TimeSlotComparator implements Comparator<TimeSlot>, Serializable {
  private static final long serialVersionUID = 6104759003649721619L;
  private final boolean chronologicalOrder;

  /**
   * Builds a default {@link TimeSlotComparator} with normal chronological
   * order.
   */
  public TimeSlotComparator() {
    this(true);
  }

  /**
   * Builds a {@link TimeSlotComparator} with a specifiable reverse or not
   * chronological order.
   * 
   * @param chronologicalOrder
   *          true for normal order and false for reverse
   */
  public TimeSlotComparator(boolean chronologicalOrder) {
    this.chronologicalOrder = chronologicalOrder;
  }

  @Override
  public int compare(TimeSlot o1, TimeSlot o2) {
    if (o1 == null) return -1;
    if (o2 == null) return 1;

    if (o1.getStartTime().equals(o2.getStartTime())) {
      return o1.getCourse().compareTo(o2.getCourse());
    }
    if (chronologicalOrder) {
      return o1.getStartTime().isBefore(o2.getStartTime()) ? -1 : 1;
    }
    return o1.getStartTime().isAfter(o2.getStartTime()) ? -1 : 1;
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
    if (!(obj instanceof TimeSlotComparator)) {
      return false;
    }
    TimeSlotComparator other = (TimeSlotComparator) obj;

    return Objects.equal(chronologicalOrder, other.chronologicalOrder);
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(chronologicalOrder);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("chronologicalOrder", chronologicalOrder).toString();
  }
}