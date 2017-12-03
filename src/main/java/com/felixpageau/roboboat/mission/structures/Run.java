/**
 * 
 */
package com.felixpageau.roboboat.mission.structures;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.felixpageau.roboboat.mission.server.Event;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Represent a single run on a single course by a {@link TeamCode}.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
@ReturnValuesAreNonNullByDefault
@Immutable
public class Run {
  private final LocalDateTime start;
  private final TeamCode team;
  private final List<Event> events;
  private final long timestamp;
  private final String runId;

  public Run(LocalDateTime start, TeamCode team, String runId, List<Event> events) {
    this.start = Preconditions.checkNotNull(start);
    this.team = Preconditions.checkNotNull(team);
    this.events = ImmutableList.copyOf(Preconditions.checkNotNull(events));
    this.timestamp = start.toEpochSecond(ZoneOffset.UTC);
    this.runId = runId;
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

  public long getTimestamp() {
    return timestamp;
  }

  public String getRunId() {
    return runId;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(start, team, events);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Run))
      return false;
    Run other = (Run) obj;
    return Objects.equal(start, other.start) && Objects.equal(team, other.team) && Objects.equal(events, other.events);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("start", start).add("team", team).add("events", events).toString();
  }
}
