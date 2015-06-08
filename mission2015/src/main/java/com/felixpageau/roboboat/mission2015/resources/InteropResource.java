package com.felixpageau.roboboat.mission2015.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.felixpageau.roboboat.mission2015.server.CompetitionManager;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.InteropReport;
import com.felixpageau.roboboat.mission2015.structures.ReportStatus;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.felixpageau.roboboat.mission2015.structures.UploadStatus;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

/**
 * Interop challenge
 */
@Path("/interop")
public class InteropResource {
  private final CompetitionManager manager;

  public InteropResource(CompetitionManager manager) {
    this.manager = Preconditions.checkNotNull(manager);
  }

  @Path("/image/{course}/{teamCode}")
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  public UploadStatus uploadImage(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode,
      @FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {
    return manager.uploadInteropImage(course, teamCode, ByteStreams.toByteArray(fileInputStream));
  }

  @Path("/report/{course}/{teamCode}")
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public ReportStatus reportShape(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode, InteropReport report) throws IOException {
    return manager.reportInterop(course, teamCode, report);
  }

  @Path("/images/{course}/{teamCode}")
  @GET
  @Produces({ MediaType.TEXT_HTML })
  public String listImages(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode) throws IOException {
    return "<html><head></head><body><ul>"
        + manager.listInteropImages(course, teamCode).stream().map(i -> String.format("<li><a href=\"/interop/image/%s\">%s</a>", i, i))
            .collect(Collectors.joining()) + "</ul></body></html>";
  }

  @Path("/images/{image}")
  @GET
  @Produces({ "image/jpeg" })
  public byte[] getImage(@PathParam("image") String image) throws IOException {
    return manager.getInteropImage(image).orElseThrow(NotFoundException::new);
  }

  // @Path("/report/{course}/{teamCode}")
  // @POST
  // @Produces({ MediaType.APPLICATION_JSON })
  // @Consumes("application/json")
  // public ReportStatus reportLightSequence(@PathParam("course") Course course,
  // @PathParam("teamCode") TeamCode teamCode, LightSequence payload)
  // throws IOException {
  // RunArchiver ra = competition.getActiveRuns().get(course);
  // if (ra == null) {
  // System.out.println(String.format("No active run, but reported: %s - %s - LightSequence - report sequence - sequence: %s",
  // course, teamCode, payload));
  // return new ReportStatus(false);
  // } else {
  // LightSequence correctSequence = ra.getRunSetup().getActiveLightSequence();
  // boolean success = payload.equals(correctSequence);
  // ra.addEvent(new Event(new DateTime(),
  // String.format("%s - %s - LightSequence - reported sequence (%s) -> %s",
  // course, teamCode, payload,
  // (success ? "success" : "incorrect"))));
  // return new ReportStatus(success);
  // }
  // }

  @SuppressWarnings("serial")
  public class NotFoundException extends WebApplicationException {
    public NotFoundException() {
      super(Response.Status.NOT_FOUND);
    }
  }
}
