package com.felixpageau.roboboat.mission.structures;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Represents a timestamp in the server
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonNullByDefault
@Immutable
@ThreadSafe
public class Timestamp {
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  private final LocalDateTime timestamp;

  @JsonCreator
  public Timestamp(String timestamp) {
    Preconditions.checkNotNull(timestamp);
    this.timestamp = LocalDateTime.from(DATE_FORMAT.parse(timestamp));
  }

  public Timestamp() {
    this.timestamp = LocalDateTime.now();
  }

  @JsonIgnore
  public long getTimeAsLong() {
    return timestamp.atOffset(ZoneOffset.UTC).toEpochSecond();
  }

  @JsonValue
  public String getTimestamp() {
    return timestamp.format(DATE_FORMAT);
  }

  @JsonIgnore
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null) return false;
    if (!(obj instanceof Timestamp)) return false;
    Timestamp other = (Timestamp) obj;
    return Objects.equal(other.timestamp, timestamp);
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(timestamp);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return timestamp.format(DATE_FORMAT);
  }
}
