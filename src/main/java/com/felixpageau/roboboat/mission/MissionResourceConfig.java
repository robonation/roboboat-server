package com.felixpageau.roboboat.mission;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;

import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.felixpageau.roboboat.mission.nmea.SentenceRegistry;
import com.felixpageau.roboboat.mission.resources.AutomatedDockingResource;
import com.felixpageau.roboboat.mission.resources.FollowTheLeaderResource;
import com.felixpageau.roboboat.mission.resources.HeartbeatResource;
import com.felixpageau.roboboat.mission.resources.RunResource;
import com.felixpageau.roboboat.mission.server.Competition;
import com.felixpageau.roboboat.mission.server.CompetitionDay;
import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.server.CourseLayout;
import com.felixpageau.roboboat.mission.server.impl.MockCompetitionManager;
import com.felixpageau.roboboat.mission.structures.Code;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.DockingBay;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Sets the resource configuration for the web-application to use Jackson for
 * marshalling/unmarshalling
 */
@SuppressFBWarnings(value = "UUF_UNUSED_FIELD")
public class MissionResourceConfig extends CompetitionResourceConfig {
  public static final JacksonObjectMapperProvider OM_PROVIDER = new JacksonObjectMapperProvider();
  public static final String COMPETITION_NAME = "RoboBoat 2016";
  public static final List<CompetitionDay> COMPETITION_DAYS = ImmutableList.<CompetitionDay> of(new CompetitionDay(LocalDateTime.of(2016, 7, 5, 8, 0),
      LocalDateTime.of(2016, 7, 5, 18, 0)), // Tu
      new CompetitionDay(LocalDateTime.of(2016, 7, 6, 8, 0), LocalDateTime.of(2016, 7, 6, 18, 0)), // We
      new CompetitionDay(LocalDateTime.of(2016, 7, 7, 8, 0), LocalDateTime.of(2016, 7, 7, 18, 0)), // Th
      new CompetitionDay(LocalDateTime.of(2016, 7, 8, 8, 0), LocalDateTime.of(2016, 7, 8, 18, 0)), // Fr
      new CompetitionDay(LocalDateTime.of(2016, 7, 9, 8, 0), LocalDateTime.of(2016, 7, 9, 18, 0)), // Sa
      new CompetitionDay(LocalDateTime.of(2016, 7, 10, 8, 0), LocalDateTime.of(2016, 7, 10, 18, 0)) // Su
      );
  public static final List<TeamCode> TEAMS = ImmutableList.of(new TeamCode("AUVSI"), new TeamCode("DBH"), new TeamCode("EEPIS"), new TeamCode("ERAU"), new TeamCode("GIT"), new TeamCode("HHS"), new TeamCode("ITSN"), new TeamCode("VTEC"), new TeamCode("UI"), new TeamCode("IOWA"), new TeamCode("UOFM"));
  private static final List<DockingBay> bays = ImmutableList.copyOf(Arrays.stream(Code.values()).map(x -> new DockingBay(x)).collect(Collectors.toList()));
  public static final Map<Course, CourseLayout> COURSE_LAYOUT_MAP;
  @SuppressFBWarnings(value = "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD", justification = "NMEAServerTest uses this field")
  public static final AtomicInteger port = new AtomicInteger(9999);
  private final NMEAServer nmeaServer;

  static {
    try {
      List<DockingBay> bays = ImmutableList.copyOf(Arrays.stream(Code.values()).map(x -> new DockingBay(x)).collect(Collectors.toList()));
      COURSE_LAYOUT_MAP = ImmutableMap.of(
          Course.courseA, new CourseLayout(Course.courseA, bays, new URL("http://192.168.1.5:4000"), new URL("http://192.168.1.12:5000"), new URL("http://192.168.1.11:5000")), 
          Course.courseB, new CourseLayout(Course.courseB, bays, new URL("http://192.168.1.6:4000"), new URL("http://192.168.1.22:5000"), new URL("http://192.168.1.21:5000")), 
          Course.openTest, new CourseLayout(Course.courseC, bays, new URL("http://192.168.1.7:4000"), new URL("http://192.168.1.32:5000"), new URL("http://192.168.1.31:5000")));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public MissionResourceConfig() throws URISyntaxException {
    this(new MockCompetitionManager(new Competition(COMPETITION_NAME, COMPETITION_DAYS, TEAMS, COURSE_LAYOUT_MAP, false, false, OM_PROVIDER.getObjectMapper()),
        OM_PROVIDER.getObjectMapper()));
  }

  public MissionResourceConfig(CompetitionManager competitionManager) throws URISyntaxException {
    super(competitionManager, ImmutableSet.of(OM_PROVIDER), JacksonFeatures.class, JacksonObjectMapperProvider.class, MultiPartFeature.class);
    this.register(MultiPartFeature.class);
    this.register(JacksonFeatures.class);
    this.register(JacksonObjectMapperProvider.class);
    this.registerFinder(new PackageNamesScanner(new String[] { "com.felixpageau.roboboat.mission2015.resources", "com.fasterxml.jackson.jaxrs.base" }, false));
    this.register(new AutomatedDockingResource(competitionManager));
    this.register(new RunResource(competitionManager));
    this.register(new HeartbeatResource(competitionManager));
    this.register(new FollowTheLeaderResource(competitionManager));
    this.register(CORSResponseFilter.class);

    this.nmeaServer = new NMEAServer(competitionManager, port.get(), createNMEASentenceRegistry(), true);
    this.nmeaServer.start();
  }

  @Override
  public final SentenceRegistry createNMEASentenceRegistry() {
    return SentenceRegistryFactory.createNMEASentenceRegistry();
  }

  @Override
  public NMEAServer getNMEAServer() {
    return nmeaServer;
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
