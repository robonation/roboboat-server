/**
 * 
 */
package com.felixpageau.roboboat.mission.structures;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author felixpageau
 *
 */
public class LightSequenceTest {
  /**
   * Test method for
   * {@link com.felixpageau.auvsif.roboboat2014.output.LightSequence#LightSequence(LightColor[])}
   * .
   */
  @Test(expected = NullPointerException.class)
  public void testLightSequenceBadCourse() {
    new LightSequence(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLightSequenceTooManyColors() {
    new LightSequence(new LightColor[] { LightColor.red, LightColor.green, LightColor.blue, LightColor.yellow });
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLightSequenceTooFewColors() {
    new LightSequence(new LightColor[] { LightColor.red, LightColor.green });
  }

  @Test
  public void testLightSequence() {
    new LightSequence(new LightColor[] { LightColor.red, LightColor.green, LightColor.blue });
  }

  /**
   * Test method for marshalling/unmarshalling
   * 
   * @throws IOException
   */
  @Test
  public void testLightSequenceSerialization() throws IOException {
    ObjectMapper om = new ObjectMapper();
    File json = new File("src/test/java/sequence.json");
    LightSequence report = om.readValue(json, LightSequence.class);
    assertNotNull(report);
    assertArrayEquals(Files.readAllBytes(json.toPath()), om.writeValueAsBytes(report));
  }

}
