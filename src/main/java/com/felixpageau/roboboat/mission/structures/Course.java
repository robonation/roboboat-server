package com.felixpageau.roboboat.mission.structures;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.Preconditions;

/**
 * Enum of valid courses
 */
@ReturnValuesAreNonNullByDefault
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public enum Course {
  courseA, courseB, courseC, courseD, openTest, testCourse1, testCourse2, testCourse3, testCourse4, testCourse5, testCourse6, testCourse7, testCourse8, testCourse9, testCourse10;

  @JsonCreator
  public static Course fromString(final String value) {
    Preconditions.checkNotNull(value, "value cannot be null");
    return valueOf(value);
  }
}
