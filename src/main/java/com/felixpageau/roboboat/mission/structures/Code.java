package com.felixpageau.roboboat.mission.structures;

import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public enum Code {
  none("0"), v1("1"), v2("2"), v3("3");
  private static final Map<String, Code> lookup = new HashMap<>();
  private final String value;

  static {
    for (Code s : EnumSet.allOf(Code.class)) {
        lookup.put(s.getValue(), s);
    }
  }

  private Code(String value) {
    this.value = Preconditions.checkNotNull(value, "value cannot be null");
  }

  @JsonIgnore
  public static Code generateRandomLeaderSequence() {
    List<Code> sequences = ImmutableList.copyOf(lookup.values());
    return sequences.get(new SecureRandom().nextInt(sequences.size()));
  }

  @JsonIgnore
  public static Code get(char code) {
    return lookup.get(Character.toUpperCase(code));
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