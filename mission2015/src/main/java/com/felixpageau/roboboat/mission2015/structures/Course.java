package com.felixpageau.roboboat.mission2015.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Preconditions;

public enum Course {
  courseA, courseB, openTest, testCourse1, testCourse2, testCourse3, testCourse4, testCourse5, testCourse6, testCourse7, testCourse8, testCourse9, testCourse10;

  @JsonCreator
  public static Course fromString(final String value) throws IllegalArgumentException {
    Preconditions.checkNotNull(value, "value cannot be null");
    return valueOf(value);
  }
}
