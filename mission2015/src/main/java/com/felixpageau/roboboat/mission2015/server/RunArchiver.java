package com.felixpageau.roboboat.mission2015.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

  public List<Event> getEvents() {
    return ImmutableList.copyOf(events);
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void endRun() {
    if (endTime == null) {
      this.endTime = LocalDateTime.now();
      addEvent(new Event(this.endTime, String.format("%s - %s - End run", runSetup.getCourse(), runSetup.getActiveTeam())));
    }
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
    return Objects.toStringHelper(this).add("runSetup", runSetup).add("events", events).toString();
  }
}
