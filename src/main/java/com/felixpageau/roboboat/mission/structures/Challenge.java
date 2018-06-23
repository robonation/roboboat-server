package com.felixpageau.roboboat.mission.structures;

import java.util.Locale;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.Preconditions;

/**
 * Enum of valid challenges
 */
@ReturnValuesAreNonNullByDefault
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public enum Challenge {
  none, auto, gates, speed, docking, path, follow, return_to_dock;

  @Override
  @JsonValue
  public String toString() {
    return name().replaceFirst("_.*", "");
  };

  @JsonCreator
  public static Challenge fromString(final String value) {
    Preconditions.checkNotNull(value, "value cannot be null");
    if (return_to_dock.name().replaceFirst("_.*", "").equals(value)) {
      return return_to_dock;
    }
    return valueOf(value.toLowerCase(Locale.ENGLISH));
  }
}
