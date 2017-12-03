package com.felixpageau.roboboat.mission.utils;

import com.felixpageau.roboboat.mission.structures.Course;

public class NMEAUtils {
  public static String formatPingerNMEAmessage(Course course, int pingerCode) {
    return String.format("RXPNC,%s,%d", course.name().replaceFirst("course", ""), pingerCode);
  }

  public static String formatNMEAmessage(String message) {
    return String.format("$%s*%s%n", message, calculateChecksum(message));
  }

  private static String calculateChecksum(String message) {
    int checksum = 0;
    for (int i = 0; i < message.length(); i++) {
      checksum ^= message.charAt(i);
    }
    return Integer.toHexString(checksum).toUpperCase();
  }
}
