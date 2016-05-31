/**
 * 
 */
package com.felixpageau.roboboat.mission.structures;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.felixpageau.roboboat.mission.server.Event;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Represent a single run on a single course by a {@link TeamCode}.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
@Immutable
public class Run {
  private final LocalDateTime start;
  private final TeamCode team;
  private final List<Event> events;

  public Run(LocalDateTime start, TeamCode team, List<Event> events) {
    this.start = Preconditions.checkNotNull(start);
    this.team = Preconditions.checkNotNull(team);
    this.events = ImmutableList.copyOf(Preconditions.checkNotNull(events));
  }

  @Nonnull
  public List<Event> getEvents() {
    return events;
  }

  @Nonnull
  public LocalDateTime getStart() {
    return start;
  }

  @Nonnull
  public TeamCode getTeam() {
    return team;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(start, team, events);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null) return false;
    if (!(obj instanceof Run)) return false;
    Run other = (Run) obj;
    return Objects.equal(start, other.start) && Objects.equal(team, other.team) && Objects.equal(events, other.events);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("start", start).add("team", team).add("events", events).toString();
  }
}
