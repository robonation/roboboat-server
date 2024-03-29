package com.felixpageau.roboboat.mission.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.base.MoreObjects;

/**
 * Root resource (exposed at "ping" path)
 */
@Path("ping")
public class MyResource {

  /**
   * Method handling HTTP GET requests. The returned object will be sent to the
   * client as "text/plain" media type.
   *
   * @return String that will be returned as a text/plain response.
   */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getIt() {
    return "pong";
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }
}
