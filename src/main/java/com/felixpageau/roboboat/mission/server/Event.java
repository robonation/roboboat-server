package com.felixpageau.roboboat.mission.server;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Preconditions;

/**
 * Represent a RoboBoat competition server event
 */
@Immutable
@ThreadSafe
@ParametersAreNonnullByDefault
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Event {
  private final LocalDateTime time;
  private final String message;

  /**
   * Construct an {@link Event}
   * 
   * @param message
   *          the message of the event
   * @param time
   *          the event's timestamp
   */
  public Event(String message, LocalDateTime time) {
    this.message = Preconditions.checkNotNull(message);
    this.time = Preconditions.checkNotNull(time);
  }

  @Nonnull
  public LocalDateTime getTime() {
    return time;
  }

  @Nonnull
  public String getMessage() {
    return message;
  }
}
