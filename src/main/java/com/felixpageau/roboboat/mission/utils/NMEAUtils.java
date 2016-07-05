package com.felixpageau.roboboat.mission.utils;

import java.util.List;

import com.felixpageau.roboboat.mission.structures.Course;

public class NMEAUtils {
  public static String formatPingerNMEAmessage(Course course, List<Integer> pingerCode) {
    return String.format("RXPNC,%s,%d,%d", getCourseCode(course), pingerCode.get(0), pingerCode.get(1));
  }

  public static String getCourseCode(Course course) {
    switch (course) {
    case courseA:
      return "A";
    case courseB:
      return "B";
    default:
      return "C";
    }
  }

  public static String formatNMEAmessage(String message) {
    return String.format("$%s*%s%n", message, calculateChecksum(message));
  }

  private static String calculateChecksum(String message) {
    int checksum = 0;
    for (int i = 0; i < message.length(); i++) {
      checksum ^= message.charAt(i);
    }
    return Integer.toHexString(checksum);
  }
}
