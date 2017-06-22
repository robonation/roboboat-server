package com.felixpageau.roboboat.mission.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Preconditions;

public enum Course {
  courseA, courseB, courseC, openTest, testCourse1, testCourse2, testCourse3, testCourse4, testCourse5, testCourse6, testCourse7, testCourse8, testCourse9, testCourse10;

  @JsonCreator
  public static Course fromString(final String value) {
    Preconditions.checkNotNull(value, "value cannot be null");
    return valueOf(value);
  }
}
