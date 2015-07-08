/**
 * 
 */
package com.felixpageau.roboboat.mission2015.server;

import java.time.LocalDateTime;

import com.felixpageau.roboboat.mission2015.structures.Challenge;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.google.common.base.Preconditions;

/**
 * @author felixpageau
 *
 */
public class StructuredEvent extends Event {
  private final Course course;
  private final TeamCode team;
  private final Challenge challenge;
  private final LocalDateTime time;
  private final String message;

  /**
   * @param message
   */
  public StructuredEvent(Course course, TeamCode team, Challenge challenge, String message) {
    this(course, team, challenge, LocalDateTime.now(), message);
  }

  /**
   * @param message
   */
  public StructuredEvent(Course course, TeamCode team, Challenge challenge, LocalDateTime time, String message) {
    super(String.format("%s - %s - %s - %s - %s", time.format(Config.DATE_FORMATTER.get()), course, team, challenge, message));
    this.course = Preconditions.checkNotNull(course, "The provided course cannot be null");
    this.team = Preconditions.checkNotNull(team, "The provided team cannot be null");
    this.challenge = Preconditions.checkNotNull(challenge, "The provided challenge cannot be null");
    this.time = Preconditions.checkNotNull(time, "The provided time cannot be null");
    this.message = Preconditions.checkNotNull(message, "The provided message cannot be null");
  }

  /**
   * @return the course
   */
  public Course getCourse() {
    return course;
  }

  /**
   * @return the team
   */
  public TeamCode getTeam() {
    return team;
  }

  /**
   * @return the challenge
   */
  public Challenge getChallenge() {
    return challenge;
  }

  /**
   * @return the time
   */
  @Override
  public LocalDateTime getTime() {
    return time;
  }

  /**
   * @return the message
   */
  @Override
  public String getMessage() {
    return message;
  }
}
