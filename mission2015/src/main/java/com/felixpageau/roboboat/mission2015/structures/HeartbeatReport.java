package com.felixpageau.roboboat.mission2015.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class HeartbeatReport {
  private final Timestamp timestamp;
  private final Challenge challenge;
  private final Position position;

  @JsonCreator
  public HeartbeatReport(@JsonProperty(value = "timestamp") Timestamp timestamp, @JsonProperty(value = "challenge") Challenge challenge,
      @JsonProperty(value = "position") Position position) {
    this.timestamp = Preconditions.checkNotNull(timestamp);
    this.challenge = Preconditions.checkNotNull(challenge);
    this.position = Preconditions.checkNotNull(position);
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public Challenge getChallenge() {
    return challenge;
  }

  public Position getPosition() {
    return position;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("timestamp", timestamp).add("challenge", challenge).add("position", position).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(timestamp, challenge, position);
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
    if (obj instanceof HeartbeatReport) {
      HeartbeatReport other = (HeartbeatReport) obj;
      return Objects.equal(timestamp, other.timestamp) && Objects.equal(challenge, other.challenge) && Objects.equal(position, other.position);
    }
    return false;
  }
}
