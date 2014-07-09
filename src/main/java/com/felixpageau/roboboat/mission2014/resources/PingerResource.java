package com.felixpageau.roboboat.mission2014.resources;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jersey.repackaged.com.google.common.base.Preconditions;

import com.felixpageau.roboboat.mission2014.server.Competition;
import com.felixpageau.roboboat.mission2014.structures.BeaconReport;
import com.felixpageau.roboboat.mission2014.structures.Course;
import com.felixpageau.roboboat.mission2014.structures.ReportStatus;
import com.felixpageau.roboboat.mission2014.structures.TeamCode;

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
        System.out.println(course + " " + teamCode + " \n" + payload);
        
        return new ReportStatus(true);
   }
}
