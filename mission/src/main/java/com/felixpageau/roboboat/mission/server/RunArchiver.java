package com.felixpageau.roboboat.mission.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.CheckForNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission.structures.BeaconReport;
import com.felixpageau.roboboat.mission.structures.HeartbeatReport;
import com.felixpageau.roboboat.mission.structures.InteropReport;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

public class RunArchiver {
  private final File f;
  private final EventBus eventBus;
  private final LocalDateTime startTime;
  private LocalDateTime endTime;
  private final RunSetup runSetup;
  private final List<Event> events = new ArrayList<>();
  private final AtomicReference<HeartbeatReport> lastHeartbeat = new AtomicReference<>();
  private final AtomicBoolean requestedGateCode = new AtomicBoolean();
  private final AtomicBoolean requestedDockingSequence = new AtomicBoolean();
  private final AtomicReference<BeaconReport> reportedPinger = new AtomicReference<>();
  private final AtomicBoolean requestedImageListing = new AtomicBoolean();
  private final AtomicBoolean requestedImage = new AtomicBoolean();
  private final AtomicReference<String> uploadedImage = new AtomicReference<>();
  private final AtomicReference<InteropReport> reportedInterop = new AtomicReference<>();

  public RunArchiver(RunSetup runSetup, File f, EventBus eventBus) {
    this(runSetup, null, LocalDateTime.now(), f, eventBus);
  }

  @JsonCreator
  public RunArchiver(@JsonProperty(value = "runSetup") RunSetup runSetup, @JsonProperty(value = "events") List<Event> events,
      @JsonProperty(value = "startTime") LocalDateTime startTime) {
    this(runSetup, events, startTime, new File("competition-log." + Math.random()), new EventBus());
  }

  public RunArchiver(RunSetup runSetup, List<Event> events, LocalDateTime startTime, File f, EventBus eventBus) {
    this.runSetup = Preconditions.checkNotNull(runSetup);
    this.startTime = Preconditions.checkNotNull(startTime);
    if (events != null && !events.isEmpty()) {
      this.events.addAll(events);
    }
    this.eventBus = Preconditions.checkNotNull(eventBus);
    this.f = Preconditions.checkNotNull(f);
    if (!f.exists()) {
      try {
        f.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @JsonIgnore
  public File getF() {
    return f;
  }

  public RunSetup getRunSetup() {
    return runSetup;
  }

  public void addEvent(Event event) {
    Preconditions.checkNotNull(event);
    this.events.add(event);
    try (BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)))) {
      bf.append(event.toString()).append("\n");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    eventBus.post(event);
    System.out.println(event);

  }

  public void addHeartbeatEvent(Event event) {
    Preconditions.checkNotNull(event);
    this.events.add(event);
    try (BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)))) {
      bf.append(event.toString()).append("\n");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    eventBus.post(event);
    System.out.println(event);

  }

  /**
   * @return the lastHeartbeat
   */
  @CheckForNull
  public Optional<HeartbeatReport> getLastHeartbeat() {
    return Optional.ofNullable(lastHeartbeat.get());
  }

  /**
   * @param lastHeartbeat
   *          the lastHeartbeat to set
   */
  public void setLastHeartbeat(HeartbeatReport lastHeartbeat) {
    Preconditions.checkNotNull(lastHeartbeat, "The lastHeartbeat cannot be null");
    this.lastHeartbeat.set(lastHeartbeat);
  }

  public boolean hasRequestedGateCode() {
    return requestedGateCode.get();
  }

  public void requestedGateCode() {
    this.requestedGateCode.set(true);
  }

  public boolean hasRequestedDockingSequence() {
    return requestedDockingSequence.get();
  }

  public void requestedDockingSequence() {
    this.requestedDockingSequence.set(true);
  }

  public Optional<BeaconReport> getReportedPinger() {
    return Optional.ofNullable(reportedPinger.get());
  }

  public void reportPinger(BeaconReport report) {
    this.reportedPinger.compareAndSet(null, report);
  }

  public boolean hasRequestedImageListing() {
    return requestedImageListing.get();
  }

  public void requestedImageListing() {
    this.requestedImageListing.set(true);
  }

  public boolean hasRequestedImage() {
    return requestedImage.get();
  }

  public void requestedImage() {
    this.requestedImage.set(true);
  }

  public Optional<String> getUploadedImage() {
    return Optional.ofNullable(uploadedImage.get());
  }

  public void uploadedImage(String path) {
    this.uploadedImage.compareAndSet(null, path);
  }

  public Optional<InteropReport> getReportedInterop() {
    return Optional.ofNullable(reportedInterop.get());
  }

  public void reportInterop(InteropReport report) {
    this.reportedInterop.compareAndSet(null, report);
  }

  public List<Event> getEvents() {
    return ImmutableList.copyOf(events);
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  @JsonIgnore
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof RunArchiver)) {
      return false;
    }
    RunArchiver other = (RunArchiver) obj;

    return Objects.equal(runSetup, other.runSetup) && Objects.equal(events, other.events);
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(runSetup, events);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("runSetup", runSetup).add("events", events).toString();
  }
}
