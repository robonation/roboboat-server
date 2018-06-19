package com.felixpageau.roboboat.mission.structures;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class CarouselStatus {
  private final boolean turning;
  
  @JsonCreator
  public CarouselStatus(@JsonProperty(value = "turning") boolean turning) {
    this.turning = turning;
  }

  public boolean isTurning() {
    return turning;
  }
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof CarouselStatus)) return false;
    CarouselStatus other = (CarouselStatus) obj;
    return Objects.equals(turning, other.turning);
  }

  @Override
  public int hashCode() {
    return Objects.hash(turning);
  }
  
  @JsonValue
  public String getValue() {
    return Boolean.toString(isTurning());
  }
}
