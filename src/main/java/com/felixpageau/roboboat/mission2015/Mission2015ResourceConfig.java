package com.felixpageau.roboboat.mission2015;

//import org.glassfish.jersey.jackson.JacksonFeature;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;

import com.felixpageau.roboboat.mission2015.resources.AdminResource;
import com.felixpageau.roboboat.mission2015.resources.AutomatedDockingResource;
import com.felixpageau.roboboat.mission2015.resources.InteropResource;
import com.felixpageau.roboboat.mission2015.resources.ObstacleAvoidanceResource;
import com.felixpageau.roboboat.mission2015.resources.PingerResource;
import com.felixpageau.roboboat.mission2015.resources.RunResource;
import com.felixpageau.roboboat.mission2015.server.Competition;
import com.felixpageau.roboboat.mission2015.server.CompetitionManager;
import com.felixpageau.roboboat.mission2015.server.impl.MockCompetitionManager;
import com.google.common.base.Preconditions;

/**
 * Sets the resource configuration for the web-application to use Jackson for
 * marshalling/unmarshalling
 */
public class Mission2015ResourceConfig extends ResourceConfig {
  private final CompetitionManager competitionManager;

  public Mission2015ResourceConfig() throws MalformedURLException, URISyntaxException {
    this(new MockCompetitionManager(new Competition()));
  }

  public Mission2015ResourceConfig(CompetitionManager competitionManager) throws MalformedURLException {
    super(JacksonObjectMapperProvider.class, MultiPartFeature.class);
    this.competitionManager = Preconditions.checkNotNull(competitionManager, "competitionManager cannot be null");
    this.registerFinder(new PackageNamesScanner(new String[] { "com.felixpageau.roboboat.mission2014.resources", "com.fasterxml.jackson.jaxrs.base" }, false));
    this.register(new AutomatedDockingResource(competitionManager));
    this.register(new InteropResource(competitionManager));
    this.register(new RunResource(competitionManager));
    this.register(new ObstacleAvoidanceResource(competitionManager));
    this.register(new PingerResource(competitionManager));
    this.register(new AdminResource(competitionManager));
  }

  public Competition getCompetition() {
    return competitionManager.getCompetition();
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
