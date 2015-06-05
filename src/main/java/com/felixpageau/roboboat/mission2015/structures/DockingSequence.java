/**
 * 
 */
package com.felixpageau.roboboat.mission2015.structures;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * @author felixpageau
 *
 */
public class DockingSequence {
  private final List<DockingBay> dockingBaySequence;

  public DockingSequence(@JsonProperty(value = "dockingBaySequence") List<DockingBay> dockingBaySequence) {
    this.dockingBaySequence = ImmutableList.copyOf(Preconditions.checkNotNull(dockingBaySequence));
  }

  public static DockingSequence generateRandomDockingSequence() {
    return new DockingSequence(ImmutableList.of(DockingBay.generateRandomDockingBay(), DockingBay.generateRandomDockingBay()));
  }

  public List<DockingBay> getDockingBaySequence() {
    return dockingBaySequence;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("dockingBaySequence", dockingBaySequence).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(dockingBaySequence);
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
    if (obj instanceof DockingSequence) {
      DockingSequence other = (DockingSequence) obj;
      return Objects.equal(dockingBaySequence, other.dockingBaySequence);
    }
    return false;
  }
}
