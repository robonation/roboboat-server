package com.felixpageau.roboboat.mission;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.Test;

public class NMEAServerTest {
  private static final int port = 6666;

  @BeforeClass
  public static void setUp() throws MalformedURLException, URISyntaxException {
    MissionResourceConfig.port.set(port);
    new MissionResourceConfig();
  }

  public void otherTest() {
    assertEquals("abc", "def");
  }

  public String heartbeat() {
    return "";
  }

  public String readLine(BufferedReader r) throws IOException {
    String line = null;
    while (line == null) {
      line = r.readLine();
    }
    return line;
  }

  @Test
  public void test() {
    try (// Socket s = new Socket("ec2-52-7-253-202.compute-1.amazonaws.com",
         // 9999);
    Socket s = new Socket("localhost", port);
        BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
      String line;

      // End run
      w.write("$SVEND,courseA,AUVSI*4E\n");
      w.flush();
      line = readLine(r);
      assertEquals("$TDEND,true*65", line);

      // Start run
      w.write("$SVSTR,courseA,AUVSI*54\n");
      w.flush();
      line = readLine(r);
      assertEquals("$TDSTR,true*7F", line);

      // Heartbeat (gates)
      w.write("$SVHRT,courseA,AUVSI,20150306061030,gates,40.689249,-74.044500*0B\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDHRT"));
      assertTrue(line.contains("true"));

      // Heartbeat (with invalid target)
      w.write("$SVHRT,courseA,AUVSI,20150306061030,gates2,40.689249,-74.044500*39\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("ERROR"));

      // Heartbeat (obstacles)
      w.write("$SVHRT,courseA,AUVSI,20150306061030,follow,40.689249,-74.044500*7E\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDHRT"));
      assertTrue(line.contains("true"));

      // Heartbeat (dock)
      w.write("$SVHRT,courseA,AUVSI,20150306061030,docking,40.689249,-74.044500*0C\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDHRT"));
      assertTrue(line.contains("true"));

      // Dock
      w.write("$SVFOL,courseA,AUVSI*44\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDFOL"));
      assertTrue(line.matches(".*[1-4]{2}.*"));

      // Heartbeat (path)
      w.write("$SVHRT,courseA,AUVSI,20150306061030,path,40.689249,-74.044500*62\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDHRT"));
      assertTrue(line.contains("true"));

      // Heartbeat (return)
      w.write("$SVHRT,courseA,AUVSI,20150306061030,return,40.689249,-74.044500*65\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDHRT"));
      assertTrue(line.contains("true"));

      // End run
      w.write("$SVEND,courseA,AUVSI*4E\n");
      w.flush();
      line = readLine(r);
      assertEquals("$TDEND,true*65", line);
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
