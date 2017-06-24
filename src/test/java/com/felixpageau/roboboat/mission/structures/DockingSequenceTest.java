package com.felixpageau.roboboat.mission.structures;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class DockingSequenceTest {

  @Test
  public void testGenerateRandomDockingBay() {
    for(int i = 0; i < 100; i++) {
      assertFalse(Code.none.equals(DockingSequence.generateRandomDockingSequence().get7Seg()));
      assertFalse(Code.none.equals(DockingSequence.generateRandomDockingSequence().getActivePinger()));
    }
  }

}
