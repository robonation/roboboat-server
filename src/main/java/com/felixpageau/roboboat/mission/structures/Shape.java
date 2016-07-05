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
  NONE(' '), ZERO('0'), ONE('1'), TWO('2'), THREE('3'), FOUR('4'), FIVE('5'), SIX('6'), SEVEN('7'), EIGHT('8'), NINE('9'), A('A'), B('B'), C('C'), D('D'), E('E'), F('F');

  private static final Map<Character, Shape> lookup = new HashMap<>();
  private final char value;

  static {
    for (Shape s : EnumSet.allOf(Shape.class)) {
      if (s != NONE) {
        lookup.put(s.getValue(), s);
      }
    }
  }

  private Shape(char value) {
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
    if (code.length() == 1) {
      return lookup.get(Character.toUpperCase(code.charAt(0)));
    }
    throw new IllegalArgumentException("The provided shape string (" + code + ") has more than 1 character");
  }

  @JsonValue
  public char getValue() {
    return value;
  }
}