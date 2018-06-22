/**
 * 
 */
package com.felixpageau.roboboat.mission.obstacle.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.felixpageau.roboboat.mission.server.CourseLayout;
import com.felixpageau.roboboat.mission.server.RunSetup;
import com.felixpageau.roboboat.mission.structures.LeaderSequence;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Client to turn on/off Channel 1 on the SR-201 used for Carousel Control in
 * 2018
 * 
 * @author felixpageau
 */
public class CarouselSR201 implements ObstacleClient {
  private static final Logger LOG = LoggerFactory.getLogger(CarouselSR201.class);
  private static final LoadingCache<URL, Socket> SOCKET_CACHE = CacheBuilder.newBuilder()
      .maximumSize(1000)
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .build(
          new CacheLoader<URL, Socket>() {
            public Socket load(URL url) throws Exception {
              System.out.println("Creating new socket");
              Socket s = new Socket(url.getHost(), url.getPort());
              s.setKeepAlive(true);
              return s;
            }
          });

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
      Socket s = null;
      while (s == null) {
        s = SOCKET_CACHE.get(new URL("https",host,port,""));
        if (!s.isConnected()) {
          SOCKET_CACHE.invalidate(s);
          s = null;
        }
      }
      
      boolean actuated = false;
      String error = null;
      try {
        
        //"11": Turn on channel 1
        //"12": Turn on channel 2
        //"21": Turn off channel 1
        //"22": Turn off channel 2
        
        //DO NOT CLOSE Streams/Reader/Writers, the sockets are re-used
        Reader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
        Writer w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        
        if (LeaderSequence.none.equals(sequence)) {
          LOG.info("Deactivating carousel...");
          w.write("21");
        } else {
          LOG.info("Activating carousel...");
          w.write("11");
        }
        w.flush();
        char[] chars = new char[8];
        r.read(chars);

        LOG.debug("Carousel response: " + new String(chars));
        if (chars[0] == '0' && LeaderSequence.none.equals(sequence)) {
          actuated = true;
          LOG.info("Deactivated Carousel");
        } else if (chars[0] == '1' && !LeaderSequence.none.equals(sequence)) {
          actuated = true;
          LOG.info("Activated Carousel");
        }
      } catch (UnknownHostException e) {
        error = String.format("Failed to find pinger server (%s:%d) due to: %s", host, port, e.getMessage());
        LOG.error(error, e);
      } catch (IOException e) {
        error = String.format("Comm failed with pinger server (%s:%d) due to: %s",host, port, e.getMessage());
        LOG.error(error, e);
      }
      return new ReportStatus(actuated, error);
    }
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    ExecutorService exec = Executors.newFixedThreadPool(1);
    Future<ReportStatus> f = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.v12));
    Future<ReportStatus> f2 = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.none));
    Future<ReportStatus> f3 = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.v12));
    Future<ReportStatus> f4 = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.none));
    Future<ReportStatus> f5 = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.v12));
    Future<ReportStatus> f6 = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.none));
    Future<ReportStatus> f7 = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.v12));
    Future<ReportStatus> f8 = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.none));
    Future<ReportStatus> f9 = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.v12));
    Future<ReportStatus> f10 = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.none));
    Future<ReportStatus> f11 = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.v12));
    Future<ReportStatus> f12 = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.none));
    Future<ReportStatus> f13 = exec.submit(new CarouselCall("127.0.0.1", 6722, LeaderSequence.v12));
    //Future<ReportStatus> f = exec.submit(new CarouselCall("192.168.1.33", 6722, LeaderSequence.v12));
    System.out.println(f.get());
    exec.shutdownNow();
  }
}
