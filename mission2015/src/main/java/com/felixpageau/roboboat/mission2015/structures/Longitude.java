package com.felixpageau.roboboat.mission2015.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Longitude {
  private final float value;

  @JsonCreator
  public Longitude(float value) {
    this.value = value;
  }

  @JsonValue
  public float getValue() {
    return value;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("value", value).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(value);
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
    if (obj instanceof Longitude) {
      Longitude other = (Longitude) obj;
      return Objects.equal(value, other.value);
    }
    return false;
  }
}
