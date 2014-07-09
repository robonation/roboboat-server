package com.felixpageau.roboboat.mission2014.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jersey.repackaged.com.google.common.base.Preconditions;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

import com.felixpageau.roboboat.mission2014.server.Competition;
import com.felixpageau.roboboat.mission2014.server.RunSetup;
import com.felixpageau.roboboat.mission2014.server.TimeSlot;
import com.felixpageau.roboboat.mission2014.server.TimeSlotComparator;
import com.felixpageau.roboboat.mission2014.structures.Course;
import com.felixpageau.roboboat.mission2014.structures.TeamCode;

/**
 * Root resource (exposed at "ping" path)
 */
@Path("/admin")
public class ManageCompetition {
    private final Competition competition;
    
    public ManageCompetition(Competition competition) {
        this.competition = Preconditions.checkNotNull(competition);
    }
    
    @Path("/flash")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public void test() {
        System.out.println("Flash tes lumieres");
    }

    @Path("/teams")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<TeamCode> getTeams() {
        return competition.getTeams();
    }
    
    @Path("/{course}/team")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Map<Course, TeamCode> getTeamInWater(@PathParam("course") Course course) {
        return ImmutableMap.of(course, competition.getTeamInWater(course));
    }
    
    @Path("/{course}/{teamCode}")
    @PUT
    public void setTeamInWater(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) {
        competition.setTeamInWater(course, teamCode);
    }
    
    @Path("/timeSlots")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<TimeSlot> getTimeSlots() {
        List<TimeSlot> timeslots = new ArrayList<>(competition.getSchedule().keySet());
        Collections.sort(timeslots, new TimeSlotComparator());
        return timeslots;
    }
    
    @Path("/newRun/{course}/{teamCode}")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public RunSetup getTeams(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) {
        TimeSlot slot = competition.findCurrentTimeSlot(course);
        return competition.startNewRun(slot, teamCode);
    }
}
