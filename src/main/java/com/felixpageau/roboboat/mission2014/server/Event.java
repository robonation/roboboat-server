package com.felixpageau.roboboat.mission2014.server;

import jersey.repackaged.com.google.common.base.Objects;
import jersey.repackaged.com.google.common.base.Preconditions;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {
    private final DateTime time;
    private final String message;
    
    @JsonCreator
    public Event(
            @JsonProperty(value = "time") DateTime time, 
            @JsonProperty(value = "message") String message) {
        this.time = Preconditions.checkNotNull(time);
        this.message = Preconditions.checkNotNull(message);
    }
    
    public DateTime getTime() {
        return time;
    }
    
    public String getMessage() {
        return message;
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
        if (!(obj instanceof Event)) {
            return false;
        }
        Event other = (Event) obj;

        return Objects.equal(time, other.time)
                && Objects.equal(message, other.message);
    }
    
    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hashCode(time, message);
    }
    
    @JsonIgnore
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("time", Config.DATE_FORMATTER.get().format(time.toDate()))
                .add("message", message).toString();
    }
}
