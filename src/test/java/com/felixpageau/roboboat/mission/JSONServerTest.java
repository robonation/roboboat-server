package com.felixpageau.roboboat.mission;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.shared.utils.io.IOUtil;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felixpageau.roboboat.mission.structures.Challenge;
import com.felixpageau.roboboat.mission.structures.DockingSequence;
import com.felixpageau.roboboat.mission.structures.HeartbeatReport;
import com.felixpageau.roboboat.mission.structures.Position;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.felixpageau.roboboat.mission.structures.Timestamp;
import com.felixpageau.roboboat.mission.structures.UploadStatus;
import com.google.common.io.ByteStreams;

public class JSONServerTest {
  private static int nmea_port = 9999;
  private static String basePath = "http://127.0.0.1:1234";
  //private static String basePath = "http://192.168.0.187:8080";
  private static Server server;

  @BeforeClass
  public static void setUp() throws Exception {
    MissionResourceConfig.port.set(nmea_port);
    CompetitionResourceConfig config = new MissionResourceConfig();
    server = JettyHttpContainerFactory.createServer(URI.create("http://127.0.0.1:6666"), config, true);
  }

  @AfterClass
  public static void tearDown() throws Exception {
    server.stop();
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
    // basePath = "http://192.168.1.111:8080";
    // basePath = "http://127.0.0.1:8080";

    System.out.println("Running test");
    ObjectMapper mapper = new ObjectMapper();
    try (CloseableHttpClient client = HttpClients.createDefault()) {
      CloseableHttpResponse resp;
      HttpPost post;

      // end run
      resp = client.execute(new HttpPost(basePath + "/run/end/testCourse1/AUVSI"));
      // assertEquals("End run status code", 200,
      // resp.getStatusLine().getStatusCode());
      // assertEquals(new ReportStatus(true),
      // mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // start run
      resp = client.execute(new HttpPost(basePath + "/run/start/testCourse1/AUVSI"));
      assertEquals("Start run status code", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // Heartbeat (gate)
      post = new HttpPost(basePath + "/heartbeat/testCourse1/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new HeartbeatReport(new Timestamp(), Challenge.gates, Position.DOCK))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Gate heartbeat", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // Heartbeat (follow)
      post = new HttpPost(basePath + "/heartbeat/testCourse1/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new HeartbeatReport(new Timestamp(), Challenge.follow, Position.FOUNDERS))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Obstacle heartbeat", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // Heartbeat (docking)
      post = new HttpPost(basePath + "/heartbeat/testCourse1/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new HeartbeatReport(new Timestamp(), Challenge.docking, Position.FOUNDERS))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Docking heartbeat", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // Docking
      resp = client.execute(new HttpGet(basePath + "/followLeader/testCourse1/AUVSI"));
      assertEquals("Docking", 200, resp.getStatusLine().getStatusCode());

      // Heartbeat (find)
      post = new HttpPost(basePath + "/heartbeat/testCourse1/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new HeartbeatReport(new Timestamp(), Challenge.path, Position.FOUNDERS))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Pinger heartbeat", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // Heartbeat (speed)
      post = new HttpPost(basePath + "/heartbeat/testCourse1/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new HeartbeatReport(new Timestamp(), Challenge.speed, Position.FOUNTAIN))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Interp heartbeat", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // Interop - upload
//      post = new HttpPost(basePath + "/docking/image/testCourse1/AUVSI");
//      post.setHeader("Content-Type", "multipart/form-data");
//      post.setEntity(MultipartEntityBuilder.create().addPart("file", new FileBody(new File("src/test/resources/test.jpg")))
//          .setMode(HttpMultipartMode.BROWSER_COMPATIBLE).build());
//      resp = client.execute(post);
//      System.out.println("**** ERR:" + IOUtil.toString(resp.getEntity().getContent()));
//      assertEquals("Interop upload", 200, resp.getStatusLine().getStatusCode());
//      System.out.println(resp.toString());
//      UploadStatus us = mapper.readValue(resp.getEntity().getContent(), UploadStatus.class);
//      assertNotNull(us);
//      resp.close();

      // Heartbeat (return)
      post = new HttpPost(basePath + "/heartbeat/testCourse1/AUVSI");
      post.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(new HeartbeatReport(new Timestamp(), Challenge.return_to_dock, Position.FOUNDERS))));
      post.setHeader("Content-Type", "application/json");
      resp = client.execute(post);
      assertEquals("Return heartbeat", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();

      // end run
      resp = client.execute(new HttpPost(basePath + "/run/end/testCourse1/AUVSI"));
      assertEquals("End run", 200, resp.getStatusLine().getStatusCode());
      assertEquals(new ReportStatus(true), mapper.readValue(resp.getEntity().getContent(), ReportStatus.class));
      resp.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
}
