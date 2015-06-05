package com.felixpageau.roboboat.mission2015.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission2015.structures.BuoyColor;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Pinger {
  private final BuoyColor buoyColor;

  @JsonCreator
  public Pinger(@JsonProperty(value = "buoyColor") BuoyColor buoyColor) {
    this.buoyColor = Preconditions.checkNotNull(buoyColor);
  }

  public BuoyColor getBuoyColor() {
    return buoyColor;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("buoyColor", buoyColor).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(buoyColor);
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
    if (obj instanceof Pinger) {
      Pinger other = (Pinger) obj;
      return Objects.equal(buoyColor, other.buoyColor);
    }
    return false;
  }
}
