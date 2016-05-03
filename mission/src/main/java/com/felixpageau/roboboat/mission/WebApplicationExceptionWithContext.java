/**
 * 
 */
package com.felixpageau.roboboat.mission;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * @author felixpageau
 *
 */
public class WebApplicationExceptionWithContext extends WebApplicationException {
  private static final long serialVersionUID = 5410018941310786986L;

  public WebApplicationExceptionWithContext() {
    this((Throwable) null, Response.Status.INTERNAL_SERVER_ERROR);
  }

  /**
   * @param message
   */
  public WebApplicationExceptionWithContext(String message) {
    this(message, null, Response.Status.INTERNAL_SERVER_ERROR);
  }

  /**
   * @param response
   */
  public WebApplicationExceptionWithContext(Response response) {
    this((Throwable) null, response);
  }

  /**
   * @param status
   */
  public WebApplicationExceptionWithContext(int status) {
    this((Throwable) null, status);
  }

  /**
   * @param status
   */
  public WebApplicationExceptionWithContext(Status status) {
    this((Throwable) null, status);
  }

  /**
   * @param cause
   */
  public WebApplicationExceptionWithContext(Throwable cause) {
    this(cause, Response.Status.INTERNAL_SERVER_ERROR);
  }

  /**
   * @param message
   * @param response
   */
  public WebApplicationExceptionWithContext(String message, Response response) {
    this(message, null, response);
  }

  /**
   * @param message
   * @param status
   */
  public WebApplicationExceptionWithContext(String message, int status) {
    this(message, null, status);
  }

  /**
   * @param message
   * @param status
   */
  public WebApplicationExceptionWithContext(String message, Status status) {
    this(message, null, status);
  }

  /**
   * @param message
   * @param cause
   */
  public WebApplicationExceptionWithContext(String message, Throwable cause) {
    this(message, cause, Response.Status.INTERNAL_SERVER_ERROR);
  }

  /**
   * @param cause
   * @param response
   */
  public WebApplicationExceptionWithContext(Throwable cause, Response response) {
    this(computeExceptionMessage(response), cause, response);
  }

  /**
   * @param cause
   * @param status
   */
  public WebApplicationExceptionWithContext(Throwable cause, int status) {
    this(cause, Response.status(status).build());
  }

  /**
   * @param cause
   * @param status
   * @throws IllegalArgumentException
   */
  public WebApplicationExceptionWithContext(Throwable cause, Status status) throws IllegalArgumentException {
    this(cause, Response.status(status).build());
  }

  /**
   * @param message
   * @param cause
   * @param response
   */
  public WebApplicationExceptionWithContext(String message, Throwable cause, Response response) {
    super(message, cause, (response == null) ? Response.serverError().entity(message + "\n").build() : Response.fromResponse(response).entity(message + "\n")
        .build());
  }

  /**
   * @param message
   * @param cause
   * @param status
   */
  public WebApplicationExceptionWithContext(String message, Throwable cause, int status) {
    this(message, cause, Response.status(status).build());
  }

  /**
   * @param message
   * @param cause
   * @param status
   * @throws IllegalArgumentException
   */
  public WebApplicationExceptionWithContext(String message, Throwable cause, Status status) throws IllegalArgumentException {
    super(message, cause, status);
    // TODO Auto-generated constructor stub
  }

  private static String computeExceptionMessage(Response response) {
    final Response.StatusType statusInfo;
    if (response != null) {
      statusInfo = response.getStatusInfo();
    } else {
      statusInfo = Response.Status.INTERNAL_SERVER_ERROR;
    }
    return "HTTP " + statusInfo.getStatusCode() + ' ' + statusInfo.getReasonPhrase();
  }

}
