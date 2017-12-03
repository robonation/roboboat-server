package com.felixpageau.roboboat.mission.structures;

import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.felixpageau.roboboat.mission.utils.GuavaCollectors;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.Preconditions;

/**
 * Enum of valid code
 */
@ReturnValuesAreNonNullByDefault
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public enum Code {
  none("0"), v1("1"), v2("2"), v3("3");
  private static final Map<String, Code> lookup;
  private final String value;

  static {
    lookup = EnumSet.allOf(Code.class).stream().collect(GuavaCollectors.immutableMap(l -> l.getValue()));
  }

  private Code(String value) {
    this.value = Preconditions.checkNotNull(value, "value cannot be null");
  }

  @JsonIgnore
  public static Code generateCodeSequence() {
    List<Code> sequences = lookup.values().stream().filter(x -> x != Code.none).collect(GuavaCollectors.immutableList());
    return sequences.get(new SecureRandom().nextInt(sequences.size()));
  }

  @JsonIgnore
  public static Code get(char code) {
    return lookup.get(Character.toString(Character.toUpperCase(code)));
  }

  @JsonCreator
  public static Code fromString(String code) {
    Code shape = lookup.get(code.toLowerCase());
    if (shape == null) {
      throw new IllegalArgumentException(String.format("The LeaderSequence '%s' is not a valid value. Valid ones are: %s", code, lookup.keySet()));
    }
    return shape;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}