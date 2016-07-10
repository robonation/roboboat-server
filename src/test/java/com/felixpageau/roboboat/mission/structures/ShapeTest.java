package com.felixpageau.roboboat.mission.structures;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ShapeTest {

  @Test
  public void testFromString() {
    assertEquals(Shape.ZERO, Shape.fromString("zero"));
    assertEquals(Shape.ZERO, Shape.fromString("ZERO"));
    assertEquals(Shape.ZERO, Shape.fromString("0"));

    assertEquals(Shape.ONE, Shape.fromString("one"));
    assertEquals(Shape.ONE, Shape.fromString("oNe"));
    assertEquals(Shape.ONE, Shape.fromString("1"));

    assertEquals(Shape.A, Shape.fromString("a"));
    assertEquals(Shape.A, Shape.fromString("A"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromStringInvalid() {
    Shape.fromString("ten");
  }
}