/**
 * 
 */
package com.felixpageau.roboboat.mission.structures;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.MoreObjects;
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

  @JsonCreator
  public DockingSequence(String sequence) {
    Preconditions.checkNotNull(sequence);
    switch (sequence) {
    case "11": dockingBaySequence = ImmutableList.of(new DockingBay(Code.v1), new DockingBay(Code.v1)); break;
    case "12": dockingBaySequence = ImmutableList.of(new DockingBay(Code.v1), new DockingBay(Code.v2)); break;
    case "13": dockingBaySequence = ImmutableList.of(new DockingBay(Code.v1), new DockingBay(Code.v3)); break;
    case "21": dockingBaySequence = ImmutableList.of(new DockingBay(Code.v2), new DockingBay(Code.v1)); break;
    case "22": dockingBaySequence = ImmutableList.of(new DockingBay(Code.v2), new DockingBay(Code.v2)); break;
    case "23": dockingBaySequence = ImmutableList.of(new DockingBay(Code.v2), new DockingBay(Code.v3)); break;
    case "31": dockingBaySequence = ImmutableList.of(new DockingBay(Code.v3), new DockingBay(Code.v1)); break;
    case "32": dockingBaySequence = ImmutableList.of(new DockingBay(Code.v3), new DockingBay(Code.v2)); break;
    case "33": dockingBaySequence = ImmutableList.of(new DockingBay(Code.v3), new DockingBay(Code.v3)); break;
    default: dockingBaySequence = ImmutableList.of();
    }
  }
  
  @JsonValue
  public String getValue() {
    return dockingBaySequence.stream().map(b -> b.getCode().getValue()).collect(Collectors.joining(""));
  }
  
  @JsonIgnore
  public static DockingSequence generateRandomDockingSequence() {
    return new DockingSequence(ImmutableList.of(DockingBay.generateRandomDockingBay(), DockingBay.generateRandomDockingBay()));
  }

  @JsonIgnore
  public List<DockingBay> getDockingBaySequence() {
    return dockingBaySequence;
  }
  
  @JsonIgnore
  public Code getActivePinger() {
    return dockingBaySequence.stream().map(x -> x.getCode()).findFirst().orElse(null);
  }
  
  @JsonIgnore
  public Code get7Seg() {
    return dockingBaySequence.stream().map(x -> x.getCode()).skip(1).findFirst().orElse(null);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("dockingBaySequence", dockingBaySequence).toString();
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
