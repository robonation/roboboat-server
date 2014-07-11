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
import com.felixpageau.roboboat.mission2014.structures.GateCode;
import com.felixpageau.roboboat.mission2014.structures.TeamCode;
import com.google.common.base.Preconditions;

/**
 * Obstacle Avoidance challenge
 */
@Path("/obstacleAvoidance/{course}/{teamCode}")
public class ObstacleAvoidanceResource {
    private final Competition competition;
    
    public ObstacleAvoidanceResource(Competition competition) {
        this.competition = Preconditions.checkNotNull(competition);
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public GateCode getGateCode(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) throws JsonProcessingException {
        RunArchiver archive = competition.getActiveRuns().get(course);
        GateCode code = archive.getRunSetup().getActiveGateCode();
        competition.getActiveRuns().get(course).addEvent(new Event(new DateTime(), String.format("%s - %s - ObstacleAvoidance - request gatecode (%s)", course, teamCode, code)));
        return code;
   }
}
