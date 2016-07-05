package com.felixpageau.roboboat.mission.server;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

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
 * 
 * @param <V>
 *          the type of Map value in the {@link Map.Entry}
 */
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public class TimeSlotComparatorEntry<V> implements Comparator<Map.Entry<TimeSlot, V>>, Serializable {
  private static final long serialVersionUID = -3266251202074962742L;
  private final boolean chronologicalOrder;

  /**
   * Builds a {@link TimeSlotComparator} with a specifiable reverse or not
   * chronological order.
   * 
   * @param chronologicalOrder
   *          true for normal order and false for reverse
   */
  public TimeSlotComparatorEntry(boolean chronologicalOrder) {
    this.chronologicalOrder = chronologicalOrder;
  }

  @Override
  public int compare(Map.Entry<TimeSlot, V> o1, Map.Entry<TimeSlot, V> o2) {
    if (o1 == null || o1.getKey() == null) return -1;
    if (o2 == null || o2.getKey() == null) return 1;

    if (o1.getKey().getStartTime().equals(o2.getKey().getStartTime())) {
      return o1.getKey().getCourse().compareTo(o2.getKey().getCourse());
    }
    if (chronologicalOrder) {
      return o1.getKey().getStartTime().isBefore(o2.getKey().getStartTime()) ? 1 : -1;
    }
    return o1.getKey().getStartTime().isBefore(o2.getKey().getStartTime()) ? -1 : 1;
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
    if (!(obj instanceof TimeSlotComparatorEntry)) {
      return false;
    }
    @SuppressWarnings("unchecked")
    TimeSlotComparatorEntry<V> other = (TimeSlotComparatorEntry<V>) obj;

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