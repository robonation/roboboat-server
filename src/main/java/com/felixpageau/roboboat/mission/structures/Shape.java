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
import com.google.common.collect.ImmutableMap;

public enum Shape {

  NONE(" "), ZERO("zero"), ONE("one"), TWO("two"), THREE("three"), FOUR("four"), FIVE("five"), SIX("six"), SEVEN("seven"), EIGHT("eight"), NINE("nine"), A("a"), B(
      "b"), C("c"), D("d"), E("e"), F("f");
  private static Map<String, String> altValue;
  private static final Map<String, Shape> lookup = new HashMap<>();
  private final String value;

  static {
    for (Shape s : EnumSet.allOf(Shape.class)) {
      if (s != NONE) {
        lookup.put(s.getValue(), s);
      }
    }
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    builder.put("0", "zero");
    builder.put("1", "one");
    builder.put("2", "two");
    builder.put("3", "three");
    builder.put("4", "four");
    builder.put("5", "five");
    builder.put("6", "six");
    builder.put("7", "seven");
    builder.put("8", "eight");
    builder.put("9", "nine");
    builder.put("A", "a");
    builder.put("B", "b");
    builder.put("C", "c");
    builder.put("D", "d");
    builder.put("E", "e");
    builder.put("F", "f");
    altValue = builder.build();
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
    if (code != null) {
      if (altValue.containsKey(code)) {
        code = altValue.get(code);
      }
    }
    Shape shape = lookup.get(code.toLowerCase());
    if (shape == null) {
      throw new IllegalArgumentException(String.format("The shape '%s' is not a valid value. Valid ones are: %s", code, lookup.keySet()));
    }
    return shape;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}