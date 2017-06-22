package com.felixpageau.roboboat.mission.resources;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.LeaderSequence;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * FollowLeader challenge
 */
@Path("/followLeader/{course}/{teamCode}")
public class FollowTheLeaderResource {
  private final CompetitionManager manager;

  public FollowTheLeaderResource(CompetitionManager manager) {
    this.manager = Preconditions.checkNotNull(manager);
  }

  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  public LeaderSequence getLeaderCode(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) throws IOException {
    return manager.getLeaderSequence(course, teamCode);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }
}
