/**
 * 
 */
package com.felixpageau.roboboat.mission2015.structures;

import java.time.LocalDateTime;
import java.util.List;

import com.felixpageau.roboboat.mission2015.server.Event;
import com.google.common.base.Preconditions;

/**
 * @author felixpageau
 *
 */
public class Run {
  private final LocalDateTime start;
  private final TeamCode team;
  private final List<Event> events;

  public Run(LocalDateTime start, TeamCode team, List<Event> events) {
    this.start = Preconditions.checkNotNull(start);
    this.team = Preconditions.checkNotNull(team);
    this.events = Preconditions.checkNotNull(events);
  }

  public List<Event> getEvents() {
    return events;
  }

  public LocalDateTime getStart() {
    return start;
  }

  public TeamCode getTeam() {
    return team;
  }
}
