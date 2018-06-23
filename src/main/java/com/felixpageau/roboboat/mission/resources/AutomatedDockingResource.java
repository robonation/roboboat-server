package com.felixpageau.roboboat.mission.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.felixpageau.roboboat.mission.UploadFailureException;
import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.felixpageau.roboboat.mission.structures.UploadStatus;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Automated Docking challenge
 */
@Path("/docking")
public class AutomatedDockingResource {
  private final CompetitionManager manager;

  public AutomatedDockingResource(CompetitionManager manager) {
    this.manager = Preconditions.checkNotNull(manager);
  }
  
  @SuppressFBWarnings(value = "JXI_UNDEFINED_PARAMETER_SOURCE_IN_ENDPOINT", justification = "fb-contribs lacks support for @FormDataParam")
  @POST
  @Path("/image/{course}/{teamCode}")
  @Consumes({MediaType.MULTIPART_FORM_DATA})
  @Produces({ MediaType.APPLICATION_JSON })
  public UploadStatus uploadPdfFile(  @PathParam("course") Course course, @PathParam("teamCode") TeamCode teamCode,
                                  @FormDataParam("file") InputStream fileInputStream,
                                  @FormDataParam("file") FormDataContentDisposition fileMetaData) throws Exception
  {
    return manager.uploadDockingImage(course, teamCode, ByteStreams.toByteArray(fileInputStream));
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }
}
