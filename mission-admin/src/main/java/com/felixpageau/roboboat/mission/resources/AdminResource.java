package com.felixpageau.roboboat.mission.resources;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.felixpageau.roboboat.mission.NotFoundException;
import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.server.RunArchiver;
import com.felixpageau.roboboat.mission.server.TimeSlot;
import com.felixpageau.roboboat.mission.server.TimeSlotComparator;
import com.felixpageau.roboboat.mission.server.TimeSlotComparatorEntry;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.DisplayStatus;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.felixpageau.roboboat.mission.structures.Run;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.felixpageau.roboboat.mission.utils.GuavaCollectors;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * Resource for admin operations (exposed at "ping" path)
 */
@Path("/admin")
public final class AdminResource {
  private final CompetitionManager competitionManager;

  public AdminResource(CompetitionManager competitionManager) {
    this.competitionManager = Preconditions.checkNotNull(competitionManager);
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
    return entries.stream().filter(e -> e.getValue().getRunSetup().getCourse() == course).map(c -> {
      RunArchiver archiver = c.getValue();
      return new Run(archiver.getStartTime(), archiver.getRunSetup().getActiveTeam(), archiver.getEvents());
    }).collect(GuavaCollectors.immutableList());
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
  public DisplayStatus display() {
    return competitionManager.getDisplayStatus(Arrays.asList(Course.courseA, Course.courseB));
  }

  @Path("/display/image/{imageId}")
  @GET
  @Produces({ "image/jpeg", "image/png" })
  public byte[] getUploadedImage(@PathParam("imageId") String imageId) {
    return competitionManager.getUploadedImage(imageId).orElseThrow(NotFoundException::new);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }
}
