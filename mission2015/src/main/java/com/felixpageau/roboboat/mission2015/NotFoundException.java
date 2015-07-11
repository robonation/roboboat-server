package com.felixpageau.roboboat.mission2015;

import javax.ws.rs.core.Response;

@SuppressWarnings("serial")
public class NotFoundException extends WebApplicationExceptionWithContext {
  public NotFoundException() {
    super(Response.Status.NOT_FOUND);
  }
}