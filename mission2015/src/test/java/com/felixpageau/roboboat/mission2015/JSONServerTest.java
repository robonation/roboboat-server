package com.felixpageau.roboboat.mission2015;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felixpageau.roboboat.mission2015.structures.BeaconReport;
import com.felixpageau.roboboat.mission2015.structures.BuoyColor;
import com.felixpageau.roboboat.mission2015.structures.Challenge;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.DockingSequence;
import com.felixpageau.roboboat.mission2015.structures.GateCode;
import com.felixpageau.roboboat.mission2015.structures.HeartbeatReport;
import com.felixpageau.roboboat.mission2015.structures.InteropReport;
import com.felixpageau.roboboat.mission2015.structures.Position;
import com.felixpageau.roboboat.mission2015.structures.ReportStatus;
import com.felixpageau.roboboat.mission2015.structures.Shape;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.felixpageau.roboboat.mission2015.structures.Timestamp;
import com.felixpageau.roboboat.mission2015.structures.UploadStatus;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

public class JSONServerTest {
  private static String basePath = "http://127.0.0.1:9000";

  // private static Server server;

  @BeforeClass
  public static void setUp() throws Exception {
    // CompetitionResourceConfig config = new Mission2015ResourceConfig();
    // server = JettyHttpContainerFactory.createServer(URI.create(basePath),
    // config, true);
  }

  @AfterClass
  public static void tearDown() throws Exception {
    // server.stop();
  }

  public String readLine(BufferedReader r) throws IOException {
    String line = null;
    while (line == null) {
      line = r.readLine();
    }
    return line;
  }

  @Test
  public void test() throws Exception {
    // basePath = "http://ec2-52-7-253-202.compute-1.amazonaws.com";
    // basePath = "http://10.0.2.1:8080";

    System.out.println("Running test");
    ObjectMapper mapper = new ObjectMapper();
    try (CloseableHttpClient client = HttpClients.createDefault()) {
      CloseableHttpResponse resp;
      HttpPost post;

      // end run
      resp = client.execute(new HttpPost(basePath + "/run/end/courseA/AUVSI"));
      assertEquals("End run status code", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // start run
      resp = client.execute(new HttpPost(basePath + "/run/start/courseA/AUVSI"));
      assertEquals("Start run status code", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // Heartbeat (gate)
      post = new HttpPost(basePath + "/heartbeat/courseA/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new HeartbeatReport(new Timestamp(), Challenge.gates, Position.DOCK))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Gate heartbeat", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // Heartbeat (obstacles)
      post = new HttpPost(basePath + "/heartbeat/courseA/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new HeartbeatReport(new Timestamp(), Challenge.obstacles, Position.FOUNDERS))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Obstacle heartbeat", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // Obstacle
      resp = client.execute(new HttpGet(basePath + "/obstacleAvoidance/courseA/AUVSI"));
      assertEquals("Obstacle", 200, resp.getStatusLine().getStatusCode());
      GateCode gc = mapper.readValue(resp.getEntity().getContent(), GateCode.class);
      assertNotNull(gc);
      resp.close();

      // Heartbeat (docking)
      post = new HttpPost(basePath + "/heartbeat/courseA/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new HeartbeatReport(new Timestamp(), Challenge.docking, Position.FOUNDERS))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Docking heartbeat", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // Docking
      resp = client.execute(new HttpGet(basePath + "/automatedDocking/courseA/AUVSI"));
      assertEquals("Docking", 200, resp.getStatusLine().getStatusCode());
      DockingSequence ds = mapper.readValue(resp.getEntity().getContent(), DockingSequence.class);
      assertNotNull(ds);
      assertNotNull(ds.getDockingBaySequence());
      assertEquals(2, ds.getDockingBaySequence().size());
      resp.close();

      // Heartbeat (pinger)
      post = new HttpPost(basePath + "/heartbeat/courseA/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new HeartbeatReport(new Timestamp(), Challenge.pinger, Position.FOUNDERS))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Pinger heartbeat", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // Pinger
      post = new HttpPost(basePath + "/pinger/courseA/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new BeaconReport(Course.courseA, new TeamCode("AUVSI"), BuoyColor.black))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Pinger", 200, resp.getStatusLine().getStatusCode());
      assertNotNull(mapper.readValue(resp.getEntity().getContent(), ReportStatus.class)); // We
                                                                                          // don't
                                                                                          // know
                                                                                          // if
                                                                                          // black
                                                                                          // is
                                                                                          // right
                                                                                          // or
                                                                                          // not
      resp.close();

      // Heartbeat (interop)
      post = new HttpPost(basePath + "/heartbeat/courseA/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new HeartbeatReport(new Timestamp(), Challenge.interop, Position.FOUNTAIN))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Interp heartbeat", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // Interop - list images
      resp = client.execute(new HttpGet(basePath + "/interop/images/courseA/AUVSI"));
      assertEquals("Interop - list", 200, resp.getStatusLine().getStatusCode());
      String listing = CharStreams.toString(new InputStreamReader(resp.getEntity().getContent(), Charsets.UTF_8));
      resp.close();
      assertNotNull(listing);
      String[] images = listing.replaceAll(".*?href\\=\"([^\"]+)\".*?", "$1,").replaceFirst(",>.*", "").split(",");
      for (String image : images) {
        resp = client.execute(new HttpGet(basePath + image));
        assertEquals(200, resp.getStatusLine().getStatusCode());
        resp.close();
      }

      // Interop - upload
      post = new HttpPost(basePath + "/interop/image/courseA/AUVSI");
      post.setEntity(MultipartEntityBuilder.create().addPart("file", new FileBody(new File("test.jpg"))).setMode(HttpMultipartMode.BROWSER_COMPATIBLE).build());
      // post.setHeader("Content-Type", "multipart/form-data");
      resp = client.execute(post);
      assertEquals("Interop upload", 200, resp.getStatusLine().getStatusCode());
      System.out.println(resp.toString());
      UploadStatus us = mapper.readValue(resp.getEntity().getContent(), UploadStatus.class);
      assertNotNull(us);
      resp.close();

      // Interop - report
      post = new HttpPost(basePath + "/interop/report/courseA/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new InteropReport(Course.courseA, new TeamCode("AUVSI"), Shape.EIGHT, us.getImageId()))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Interop report", 200, resp.getStatusLine().getStatusCode());
      assertNotNull(mapper.readValue(resp.getEntity().getContent(), ReportStatus.class)); // We
                                                                                          // don't
                                                                                          // know
                                                                                          // if
                                                                                          // eight
                                                                                          // is
                                                                                          // right
                                                                                          // or
                                                                                          // not
      resp.close();

      // Heartbeat (return)
      post = new HttpPost(basePath + "/heartbeat/courseA/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new HeartbeatReport(new Timestamp(), Challenge.return_to_dock, Position.FOUNDERS))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Return heartbeat", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // end run
      resp = client.execute(new HttpPost(basePath + "/run/end/courseA/AUVSI"));
      assertEquals("End run", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
}
