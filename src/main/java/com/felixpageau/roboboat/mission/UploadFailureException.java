package com.felixpageau.roboboat.mission;

import javax.ws.rs.core.Response;

@SuppressWarnings("serial")
public class UploadFailureException extends WebApplicationExceptionWithContext {
  public UploadFailureException(String message) {
    super(message, Response.Status.BAD_REQUEST);
  }
}