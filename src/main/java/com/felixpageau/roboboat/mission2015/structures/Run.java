/**
 * 
 */
package com.felixpageau.roboboat.mission2015.structures;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.felixpageau.roboboat.mission2015.server.Event;
import com.google.common.base.Preconditions;

/**
 * @author felixpageau
 *
 */
public class Run {
    private final Date start;
    private final TeamCode team;
    private final List<Event> events;
    
    public Run(DateTime start, TeamCode team, List<Event> events) {
        this.start = Preconditions.checkNotNull(start).toDate();
        this.team = Preconditions.checkNotNull(team);
        this.events = Preconditions.checkNotNull(events);
    }
    
    public List<Event> getEvents() {
        return events;
    }
    
    public Date getStart() {
        return start;
    }
    
    public TeamCode getTeam() {
        return team;
    }
}
