package com.felixpageau.roboboat.mission;

import java.net.URISyntaxException;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.felixpageau.roboboat.mission.server.Competition;
import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.server.impl.MockCompetitionManager;
import com.google.common.collect.ImmutableSet;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Sets the resource configuration for the web-application to use Jackson for
 * marshalling/unmarshalling
 */
@SuppressFBWarnings(value = "UUF_UNUSED_FIELD")
public class MissionResourceConfig extends CompetitionResourceConfig {
  private static final Logger LOG = LoggerFactory.getLogger(AdminResourceConfig.class);

  public MissionResourceConfig() throws URISyntaxException {
    this(new MockCompetitionManager(new Competition(COMPETITION_NAME, COMPETITION_DAYS, TEAMS, COURSE_LAYOUT_MAP, false, false, false, OM_PROVIDER.getObjectMapper()),
        OM_PROVIDER.getObjectMapper()));
  }

  public MissionResourceConfig(CompetitionManager competitionManager) throws URISyntaxException {
    super(competitionManager, ImmutableSet.of(OM_PROVIDER), JacksonFeatures.class, JacksonObjectMapperProvider.class, MultiPartFeature.class);
    
    LOG.error("*** Using the MissionResourceConfig ***");
  }

  @Override
  public String toString() {
    System.out.println("Packages:");
    for (Class<?> c : getConfiguration().getClasses()) {
      System.out.println(c.toString());
    }
    return super.toString();
  }
}
