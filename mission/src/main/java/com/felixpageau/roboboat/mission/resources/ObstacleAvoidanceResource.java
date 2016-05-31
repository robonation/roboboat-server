package com.felixpageau.roboboat.mission.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.GateCode;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * Obstacle Avoidance challenge
 */
@Path("/obstacleAvoidance/{course}/{teamCode}")
public class ObstacleAvoidanceResource {
  private final CompetitionManager manager;

  public ObstacleAvoidanceResource(CompetitionManager manager) {
    this.manager = Preconditions.checkNotNull(manager);
  }

  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  public GateCode getGateCode(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) throws JsonProcessingException {
    return manager.getObstacleCourseCode(course, teamCode);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }
}
