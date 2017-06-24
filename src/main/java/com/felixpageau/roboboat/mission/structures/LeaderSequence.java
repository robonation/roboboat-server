package com.felixpageau.roboboat.mission.structures;

import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public enum LeaderSequence {
   none("00"), v12("12"), v23("23"), v34("34"), v41("41");
  private static final Map<String, LeaderSequence> lookup = new HashMap<>();
  private final String value;

  static {
    for (LeaderSequence s : EnumSet.allOf(LeaderSequence.class)) {
        lookup.put(s.getValue(), s);
    }
  }

  private LeaderSequence(String value) {
    this.value = Preconditions.checkNotNull(value, "value cannot be null");
  }

  @JsonIgnore
  public static LeaderSequence generateRandomLeaderSequence() {
    List<LeaderSequence> sequences = ImmutableList.copyOf(lookup.values().stream().filter(x -> x != LeaderSequence.none).collect(Collectors.toList()));
    return sequences.get(new SecureRandom().nextInt(sequences.size()));
  }

  @JsonIgnore
  public static LeaderSequence get(char code) {
    return lookup.get(Character.toUpperCase(code));
  }

  @JsonCreator
  public static LeaderSequence fromString(String code) {
    LeaderSequence shape = lookup.get(code.toLowerCase());
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