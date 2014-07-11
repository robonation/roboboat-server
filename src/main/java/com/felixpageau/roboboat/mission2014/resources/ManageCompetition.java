package com.felixpageau.roboboat.mission2014.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.felixpageau.roboboat.mission2014.server.Competition;
import com.felixpageau.roboboat.mission2014.server.Event;
import com.felixpageau.roboboat.mission2014.server.RunArchiver;
import com.felixpageau.roboboat.mission2014.server.RunSetup;
import com.felixpageau.roboboat.mission2014.server.TimeSlot;
import com.felixpageau.roboboat.mission2014.server.TimeSlotComparator;
import com.felixpageau.roboboat.mission2014.server.TimeSlotComparatorEntry;
import com.felixpageau.roboboat.mission2014.structures.Course;
import com.felixpageau.roboboat.mission2014.structures.TeamCode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

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
        return Collections.singletonMap(course, competition.getTeamInWater(course));
    }
    
    @Path("/{course}/{teamCode}")
    @PUT
    public void setTeamInWater(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) {
        competition.setTeamInWater(course, teamCode);
    }
    
    @Path("/events/{course}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<Event> getEvents(final @PathParam("course") Course course) {
        List<Map.Entry<TimeSlot, RunArchiver>> entries = new ArrayList<>(competition.getResults().entries());
        Collections.sort(entries, new TimeSlotComparatorEntry<RunArchiver>(true));
        ImmutableList.Builder<Event> builder = ImmutableList.builder();
        for (Entry<TimeSlot, RunArchiver> entry : entries) {
            if (course.equals(entry.getValue().getRunSetup().getCourse())) {
                builder.addAll(entry.getValue().getEvents());
            }
        }
        return builder.build();
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
