package com.felixpageau.roboboat.mission.structures;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Defines the latitude of a GPS coordinate
 */
@ReturnValuesAreNonNullByDefault
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public class Latitude {
  private final float value;

  @JsonCreator
  public Latitude(float value) {
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
    if (obj instanceof Latitude) {
      Latitude other = (Latitude) obj;
      return Objects.equal(value, other.value);
    }
    return false;
  }
}
