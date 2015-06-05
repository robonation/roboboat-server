package com.felixpageau.roboboat.mission2015.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.felixpageau.roboboat.mission2015.server.CompetitionManager;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.DockingSequence;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.google.common.base.Preconditions;

/**
 * Obstacle Avoidance challenge
 */
@Path("/automatedDocking/{course}/{teamCode}")
public class AutomatedDockingResource {
  private final CompetitionManager manager;

  public AutomatedDockingResource(CompetitionManager manager) {
    this.manager = Preconditions.checkNotNull(manager);
  }

  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  public DockingSequence getGateCode(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) throws JsonProcessingException {
    return manager.getDockingSequence(course, teamCode);
  }
}
