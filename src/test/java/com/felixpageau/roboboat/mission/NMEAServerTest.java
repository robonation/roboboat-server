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
      w.write("$SVHRT,courseA,AUVSI,20150306061030,obstacles,40.689249,-74.044500*1D\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDHRT"));
      assertTrue(line.contains("true"));

      // Obstacle
      w.write("$SVOBS,courseA,AUVSI*5F\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDOBS"));
      assertTrue(line.matches(".*[1-3]\\,[X-Z]\\*.*"));

      // Heartbeat (dock)
      w.write("$SVHRT,courseA,AUVSI,20150306061030,docking,40.689249,-74.044500*0C\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDHRT"));
      assertTrue(line.contains("true"));

      // Dock
      w.write("$SVDOC,courseA,AUVSI*49\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDDOC"));
      assertTrue(line.matches(".*(\\,(circle|triangle|cruciform)\\,(yellow|blue|black|green|red)){2}.*"));

      // Heartbeat (pinger)
      w.write("$SVHRT,courseA,AUVSI,20150306061030,pinger,40.689249,-74.044500*68\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDHRT"));
      assertTrue(line.contains("true"));

      // Pinger
      w.write("$SVPIN,courseA,AUVSI,red,24,black,36*41\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDPIN"));
      assertTrue(line.matches(".*(true|false).*"));

      // Heartbeat (interop)
      w.write("$SVHRT,courseA,AUVSI,20150306061030,interop,40.689249,-74.044500*14\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDHRT"));
      assertTrue(line.contains("true"));

      // Dock
      w.write("$SVUAV,courseA,AUVSI,eight,a4aa8224-07f2-4b57-a03a-c8887c2505c7*30\n");
      w.flush();
      line = readLine(r);
      assertTrue(line.contains("TDUAV"));
      assertTrue(line.matches(".*(true|false).*"));

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
