package com.felixpageau.roboboat.mission;

import java.net.URISyntaxException;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.felixpageau.roboboat.mission.resources.AdminResource;
import com.felixpageau.roboboat.mission.resources.MyResource;
import com.felixpageau.roboboat.mission.server.Competition;
import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.server.impl.CompetitionManagerImpl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Sets the resource configuration for the web-application to use Jackson for
 * marshalling/unmarshalling
 */
@SuppressFBWarnings(value = "UUF_UNUSED_FIELD")
public class AdminResourceConfig extends CompetitionResourceConfig {
  private static final Logger LOG = LoggerFactory.getLogger(AdminResourceConfig.class);

  public AdminResourceConfig() throws URISyntaxException {
    this(new CompetitionManagerImpl(new Competition(COMPETITION_NAME, COMPETITION_DAYS, TEAMS, COURSE_LAYOUT_MAP, true, true, true, OM_PROVIDER.getObjectMapper()),
        OM_PROVIDER.getObjectMapper()));
  }

  public AdminResourceConfig(CompetitionManager competitionManager) throws URISyntaxException {
    super(competitionManager, JacksonFeatures.class, JacksonObjectMapperProvider.class, MultiPartFeature.class);
    this.register(new AdminResource(competitionManager));
    this.register(MyResource.class);
    
    LOG.error("*** Using the AdminResourceConfig ***");
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
