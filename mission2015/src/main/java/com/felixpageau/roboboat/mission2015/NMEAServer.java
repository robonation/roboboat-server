package com.felixpageau.roboboat.mission2015;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.felixpageau.roboboat.mission2015.nmea.NMEASentence;
import com.felixpageau.roboboat.mission2015.nmea.SentenceRegistry;
import com.felixpageau.roboboat.mission2015.server.Competition;
import com.google.common.base.Preconditions;

@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public class NMEAServer implements Runnable {
  private final int port;
  private final SentenceRegistry registry;
  private final AtomicBoolean abort = new AtomicBoolean(false);
  private final AtomicBoolean debug = new AtomicBoolean(false);
  private final Competition competition;
  private final Executor executor = Executors.newFixedThreadPool(50);

  public NMEAServer(Competition competition, int port, SentenceRegistry registry) {
    Preconditions.checkArgument(port > 0, "port must be > 0");
    this.competition = Preconditions.checkNotNull(competition, "competition cannot be null");
    this.registry = Preconditions.checkNotNull(registry, "registry cannot be null");
    this.port = port;
  }

  @Override
  public void run() {
    try (ServerSocket ss = new ServerSocket(port)) {
      while (!abort.get()) {
        Socket client = ss.accept();
        System.out.println("New NMEA connection open on client socket" + client.getLocalPort());
        executor.execute(new NMEAWorker(competition, registry, client));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start() {
    executor.execute(this);
    System.out.println("\n\nNMEA Server started on:" + getPort());
  }

  public void stop() {
    abort.set(true);
    System.out.println("\n\nNMEA Server stopping on:" + getPort());
  }

  public int getPort() {
    return port;
  }

  private class NMEAWorker implements Runnable {
    private final Socket s;
    private final SentenceRegistry registry;
    private final Competition competition;

    public NMEAWorker(Competition competition, SentenceRegistry registry, Socket s) {
      this.s = Preconditions.checkNotNull(s, "socket cannot be null");
      this.registry = Preconditions.checkNotNull(registry, "registry cannot be null");
      this.competition = Preconditions.checkNotNull(competition, "competition cannot be null");
    }

    @Override
    public void run() {
      try (BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
          BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
        while (!abort.get()) {
          String line = r.readLine();
          try {
            NMEASentence sentence = NMEASentence.parse(registry, line);
            System.out.println("Do something with " + sentence);
          } catch (IllegalArgumentException e) {
            if (debug.get()) {
              w.write(e.getMessage());
            }
            System.out.print(e.getMessage());
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
