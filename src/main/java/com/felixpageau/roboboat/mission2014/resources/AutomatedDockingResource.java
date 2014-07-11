package com.felixpageau.roboboat.mission2014.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.felixpageau.roboboat.mission2014.server.Competition;
import com.felixpageau.roboboat.mission2014.server.Event;
import com.felixpageau.roboboat.mission2014.server.RunArchiver;
import com.felixpageau.roboboat.mission2014.structures.Course;
import com.felixpageau.roboboat.mission2014.structures.DockingBay;
import com.felixpageau.roboboat.mission2014.structures.TeamCode;
import com.google.common.base.Preconditions;

/**
 * Obstacle Avoidance challenge
 */
@Path("/automatedDocking/{course}/{teamCode}")
public class AutomatedDockingResource {
    private final Competition competition;
    
    public AutomatedDockingResource(Competition competition) {
        this.competition = Preconditions.checkNotNull(competition);
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public DockingBay getGateCode(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) throws JsonProcessingException {
        RunArchiver archive = competition.getActiveRuns().get(course);
        DockingBay bay = archive.getRunSetup().getActiveDockingBay();
        competition.getActiveRuns().get(course).addEvent(new Event(new DateTime(), String.format("%s - %s - AutomatedDocking - request bay (%s)", course, teamCode, bay)));
        return bay;
   }
}
