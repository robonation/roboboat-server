package com.felixpageau.roboboat.mission.structures;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CodeTest {

  @Test
  public void testGenerateCodeSequence() {
    for(int i = 0; i < 100; i++) {
      assertTrue(Code.none != Code.generateCodeSequence());
    }
  }

}
