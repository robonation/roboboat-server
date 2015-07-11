package com.felixpageau.roboboat.mission2015.structures;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;

public enum Shape {
  ZERO('0'), ONE('1'), TWO('2'), THREE('3'), FOUR('4'), FIVE('5'), SIX('6'), SEVEN('7'), EIGHT('8'), NINE('9'), A('A'), B('B'), C('C'), D('D'), E('E'), F('F');

  private static final Map<Character, Shape> lookup = new HashMap<>();
  private final char value;

  static {
    for (Shape s : EnumSet.allOf(Shape.class))
      lookup.put(s.getValue(), s);
  }

  private Shape(char value) {
    this.value = Preconditions.checkNotNull(value, "value cannot be null");
  }

  @JsonIgnore
  public static Shape generateRandomInteropShape() {
    return Shape.values()[new Random().nextInt(Shape.values().length)];
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