package com.felixpageau.roboboat.mission2015.utils;

import com.felixpageau.roboboat.mission2015.structures.Course;

public class NMEAUtils {
    public static String formatPingerNMEAmessage(Course course, int pingerCode) {
        return String.format("RXPNC,%s,%d", getCourseCode(course), pingerCode);
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
        return String.format("$%s*%s\r\n", message, calculateChecksum(message));
    }
    
    private static String calculateChecksum (String message) {
        int checksum = 0; 
        for(int i = 0; i < message.length(); i++) { 
          checksum ^= (int)message.charAt(i); 
        }
        return Integer.toHexString(checksum);
    }
}
