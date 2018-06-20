/**
 * 
 */
package com.felixpageau.roboboat.mission.obstacle.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.felixpageau.roboboat.mission.server.CourseLayout;
import com.felixpageau.roboboat.mission.server.RunSetup;
import com.felixpageau.roboboat.mission.structures.LeaderSequence;
import com.felixpageau.roboboat.mission.structures.ReportStatus;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Client to turn on/off Channel 1 on the SR-201 used for Carousel Control in
 * 2018
 * 
 * @author felixpageau
 */
public class CarouselSR201 implements ObstacleClient {
  private static final Logger LOG = LoggerFactory.getLogger(CarouselSR201.class);

  /* (non-Javadoc)
   * @see com.felixpageau.roboboat.mission.obstacle.client.ObstacleClient#activate(java.util.concurrent.Executor, com.felixpageau.roboboat.mission.server.RunSetup)
   */
  @Override
  public Future<ReportStatus> activate(ExecutorService e, CourseLayout layout, RunSetup setup) {
    // TODO Auto-generated method stub
    return e.submit(new CarouselCall(layout.getCarouselControlServer().getHost(), layout.getCarouselControlServer().getPort(), setup.getActiveLeaderSequence()));
  }

  /* (non-Javadoc)
   * @see com.felixpageau.roboboat.mission.obstacle.client.ObstacleClient#turnOff(java.util.concurrent.Executor, com.felixpageau.roboboat.mission.server.RunSetup)
   */
  @Override
  public Future<ReportStatus> turnOff(ExecutorService e, CourseLayout layout, RunSetup setup) {
    return e.submit(new CarouselCall(layout.getCarouselControlServer().getHost(), layout.getCarouselControlServer().getPort(), LeaderSequence.none));
  }

  private static class CarouselCall implements Callable<ReportStatus> {
    private final String host;
    private int port;
    private LeaderSequence sequence;

    public CarouselCall(String host, int port, LeaderSequence sequence) {
      this.host = host;
      this.port = port;
      this.sequence = sequence;
    }

    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY")
    @Override
    public ReportStatus call() throws Exception {
      Socket s = new Socket(host, port);
      boolean activated = false;
      String error = null;

      try (BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
          Writer w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
        
        if (LeaderSequence.none.equals(sequence)) {
          LOG.info("Deactivating carousel...");
          w.write("11");
          //w.write("00");
        } else {
          LOG.info("Activating carousel...");
          w.write("11");
        }
        w.flush();
        char[] chars = new char[8];
        r.read(chars);

        LOG.info("Carousel response: " + new String(chars));
        if (chars[0] == 0) {
          LOG.info("Deactivated Carousel");
        } else {
          activated = true;
          LOG.info("Activated Carousel");
        }
      } catch (UnknownHostException e) {
        error = String.format("Failed to find pinger server (%s:%d) due to: %s", host, port, e.getMessage());
        LOG.error(error, e);
      } catch (IOException e) {
        error = String.format("Comm failed with pinger server (%s:%d) due to: %s",host, port, e.getMessage());
        LOG.error(error, e);
      } finally {
        s.close();
      }
      return new ReportStatus(activated, error);
    }
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    ExecutorService exec = Executors.newFixedThreadPool(1);
    Future<ReportStatus> f = exec.submit(new CarouselCall("192.168.1.23", 6722, LeaderSequence.none));
    System.out.println(f.get());
  }
}
