package com.felixpageau.roboboat.mission2015.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.felixpageau.roboboat.mission2015.server.CompetitionManager;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.GateCode;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
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
}
