package com.felixpageau.roboboat.mission.structures;

import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.felixpageau.roboboat.mission.utils.GuavaCollectors;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Enum of valid shapes
 */
@ReturnValuesAreNonNullByDefault
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public enum Shape {
  NONE(" "), ZERO("zero", "0"), ONE("one", "1"), TWO("two", "2"), THREE("three", "3"), FOUR("four", "4"), FIVE("five", "5"), 
  SIX("six", "6"), SEVEN("seven", "7"), EIGHT("eight", "8"), NINE("nine", "9"), A("a", "A"), B("b", "B"), C("c", "C"), 
  D("d", "D"), E("e", "E"), F("f", "F");
  private static Map<String, String> altValues;
  private static final Map<String, Shape> lookup;
  private final String value;
  private final String altValue;

  static {
    lookup = EnumSet.allOf(Shape.class).stream().collect(GuavaCollectors.immutableMap(l -> l.getValue()));
    altValues = EnumSet.allOf(Shape.class).stream()
        .collect(GuavaCollectors.immutableMap(l -> l.getAltValue(), l -> l.getValue()));
  }

  private Shape(String value, @Nullable String... altValue) {
    this.value = Preconditions.checkNotNull(value, "value cannot be null");
    this.altValue = altValue != null && altValue.length > 0 ? altValue[0] : null;
  }

  @JsonIgnore
  public static Shape generateRandomInteropShape() {
    List<Shape> shapes = ImmutableList.copyOf(lookup.values());
    return shapes.get(new SecureRandom().nextInt(shapes.size()));
  }

  @JsonIgnore
  public static Shape get(char code) {
    return lookup.get(Character.toString(Character.toUpperCase(code)));
  }

  @JsonCreator
  public static Shape fromString(String code) {
    Shape shape = null;
    if (code != null) {
      if (altValues.containsKey(code)) {
        code = altValues.get(code);
      }
      shape = lookup.get(code.toLowerCase());
    }
    if (shape == null) {
      throw new IllegalArgumentException(
          String.format("The shape '%s' is not a valid value. Valid ones are: %s", code, lookup.keySet()));
    }
    return shape;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonIgnore
  public String getAltValue() {
    return altValue;
  }
}