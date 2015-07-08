package com.felixpageau.roboboat.mission2015.resources;

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

import com.felixpageau.roboboat.mission2015.server.CompetitionManager;
import com.felixpageau.roboboat.mission2015.server.RunArchiver;
import com.felixpageau.roboboat.mission2015.server.TimeSlot;
import com.felixpageau.roboboat.mission2015.server.TimeSlotComparator;
import com.felixpageau.roboboat.mission2015.server.TimeSlotComparatorEntry;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.DisplayStatus;
import com.felixpageau.roboboat.mission2015.structures.ReportStatus;
import com.felixpageau.roboboat.mission2015.structures.Run;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Resource for admin operations (exposed at "ping" path)
 */
@Path("/admin")
public class AdminResource {
  private final CompetitionManager competitionManager;

  public AdminResource(CompetitionManager competitionManager) {
    this.competitionManager = Preconditions.checkNotNull(competitionManager);
  }

  @Path("/flash")
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  public void test() {
    System.out.println("Flash tes lumieres");
  }

  @Path("/teams")
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  public List<TeamCode> getTeams() {
    return competitionManager.getCompetition().getTeams();
  }

  @Path("/{course}/team")
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  public Map<Course, TeamCode> getTeamInWater(@PathParam("course") Course course) {
    return Collections.singletonMap(course, competitionManager.getCompetition().getTeamInWater(course));
  }

  @Path("/{course}/{teamCode}")
  @PUT
  public void setTeamInWater(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) {
    competitionManager.getCompetition().setTeamInWater(course, teamCode);
  }

  @Path("/events/{course}")
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  public List<Run> getEvents(final @PathParam("course") Course course) {
    List<Map.Entry<TimeSlot, RunArchiver>> entries = new ArrayList<>(competitionManager.getCompetition().getResults().entries());
    Collections.sort(entries, new TimeSlotComparatorEntry<RunArchiver>(true));
    ImmutableList.Builder<Run> builder = ImmutableList.builder();
    for (Entry<TimeSlot, RunArchiver> entry : entries) {
      if (course.equals(entry.getValue().getRunSetup().getCourse())) {
        builder.add(new Run(entry.getValue().getStartTime(), entry.getValue().getRunSetup().getActiveTeam(), entry.getValue().getEvents()));
      }
    }
    return builder.build();
  }

  @Path("/timeSlots")
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  public List<TimeSlot> getTimeSlots() {
    List<TimeSlot> timeslots = new ArrayList<>(competitionManager.getCompetition().getSchedule().keySet());
    Collections.sort(timeslots, new TimeSlotComparator());
    return timeslots;
  }

  @Path("/newRun/{course}/{teamCode}")
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  public ReportStatus startRun(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) {
    return competitionManager.startRun(course, teamCode);
  }

  @Path("/endRun/{course}/{teamCode}")
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  public ReportStatus endRun(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) {
    return competitionManager.endRun(course, teamCode);
  }

  @Path("/display")
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  public DisplayStatus display(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) {
    return competitionManager.getDisplayStatus();
  }
}
