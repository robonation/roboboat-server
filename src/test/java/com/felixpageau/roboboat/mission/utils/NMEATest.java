package com.felixpageau.roboboat.mission.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.felixpageau.roboboat.mission.structures.Course;

public class NMEATest {
  @Test
  public void testNMEAFormatter() {
    assertEquals("$RXPNC,A,0*26\n", NMEAUtils.formatNMEAmessage("RXPNC,A,0"));
  }
  
  @Test
  public void testFormatPingerNMEAMessage() {
    assertEquals("RXPNC,A,2", NMEAUtils.formatPingerNMEAmessage(Course.courseA, 2));
  }
}
