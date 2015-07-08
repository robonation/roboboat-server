package com.felixpageau.roboboat.mission2015;

//import org.glassfish.jersey.jackson.JacksonFeature;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;

import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.felixpageau.roboboat.mission2015.nmea.SentenceRegistry;
import com.felixpageau.roboboat.mission2015.resources.AdminResource;
import com.felixpageau.roboboat.mission2015.resources.AutomatedDockingResource;
import com.felixpageau.roboboat.mission2015.resources.HeartbeatResource;
import com.felixpageau.roboboat.mission2015.resources.InteropResource;
import com.felixpageau.roboboat.mission2015.resources.ObstacleAvoidanceResource;
import com.felixpageau.roboboat.mission2015.resources.PingerResource;
import com.felixpageau.roboboat.mission2015.resources.RunResource;
import com.felixpageau.roboboat.mission2015.server.Competition;
import com.felixpageau.roboboat.mission2015.server.CompetitionDay;
import com.felixpageau.roboboat.mission2015.server.CompetitionManager;
import com.felixpageau.roboboat.mission2015.server.CourseLayout;
import com.felixpageau.roboboat.mission2015.server.impl.CompetitionManagerImpl;
import com.felixpageau.roboboat.mission2015.structures.BuoyColor;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.Pinger;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Sets the resource configuration for the web-application to use Jackson for
 * marshalling/unmarshalling
 */
public class AdminResourceConfig extends CompetitionResourceConfig {
  private static final List<CompetitionDay> COMPETITION_DAYS = ImmutableList.<CompetitionDay> of(new CompetitionDay(LocalDateTime.of(2015, 7, 7, 8, 0),
      LocalDateTime.of(2015, 7, 7, 18, 0)), // Tu
      new CompetitionDay(LocalDateTime.of(2015, 7, 8, 8, 0), LocalDateTime.of(2015, 7, 8, 18, 0)), // We
      new CompetitionDay(LocalDateTime.of(2015, 7, 9, 8, 0), LocalDateTime.of(2015, 7, 9, 18, 0)), // Th
      new CompetitionDay(LocalDateTime.of(2015, 7, 10, 8, 0), LocalDateTime.of(2015, 7, 10, 18, 0)), // Fr
      new CompetitionDay(LocalDateTime.of(2015, 7, 11, 8, 0), LocalDateTime.of(2015, 7, 11, 18, 0)), // Sa
      new CompetitionDay(LocalDateTime.of(2015, 7, 12, 8, 0), LocalDateTime.of(2015, 7, 12, 18, 0)) // Su
      );
  private static final List<TeamCode> TEAMS = ImmutableList.of(new TeamCode("AUVSI"), new TeamCode("DBH"), new TeamCode("EEPIS"), new TeamCode("ERAU"),
      new TeamCode("FAU"), new TeamCode("GIT"), new TeamCode("NCKU"), new TeamCode("ODUSM"), new TeamCode("ODUBB"), new TeamCode("TUCE"), new TeamCode("CUA"),
      new TeamCode("UCF"), new TeamCode("UF"), new TeamCode("UOFM"), new TeamCode("ULSAN"), new TeamCode("UWF"), new TeamCode("VU"));
  private static final List<Pinger> courseAPingers = ImmutableList.of(new Pinger(BuoyColor.black), new Pinger(BuoyColor.blue), new Pinger(BuoyColor.red),
      new Pinger(BuoyColor.yellow), new Pinger(BuoyColor.green));
  private static final List<Pinger> courseBPingers = ImmutableList.of(new Pinger(BuoyColor.black), new Pinger(BuoyColor.blue), new Pinger(BuoyColor.red),
      new Pinger(BuoyColor.yellow), new Pinger(BuoyColor.green));
  private static final List<Pinger> openTestPingers = ImmutableList.of(new Pinger(BuoyColor.black), new Pinger(BuoyColor.blue), new Pinger(BuoyColor.red),
      new Pinger(BuoyColor.yellow), new Pinger(BuoyColor.green));
  private static final Map<Course, CourseLayout> COURSE_LAYOUT_MAP;
  public static final AtomicInteger port = new AtomicInteger(9999);
  private final NMEAServer nmeaServer;

  static {
    try {
      COURSE_LAYOUT_MAP = ImmutableMap.of(
          Course.courseA, new CourseLayout(Course.courseA, courseAPingers, new URL("http://192.168.1.7:5000"), new URL("http://192.168.1.5:4000")), 
          Course.courseB, new CourseLayout(Course.courseB, courseBPingers, new URL("http://192.168.1.8:5000"), new URL("http://192.168.1.6:4000")), 
          Course.openTest, new CourseLayout(Course.openTest, openTestPingers, new URL("http://127.0.0.1:5000"), new URL("http://127.0.0.1:4000")));
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public AdminResourceConfig() throws MalformedURLException, URISyntaxException {
    this(new CompetitionManagerImpl(new Competition(COMPETITION_DAYS, TEAMS, COURSE_LAYOUT_MAP, true)));
  }

  public AdminResourceConfig(CompetitionManager competitionManager) throws MalformedURLException, URISyntaxException {
    super(competitionManager, JacksonFeatures.class, JacksonObjectMapperProvider.class, MultiPartFeature.class);
    this.registerFinder(new PackageNamesScanner(new String[] { "com.felixpageau.roboboat.mission2015.resources", "com.fasterxml.jackson.jaxrs.base" }, false));
    this.register(new AutomatedDockingResource(competitionManager));
    this.register(new InteropResource(competitionManager));
    this.register(new RunResource(competitionManager));
    this.register(new ObstacleAvoidanceResource(competitionManager));
    this.register(new HeartbeatResource(competitionManager));
    this.register(new PingerResource(competitionManager));
    this.register(new AdminResource(competitionManager));
    this.register(JacksonFeatures.class);
    this.register(JacksonObjectMapperProvider.class);
    this.register(MultiPartFeature.class);

    this.nmeaServer = new NMEAServer(competitionManager, port.get(), createNMEASentenceRegistry(), true);
    this.nmeaServer.start();
  }

  @Override
  public SentenceRegistry createNMEASentenceRegistry() {
    return SentenceRegistryFactory.create2015NMEASentenceRegistry();
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
