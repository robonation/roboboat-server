/**
 * 
 */
package com.felixpageau.roboboat.mission.obstacle.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.felixpageau.roboboat.mission.App;
import com.felixpageau.roboboat.mission.server.CourseLayout;
import com.felixpageau.roboboat.mission.server.RunSetup;
import com.felixpageau.roboboat.mission.structures.Code;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.felixpageau.roboboat.mission.utils.NMEAUtils;
import com.google.common.base.Preconditions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author felixpageau
 *
 */
public class PingerClient implements ObstacleClient {
  private static final Logger LOG = LoggerFactory.getLogger(PingerClient.class);
  
  @Override
  public Future<ReportStatus> activate(ExecutorService e, CourseLayout layout, RunSetup setup) {
    return e.submit(new ActivatePinger(layout, setup));
  }

  /* (non-Javadoc)
   * @see com.felixpageau.roboboat.mission.obstacle.client.ObstacleClient#turnOff(java.util.concurrent.Executor, com.felixpageau.roboboat.mission.server.RunSetup)
   */
  @Override
  public Future<ReportStatus> turnOff(ExecutorService e, CourseLayout layout, RunSetup setup) {
    return e.submit(new ActivatePinger(layout, RunSetup.NO_RUN));
  }
  
  
  private static class ActivatePinger implements Callable<ReportStatus> {
    private final CourseLayout layout;
    private final RunSetup newSetup;

    public ActivatePinger(CourseLayout layout, RunSetup newSetup) {
      this.layout = Preconditions.checkNotNull(layout, "layout cannot be null");
      this.newSetup = Preconditions.checkNotNull(newSetup, "newSetup cannot be null");
    }

    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY")
    @Override
    public ReportStatus call() throws Exception {
      boolean activated = false;
      String error = null;
      for (int j = 0; j < 10 && !activated; j++) {
        try (Socket s = new Socket(layout.getPingerControlServer().getHost(), layout.getPingerControlServer().getPort());
            Writer w = new OutputStreamWriter(s.getOutputStream(), App.APP_CHARSET);
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream(), App.APP_CHARSET))) {
          
          LOG.error("*** Pinger value in setup: " + newSetup);
          LOG.error("*** Pinger value in docking sequence: " + newSetup.getActiveDockingSequence());
          LOG.error("*** Pinger value in active pinger: " + newSetup.getActiveDockingSequence().getActivePinger());
          
          error = null;
          if (com.felixpageau.roboboat.mission.structures.Code.none.equals(newSetup.getActiveDockingSequence().getActivePinger())) {
            String pingerActivationMessage = NMEAUtils.formatNMEAmessage(NMEAUtils.formatPingerNMEAmessage(layout.getCourse(), 0));
            w.write(pingerActivationMessage);
            System.out.println("TURN OFF PINGER: " + pingerActivationMessage);
            w.flush();
            activated = true;
            LOG.info("Turned off Pinger");
          } else {
            System.out.println(String.format("** Available pingers: %s **", Arrays.stream(Code.values()).map(x -> x.getValue()).collect(Collectors.joining())));
            System.out.println(String.format("** Active pingers: %s **", newSetup.getActiveDockingSequence().getActivePinger().getValue()));
            int activate = Integer.parseInt(newSetup.getActiveDockingSequence().getActivePinger().getValue());

            System.out.println("**** Activation values: " + activate);
            String pingerActivationMessage = NMEAUtils.formatNMEAmessage(NMEAUtils.formatPingerNMEAmessage(layout.getCourse(), activate));
            w.write(pingerActivationMessage);
            System.out.println(pingerActivationMessage);
            w.flush();
            activated = true;
            LOG.info("Activated Pinger");
          }
          System.out.println(r.readLine());
          System.out.println(r.readLine());
        } catch (UnknownHostException e) {
          error = String.format("Failed to find pinger server (%s) due to: %s", layout.getPingerControlServer().toString(), e.getMessage());
          LOG.error(error, e);
        } catch (IOException e) {
          error = String.format("Comm failed with pinger server (%s) due to: %s", layout.getPingerControlServer().toString(), e.getMessage());
          LOG.error(error, e);
        }
      }
      return new ReportStatus(activated, error);
    }
  }
}
