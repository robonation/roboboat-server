package com.felixpageau.roboboat.mission.resources;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.InteropReport;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.felixpageau.roboboat.mission.structures.UploadStatus;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Interop challenge
 */
@Path("/interop")
public class InteropResource {
  private final CompetitionManager manager;

  public InteropResource(CompetitionManager manager) {
    this.manager = Preconditions.checkNotNull(manager);
  }

  @SuppressFBWarnings(value = "JXI_UNDEFINED_PARAMETER_SOURCE_IN_ENDPOINT", justification = "fb-contribs lacks support for @FormDataParam")
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
  @SuppressFBWarnings(value = "JXI_UNDEFINED_PARAMETER_SOURCE_IN_ENDPOINT")
  public ReportStatus reportShape(@PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode, InteropReport report) throws IOException {
    return manager.reportInterop(course, teamCode, report);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }
}
