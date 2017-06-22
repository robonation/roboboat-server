package com.felixpageau.roboboat.mission.server;

import static com.felixpageau.roboboat.mission.utils.FunctionalUtils.rethrow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission.App;
import com.felixpageau.roboboat.mission.structures.HeartbeatReport;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@ThreadSafe
@ParametersAreNonnullByDefault
@ReturnValuesAreNonNullByDefault
public final class RunArchiver {
  private static final Logger LOG = LoggerFactory.getLogger(RunArchiver.class);
  private final File f;
  private final LocalDateTime startTime;
  private final RunSetup runSetup;
  private final EventBus bus;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  @GuardedBy(value = "lock")
  private LocalDateTime endTime;
  @GuardedBy(value = "lock")
  private final List<Event> events = new ArrayList<>();
  @GuardedBy(value = "lock")
  private HeartbeatReport lastHeartbeat;
  @GuardedBy(value = "lock")
  private boolean requestedCarouselCode = false;
  @GuardedBy(value = "lock")
  private String uploadedImage;

  public RunArchiver(RunSetup runSetup, File f, EventBus bus) {
    this(runSetup, null, LocalDateTime.now(), null, null, false, null, f, bus);
  }

  @JsonCreator
  public RunArchiver(@JsonProperty(value = "runSetup") RunSetup runSetup, @JsonProperty(value = "events") @Nullable List<Event> events,
      @JsonProperty(value = "startTime") LocalDateTime startTime, @JsonProperty(value = "endTime") LocalDateTime endTime,
      @JsonProperty(value = "lastHeartbeat") HeartbeatReport lastHeartbeat, @JsonProperty(value = "requestedCarouselCode") boolean requestedCarouselCode,
      @JsonProperty(value = "uploadedImage") String uploadedImage) {
    this(runSetup, events, startTime, endTime, lastHeartbeat, requestedCarouselCode, uploadedImage,
        new File("tmp/competition-log." + Math.random()), new EventBus());
  }

  @SuppressFBWarnings(value = "EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS", justification = "Crashing the server with an unchecked exception is the right thing to do here")
  public RunArchiver(RunSetup runSetup, @Nullable List<Event> events, LocalDateTime startTime, @Nullable LocalDateTime endTime,
      @Nullable HeartbeatReport lastHeartbeat, boolean requestedCarouselCode,
      @Nullable String uploadedImage, File f, EventBus bus) {
    this.runSetup = Preconditions.checkNotNull(runSetup);
    this.startTime = Preconditions.checkNotNull(startTime);
    this.endTime = endTime;
    this.lastHeartbeat = lastHeartbeat;
    this.requestedCarouselCode = requestedCarouselCode;
    this.uploadedImage = uploadedImage;
    this.bus = Preconditions.checkNotNull(bus, "bus cannot be null");
    if (events != null && !events.isEmpty()) {
      this.events.addAll(events);
    }
    this.f = Preconditions.checkNotNull(f);
    LOG.error("RunArchiver.f = {}", f.getPath());
    if (!f.exists()) {
      try {
        if (!f.getParentFile().mkdirs() && !f.createNewFile()) {
          throw new RuntimeException(String.format("Unable to create run log file at (%s). Restart server", f.getAbsolutePath()));
        }
      } catch (IOException e) {
        throw new RuntimeException(String.format("Unable to create run log file at (%s). Restart server", f.getAbsolutePath()), e);
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

  public void addEvent(Event... events) {
    this.addEvent(Arrays.asList(Preconditions.checkNotNull(events)));
  }

  public void addEvent(List<Event> events) {
    Preconditions.checkNotNull(events);
    lock.writeLock().lock();
    try {
      this.events.addAll(events);
    } finally {
      lock.writeLock().unlock();
    }

    try (BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true), App.APP_CHARSET))) {
      events.stream().forEach(rethrow(event -> bf.append(event.toString()).append("\n")));
      events.stream().forEach(e -> bus.post(e));
      bf.flush();
    } catch (FileNotFoundException e) {
      LOG.error("Can't find log file", e);
    } catch (IOException e) {
      LOG.error("Can't write to log file", e);
    }
  }

  public void addHeartbeatEvent(Event event) {
    this.addEvent(Preconditions.checkNotNull(event));
  }

  /**
   * @param lastHeartbeat
   *          the lastHeartbeat to set
   */
  public void setLastHeartbeat(HeartbeatReport lastHeartbeat) {
    Preconditions.checkNotNull(lastHeartbeat, "The lastHeartbeat cannot be null");
    lock.writeLock().lock();
    try {
      this.lastHeartbeat = lastHeartbeat;
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * @return the lastHeartbeat
   */
  public Optional<HeartbeatReport> getLastHeartbeat() {
    lock.readLock().lock();
    try {
      return Optional.ofNullable(lastHeartbeat);
    } finally {
      lock.readLock().unlock();
    }
  }

  public void requestedCarouselCode() {
    lock.writeLock().lock();
    try {
      this.requestedCarouselCode = true;
    } finally {
      lock.writeLock().unlock();
    }
  }
  
  public boolean hasRequestedCarouselCode() {
    lock.writeLock().lock();
    try {
      return requestedCarouselCode;
    } finally {
      lock.writeLock().unlock();
    }
  }

  public Optional<String> getUploadedImage() {
    lock.readLock().lock();
    try {
      return Optional.ofNullable(uploadedImage);
    } finally {
      lock.readLock().unlock();
    }
  }

  public void uploadedImage(String path) {
    Preconditions.checkNotNull(path, "The path cannot be null");
    lock.writeLock().lock();
    try {
      this.uploadedImage = path;
    } finally {
      lock.writeLock().unlock();
    }
  }

  public List<Event> getEvents() {
    lock.readLock().lock();
    try {
      return ImmutableList.copyOf(events);
    } finally {
      lock.readLock().unlock();
    }
  }

  public boolean endRun(Event event) {
    lock.writeLock().lock();
    try {
      if (endTime == null) {
        this.addEvent(event);
        endTime = event.getTime();
        return true;
      }
      return false;
    } finally {
      lock.writeLock().unlock();
    }
  }

  public LocalDateTime getEndTime() {
    lock.readLock().lock();
    try {
      return endTime;
    } finally {
      lock.readLock().unlock();
    }
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

    lock.readLock().lock();
    try {
      return Objects.equal(runSetup, other.runSetup) && Objects.equal(events, other.events);
    } finally {
      lock.readLock().unlock();
    }
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    lock.readLock().lock();
    try {
      return Objects.hashCode(runSetup, events);
    } finally {
      lock.readLock().unlock();
    }
  }

  @JsonIgnore
  @Override
  public String toString() {
    lock.readLock().lock();
    try {
      return MoreObjects.toStringHelper(this).add("runSetup", runSetup).add("events", events).toString();
    } finally {
      lock.readLock().unlock();
    }
  }
}
