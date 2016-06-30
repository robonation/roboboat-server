/**
 * 
 */
package com.felixpageau.roboboat.mission.server;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission.structures.Challenge;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * A detail rich event happening in the a competition run.
 */
@Immutable
@ThreadSafe
@ParametersAreNonnullByDefault
public class StructuredEvent extends Event {
  private final Course course;
  private final TeamCode team;
  private final Challenge challenge;

  /**
   * @param message
   */
  public StructuredEvent(Course course, TeamCode team, Challenge challenge, String message) {
    this(course, team, challenge, LocalDateTime.now(), message);
  }

  /**
   * @param message
   */
  @JsonCreator
  public StructuredEvent(@JsonProperty(value = "course") Course course, @JsonProperty(value = "team") TeamCode team,
      @JsonProperty(value = "challenge") Challenge challenge, @JsonProperty(value = "time") LocalDateTime time, @JsonProperty(value = "message") String message) {
    super(String.format("%s - %s - %s - %s - %s", time.format(Config.DATE_FORMATTER.get()), course, team, challenge, message), time);
    this.course = Preconditions.checkNotNull(course, "The provided course cannot be null");
    this.team = Preconditions.checkNotNull(team, "The provided team cannot be null");
    this.challenge = Preconditions.checkNotNull(challenge, "The provided challenge cannot be null");
  }

  /**
   * @return the course
   */
  @Nonnull
  public Course getCourse() {
    return course;
  }

  /**
   * @return the team
   */
  @Nonnull
  public TeamCode getTeam() {
    return team;
  }

  /**
   * @return the challenge
   */
  @Nonnull
  public Challenge getChallenge() {
    return challenge;
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
    if (!(obj instanceof StructuredEvent)) {
      return false;
    }
    StructuredEvent other = (StructuredEvent) obj;

    return Objects.equal(course, other.course) && Objects.equal(team, other.team) && Objects.equal(getMessage(), other.getMessage())
        && Objects.equal(getTime(), other.getTime()) && Objects.equal(challenge, other.challenge);
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(course, team, challenge, getTime(), getMessage());
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("course", course).add("team", team).add("challenge", challenge).add("time", getTime())
        .add("message", getMessage()).toString();
  }
}
