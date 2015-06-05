package com.felixpageau.roboboat.mission2015.resources;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.felixpageau.roboboat.mission2015.server.CompetitionManager;
import com.felixpageau.roboboat.mission2015.structures.BeaconReport;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.ReportStatus;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.google.common.base.Preconditions;

/**
 * Acoustic Beacon Positioning challenge
 */
@Path("/pinger/{course}/{teamCode}")
public class PingerResource {
  private final CompetitionManager manager;

  public PingerResource(CompetitionManager manager) {
    this.manager = Preconditions.checkNotNull(manager);
  }

  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes(MediaType.APPLICATION_JSON)
  public ReportStatus reportPinger(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode, BeaconReport payload) throws IOException {
    return manager.reportPinger(course, teamCode, payload);
  }
}
