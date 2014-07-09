package com.felixpageau.roboboat.mission2014.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jersey.repackaged.com.google.common.base.Objects;
import jersey.repackaged.com.google.common.base.Preconditions;
import jersey.repackaged.com.google.common.collect.ImmutableList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RunArchiver {
    private final Date startTime;
    private Date endTime;
    private final RunSetup runSetup;
    private final List<Event> events = new ArrayList<>();

    public RunArchiver(RunSetup runSetup) {
        this(runSetup, null, now());
    }
    
    @JsonCreator
    public RunArchiver(
            @JsonProperty(value = "runSetup") RunSetup runSetup,
            @JsonProperty(value = "events") List<Event> events,
            @JsonProperty(value = "startTime") Date startTime) {
        this.runSetup = Preconditions.checkNotNull(runSetup);
        this.startTime = Preconditions.checkNotNull(startTime);
        if (events != null && !events.isEmpty()) {
            this.events.addAll(events);
        }
    }
    
    public RunSetup getRunSetup() {
        return runSetup;
    }
    
    public void addEvent(Event event) {
        Preconditions.checkNotNull(event);
        this.events.add(event);
    }
    
    public List<Event> getEvents() {
        return ImmutableList.copyOf(events);
    }
    
    public Date getEndTime() {
        return endTime;
    }
    
    public Date getStartTime() {
        return startTime;
    }
    
    public void endRun() {
        this.endTime = now();
        //TODO 
    }
    
    private static final Date now(){
        return new Date(Calendar.getInstance(Config.TIME_ZONE).getTime().getTime());
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

        return Objects.equal(runSetup, other.runSetup)
                && Objects.equal(events, other.events);
    }
    
    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hashCode(runSetup, events);
    }
    
    @JsonIgnore
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("runSetup", runSetup)
                .add("events", events).toString();
    }
}
