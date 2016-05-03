package com.felixpageau.roboboat.mission.nmea;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * NMEA Field definition
 */
@ParametersAreNonnullByDefault
@Immutable
@ThreadSafe
public abstract class Field {
  private final String name;

  public Field(String name) {
    this.name = Preconditions.checkNotNull(name, "name cannot be null");
  }

  @Nonnull
  public String name() {
    return name;
  }

  public abstract boolean isValid(String value);

  public abstract String validityCondition();

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("name", name()).add("criteria", validityCondition()).toString();
  }
}
