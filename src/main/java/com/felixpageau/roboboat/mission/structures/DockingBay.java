package com.felixpageau.roboboat.mission.structures;

import java.security.SecureRandom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class DockingBay {
  private final Code code;

  @JsonCreator
  public DockingBay(@JsonProperty(value = "code") Code code) {
    this.code = Preconditions.checkNotNull(code, "code cannot be null");
  }

  public static DockingBay generateRandomDockingBay() {
    return new DockingBay(Code.values()[new SecureRandom().nextInt(Code.values().length)]);
  }

  public Code getCode() {
    return code;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("code", code).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(code);
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
    if (obj instanceof DockingBay) {
      DockingBay other = (DockingBay) obj;
      return Objects.equal(code, other.code);
    }
    return false;
  }
}
