package com.felixpageau.roboboat.mission;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.felixpageau.roboboat.mission.server.TimeSlot;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class JacksonTimeslotModule extends SimpleModule {
  private static final long serialVersionUID = 6736484404399417670L;
  private final ObjectMapper om;

  public JacksonTimeslotModule(ObjectMapper om) {
    this.om = Preconditions.checkNotNull(om, "om cannot be null");
    addKeyDeserializer(TimeSlot.class, new TimeSlotKeyDeserializer());
    addKeySerializer(TimeSlot.class, new TimeSlotKeySerializer());
  }

  public class TimeSlotKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException {
      return om.readValue(key, TimeSlot.class);
    }
  }

  public class TimeSlotKeySerializer extends JsonSerializer<TimeSlot> {
    @Override
    public void serialize(TimeSlot value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
      jgen.writeFieldName(om.writeValueAsString(value));
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(JacksonObjectMapperProvider.class).add("om", om).toString();
  }
}
