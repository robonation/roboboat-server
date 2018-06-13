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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.felixpageau.roboboat.mission.server.CourseLayout;
import com.felixpageau.roboboat.mission.server.RunSetup;
import com.felixpageau.roboboat.mission.structures.LeaderSequence;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.google.common.base.Preconditions;

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
    return e.submit(new CarouselCall(layout, setup));
  }

  /* (non-Javadoc)
   * @see com.felixpageau.roboboat.mission.obstacle.client.ObstacleClient#turnOff(java.util.concurrent.Executor, com.felixpageau.roboboat.mission.server.RunSetup)
   */
  @Override
  public Future<ReportStatus> turnOff(ExecutorService e, CourseLayout layout, RunSetup setup) {
    return e.submit(new CarouselCall(layout, RunSetup.NO_RUN));
  }

  private static class CarouselCall implements Callable<ReportStatus> {
    private final CourseLayout layout;
    private final RunSetup newSetup;

    public CarouselCall(CourseLayout layout, RunSetup newSetup) {
      this.layout = Preconditions.checkNotNull(layout, "layout cannot be null");
      this.newSetup = Preconditions.checkNotNull(newSetup, "newSetup cannot be null");
    }

    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY")
    @Override
    public ReportStatus call() throws Exception {
      Socket s = new Socket(layout.getCarouselControlServer().getHost(), layout.getCarouselControlServer().getPort());
      boolean activated = false;
      String error = null;

      try (BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
          Writer w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
        
        if (LeaderSequence.none.equals(newSetup.getActiveLeaderSequence())) {
          w.write("22");
        } else {
          w.write("11");
        }
        w.flush();
        char[] chars = new char[8];
        r.read(chars);

        if (chars[0] == 0) {
          LOG.info("Deactivated Carousel");
        } else {
          activated = true;
          LOG.info("Activated Carousel");
        }
      } catch (UnknownHostException e) {
        error = String.format("Failed to find pinger server (%s) due to: %s", layout.getPingerControlServer().toString(), e.getMessage());
        LOG.error(error, e);
      } catch (IOException e) {
        error = String.format("Comm failed with pinger server (%s) due to: %s",layout.getPingerControlServer().toString(), e.getMessage());
        LOG.error(error, e);
      } finally {
        s.close();
      }
      return new ReportStatus(activated, error);
    }
  }

}
