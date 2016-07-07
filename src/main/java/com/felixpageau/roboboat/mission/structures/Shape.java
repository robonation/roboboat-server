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

public enum Shape {
  NONE(" "), ZERO("zero"), ONE("one"), TWO("two"), THREE("three"), FOUR("four"), FIVE("five"), SIX("six"), SEVEN("seven"), EIGHT("eight"), NINE("nine"), A("A"), B(
      "B"), C("C"), D("D"), E("E"), F("F");

  private static final Map<String, Shape> lookup = new HashMap<>();
  private final String value;

  static {
    for (Shape s : EnumSet.allOf(Shape.class)) {
      if (s != NONE) {
        lookup.put(s.getValue(), s);
      }
    }
  }

  private Shape(String value) {
    this.value = Preconditions.checkNotNull(value, "value cannot be null");
  }

  @JsonIgnore
  public static Shape generateRandomInteropShape() {
    List<Shape> shapes = ImmutableList.copyOf(lookup.values());
    return shapes.get(new SecureRandom().nextInt(shapes.size()));
  }

  @JsonIgnore
  public static Shape get(char code) {
    return lookup.get(Character.toUpperCase(code));
  }

  @JsonCreator
  public static Shape fromString(String code) {
    return lookup.get(code);
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}