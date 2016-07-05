/**
 * 
 */
package com.felixpageau.roboboat.mission.structures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author felixpageau
 *
 */
public class GateCodeTest {

  /**
   * Test method for
   * {@link com.felixpageau.auvsif.roboboat2014.output.GateCode#GateCode(int, java.lang.String)}
   * .
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGateCodeBadEntrance() {
    new GateCode(0, "X");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGateCodeBadExit() {
    new GateCode(1, "A");
  }

  @Test
  public void testGateCode() {
    assertNotNull(new GateCode(1, "X"));
    assertNotNull(new GateCode(2, "X"));
    assertNotNull(new GateCode(3, "X"));
    assertNotNull(new GateCode(1, "Y"));
    assertNotNull(new GateCode(2, "Y"));
    assertNotNull(new GateCode(3, "Y"));
    assertNotNull(new GateCode(1, "Z"));
    assertNotNull(new GateCode(2, "Z"));
    assertNotNull(new GateCode(3, "Z"));
  }

  /**
   * Test method for
   * {@link com.felixpageau.auvsif.roboboat2014.output.GateCode#getGateCode()}.
   * 
   * @throws JsonProcessingException
   */
  @Test
  public void testGetGateCode() throws JsonProcessingException {
    ObjectMapper om = new ObjectMapper();
    assertEquals("{\"gateCode\":\"(1,X)\"}", om.writeValueAsString(new GateCode(1, "X")));
  }

}
