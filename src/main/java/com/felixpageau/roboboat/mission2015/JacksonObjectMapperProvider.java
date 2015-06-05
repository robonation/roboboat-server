package com.felixpageau.roboboat.mission2015;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonObjectMapperProvider implements ContextResolver<ObjectMapper> {
 
    final ObjectMapper om;
 
    public JacksonObjectMapperProvider() {
        this.om = new ObjectMapper();
    }
 
    @Override
    public ObjectMapper getContext(Class<?> type) {
        return this.om;
    }
}