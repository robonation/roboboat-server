package com.felixpageau.roboboat.mission2014.resources;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;

import com.felixpageau.roboboat.mission2014.server.Competition;
import com.felixpageau.roboboat.mission2014.server.CourseLayout;
import com.felixpageau.roboboat.mission2014.server.Event;
import com.felixpageau.roboboat.mission2014.server.RunArchiver;
import com.felixpageau.roboboat.mission2014.structures.Course;
import com.felixpageau.roboboat.mission2014.structures.LightSequence;
import com.felixpageau.roboboat.mission2014.structures.ReportStatus;
import com.felixpageau.roboboat.mission2014.structures.TeamCode;
import com.google.common.base.Preconditions;

/**
 * Acoustic Beacon Positioning challenge
 */
@Path("/lightSequence")
public class LightSequenceResource {
    private final Competition competition;
    private final CloseableHttpClient httpclient = HttpClients.createDefault();
    
    public LightSequenceResource(Competition competition) {
        this.competition = Preconditions.checkNotNull(competition);
    }
    
    @Path("/activate/{course}/{teamCode}")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes("application/json")
    public ReportStatus activateLightSequence(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) throws IOException {
        RunArchiver ra = competition.getActiveRuns().get(course);
        CourseLayout layout = competition.getCourseLayout(course);
        LightSequence ls = ra.getRunSetup().getActiveLightSequence();

        ra.addEvent(new Event(new DateTime(), String.format("%s - %s - LightSequence - activated sequence (%s)", course, teamCode, ls)));
        HttpGet httpget = new HttpGet(layout.getLightControlServer() + "/activate/" + ls.lightSequenceString());
        try (CloseableHttpResponse response = httpclient.execute(httpget)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine());
             }
        }

        return new ReportStatus(true);
    }
    
    @Path("/report/{course}/{teamCode}")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes("application/json")
    public ReportStatus reportLightSequence(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode, LightSequence payload) throws IOException {
        RunArchiver ra = competition.getActiveRuns().get(course);
        if(ra == null) {
            System.out.println(String.format("No active run, but reported: %s - %s - LightSequence - report sequence - sequence: %s", course, teamCode, payload));
            return new ReportStatus(false);
        }
        else {
            LightSequence correctSequence = ra.getRunSetup().getActiveLightSequence();
            boolean success = payload.equals(correctSequence);
            ra.addEvent(new Event(new DateTime(), String.format("%s - %s - LightSequence - reported sequence (%s) -> %s", course, teamCode, payload, (success ? "success": "incorrect"))));
            return new ReportStatus(success);
        }
   }
}
