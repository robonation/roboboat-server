package com.felixpageau.roboboat.mission.nmea;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;

import com.felixpageau.roboboat.mission.SentenceRegistryFactory;

public class TestNMEASentence {
  SentenceRegistry registry = SentenceRegistryFactory.createNMEASentenceRegistry();
  String obstacleReq = "$SVOBS,courseA,AUVSI*5F";
  String obstacleResp = "$TDOBS,20150306061030,2,X*0F";
  String dockingReq = "$SVDOC,courseA,AUVSI*49";
  String dockingResp = "$TDDOC,cruciform,red,triangle,blue*43";
  String pingerReq = "$SVPIN,courseA,AUVSI,red,24,black,36*41";
  String pingerResp = "$TDPIN,20150306061030,true*56";
  String interopReq = "$SVUAV,courseA,AUVSI,eight,a4aa8224-07f2-4b57-a03a-c8887c2505c7*30";
  String interopResp = "$TDUAV,20150306061030,true*43";
  String heartbeatReq = "$SVHRT,courseA,AUVSI,20150306061030,gates,40.689249,-74.044500*0B";
  String heartbeatResp = "$TDHRT,20150306061030,true*4F";
  String startRunReq = "$SVSTR,courseA,AUVSI*54";
  String startRunResp = "$TDSTR,true*7F";
  String endRunReq = "$SVEND,courseA,AUVSI*4E";
  String endRunResp = "$TDEND,true*65";

  @Test(expected = NullPointerException.class)
  public void testParseNullRegistry() {
    NMEASentence.parse(null, heartbeatReq);
  }

  @Test(expected = NullPointerException.class)
  public void testParseNullSentence() {
    NMEASentence.parse(registry, null);
  }

  @Test
  public void testParse() {
    NMEASentence sentence = NMEASentence.parse(registry, heartbeatReq);
    assertNotNull(sentence);
    assertEquals("SV", sentence.getTalkerId());
    assertEquals("HRT", sentence.getSentenceId());
    assertEquals(Arrays.asList("courseA", "AUVSI", "20150306061030", "gates", "40.689249", "-74.044500"), sentence.getFields());

    sentence = NMEASentence.parse(registry, heartbeatResp);
    assertNotNull(sentence);
    assertEquals("TD", sentence.getTalkerId());
    assertEquals("HRT", sentence.getSentenceId());
    assertEquals(Arrays.asList("20150306061030", "true"), sentence.getFields());
  }

  @Test
  public void testParseObstacle() {
    assertNotNull(NMEASentence.parse(registry, obstacleReq));
    assertNotNull(NMEASentence.parse(registry, obstacleResp));
  }

  @Test
  public void testParseDocking() {
    assertNotNull(NMEASentence.parse(registry, dockingReq));
    assertNotNull(NMEASentence.parse(registry, dockingResp));
  }

  @Test
  public void testParsePinger() {
    assertNotNull(NMEASentence.parse(registry, pingerReq));
    assertNotNull(NMEASentence.parse(registry, pingerResp));
  }

  @Test
  public void testParseInterop() {
    assertNotNull(NMEASentence.parse(registry, interopReq));
    assertNotNull(NMEASentence.parse(registry, interopResp));
  }

  @Test
  public void testParseHeartbeat() {
    assertNotNull(NMEASentence.parse(registry, heartbeatReq));
    assertNotNull(NMEASentence.parse(registry, heartbeatResp));
  }

  @Test
  public void testParseStartRun() {
    assertNotNull(NMEASentence.parse(registry, startRunReq));
    assertNotNull(NMEASentence.parse(registry, startRunResp));
  }

  @Test
  public void testParseEndRun() {
    assertNotNull(NMEASentence.parse(registry, endRunReq));
    assertNotNull(NMEASentence.parse(registry, endRunResp));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseBadChecksum() {
    NMEASentence.parse(registry, "$SVHRT,courseA,AUVSI,20150306061030,gates,40.689249,-74.044500*0C");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseBadTalkerId() {
    NMEASentence.parse(registry, "$FUHRT,courseA,AUVSI,20150306061030,gates,40.689249,-74.044500*1D");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseBadSentenceId() {
    NMEASentence.parse(registry, "$TDWOW,courseA,AUVSI,20150306061030,gates,40.689249,-74.044500*1F");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseBadFieldCourse() {
    NMEASentence.parse(registry, "$SVHRT,courseD,AUVSI,20150306061030,gates,40.689249,-74.044500*0E");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseNoFieldTeam() {
    NMEASentence.parse(registry, "$SVHRT,courseA,,20150306061030,gates,40.689249,-74.044500*53");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseBadFieldTeam() {
    NMEASentence.parse(registry, "$SVHRT,courseA,WAYTOOLONG,20150306061030,gates,40.689249,-74.044500*42");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseMissing1Field() {
    NMEASentence.parse(registry, "$SVHRT,AUVSI,20150306061030,gates,40.689249,-74.044500*7B");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseSuperfluousField() {
    NMEASentence.parse(registry, "$SVHRT,AUVSI,courseA,bougadabougadabougada,20150306061030,gates,40.689249,-74.044500*5C");
  }
}
