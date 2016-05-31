package com.felixpageau.roboboat.mission;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@ParametersAreNonnullByDefault
public class JacksonObjectMapperProvider implements ContextResolver<ObjectMapper> {
  private final ObjectMapper om;

  public JacksonObjectMapperProvider() {
    this(new ObjectMapper());
  }

  public JacksonObjectMapperProvider(ObjectMapper om) {
    this.om = Preconditions.checkNotNull(om, "om cannot be null");
  }

  /**
   * @return the object mapper instance
   */
  public ObjectMapper getObjectMapper() {
    return om;
  }

  @Override
  public ObjectMapper getContext(Class<?> type) {
    return this.om;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(JacksonObjectMapperProvider.class).toString();
  }
}