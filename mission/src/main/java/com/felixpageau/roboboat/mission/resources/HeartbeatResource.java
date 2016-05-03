package com.felixpageau.roboboat.mission.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.HeartbeatReport;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.google.common.base.Preconditions;

/**
 * Run resource used by team to manage their runs
 */
@Path("/heartbeat")
public class HeartbeatResource {
  private final CompetitionManager competitionManager;

  public HeartbeatResource(CompetitionManager competitionManager) {
    this.competitionManager = Preconditions.checkNotNull(competitionManager);
  }

  @Path("/{course}/{teamCode}")
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public ReportStatus startRun(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode, HeartbeatReport report) {
    return competitionManager.reportHeartbeat(course, teamCode, report);
  }
}
