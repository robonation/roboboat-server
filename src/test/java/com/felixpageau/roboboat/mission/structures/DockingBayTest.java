package com.felixpageau.roboboat.mission.structures;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class DockingBayTest {

  @Test
  public void testGenerateRandomDockingBay() {
    for(int i = 0; i < 100; i++) {
      assertFalse(new DockingBay(Code.none).equals(DockingBay.generateRandomDockingBay()));
    }
  }

}
