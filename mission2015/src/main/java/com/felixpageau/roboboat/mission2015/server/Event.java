package com.felixpageau.roboboat.mission2015.server;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Event {
  private final LocalDateTime time;
  private final String message;

  public Event(String message) {
    this(LocalDateTime.now(), message);
  }

  @JsonCreator
  public Event(@JsonProperty(value = "time") LocalDateTime time, @JsonProperty(value = "message") String message) {
    this.time = Preconditions.checkNotNull(time);
    this.message = Preconditions.checkNotNull(message);
  }

  public LocalDateTime getTime() {
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

    return Objects.equal(time, other.time) && Objects.equal(message, other.message);
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(time, message);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("time", Config.DATE_FORMATTER.get().format(time)).add("message", message).toString();
  }
}
