package com.felixpageau.roboboat.mission.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * Run resource used by team to manage their runs
 */
@Path("/run")
public class RunResource {
  private final CompetitionManager competitionManager;

  public RunResource(CompetitionManager competitionManager) {
    this.competitionManager = Preconditions.checkNotNull(competitionManager);
  }

  @Path("/start/{course}/{teamCode}")
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  public ReportStatus startRun(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) {
    return competitionManager.startRun(course, teamCode);
  }

  @Path("/end/{course}/{teamCode}")
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  public ReportStatus endRun(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) {
    return competitionManager.endRun(course, teamCode);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }
}
