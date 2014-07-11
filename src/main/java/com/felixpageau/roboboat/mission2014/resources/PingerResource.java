package com.felixpageau.roboboat.mission2014.resources;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;

import com.felixpageau.roboboat.mission2014.server.Competition;
import com.felixpageau.roboboat.mission2014.server.Event;
import com.felixpageau.roboboat.mission2014.server.Pinger;
import com.felixpageau.roboboat.mission2014.server.RunArchiver;
import com.felixpageau.roboboat.mission2014.structures.BeaconReport;
import com.felixpageau.roboboat.mission2014.structures.Course;
import com.felixpageau.roboboat.mission2014.structures.ReportStatus;
import com.felixpageau.roboboat.mission2014.structures.TeamCode;
import com.google.common.base.Preconditions;

/**
 * Acoustic Beacon Positioning challenge
 */
@Path("/pinger/{course}/{teamCode}")
public class PingerResource {
    private final Competition competition;
    
    public PingerResource(Competition competition) {
        this.competition = Preconditions.checkNotNull(competition);
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    public ReportStatus reportPinger(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode, BeaconReport payload) throws IOException {
        RunArchiver archive = competition.getActiveRuns().get(course);
        Pinger reportedPinger = new Pinger(payload.getBuoyColor(), payload.getBuoyPosition());
        boolean success = reportedPinger.equals(archive.getRunSetup().getActivePinger());
        competition.getActiveRuns().get(course).addEvent(new Event(new DateTime(), String.format("%s - %s - ObstacleAvoidance - reported pinger (%s) -> ", course, teamCode, payload, success ? "success" : "incorrect")));
        return new ReportStatus(success);
   }
}
