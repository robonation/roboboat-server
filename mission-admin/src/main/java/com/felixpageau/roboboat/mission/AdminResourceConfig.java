package com.felixpageau.roboboat.mission;

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
import com.felixpageau.roboboat.mission.nmea.SentenceRegistry;
import com.felixpageau.roboboat.mission.resources.AdminResource;
import com.felixpageau.roboboat.mission.resources.AutomatedDockingResource;
import com.felixpageau.roboboat.mission.resources.HeartbeatResource;
import com.felixpageau.roboboat.mission.resources.InteropResource;
import com.felixpageau.roboboat.mission.resources.ObstacleAvoidanceResource;
import com.felixpageau.roboboat.mission.resources.PingerResource;
import com.felixpageau.roboboat.mission.resources.RunResource;
import com.felixpageau.roboboat.mission.server.Competition;
import com.felixpageau.roboboat.mission.server.CompetitionDay;
import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.server.CourseLayout;
import com.felixpageau.roboboat.mission.server.impl.CompetitionManagerImpl;
import com.felixpageau.roboboat.mission.structures.BuoyColor;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.DockingBay;
import com.felixpageau.roboboat.mission.structures.Pinger;
import com.felixpageau.roboboat.mission.structures.Symbol;
import com.felixpageau.roboboat.mission.structures.SymbolColor;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Sets the resource configuration for the web-application to use Jackson for
 * marshalling/unmarshalling
 */
@SuppressFBWarnings(value = "UUF_UNUSED_FIELD")
public class AdminResourceConfig extends CompetitionResourceConfig {
  private static final JacksonObjectMapperProvider OM_PROVIDER = new JacksonObjectMapperProvider();
  private static final String COMPETITION_NAME = "RoboBoat 2016";
  private static final List<CompetitionDay> COMPETITION_DAYS = ImmutableList.<CompetitionDay> of(
      new CompetitionDay(LocalDateTime.of(2016, 7, 5, 8, 0), LocalDateTime.of(2016, 7, 5, 18, 0)), // Tu
      new CompetitionDay(LocalDateTime.of(2016, 7, 6, 8, 0), LocalDateTime.of(2016, 7, 6, 18, 0)), // We
      new CompetitionDay(LocalDateTime.of(2016, 7, 7, 8, 0), LocalDateTime.of(2016, 7, 7, 18, 0)), // Th
      new CompetitionDay(LocalDateTime.of(2016, 7, 8, 8, 0), LocalDateTime.of(2016, 7, 8, 18, 0)), // Fr
      new CompetitionDay(LocalDateTime.of(2016, 7, 9, 8, 0), LocalDateTime.of(2016, 7, 9, 18, 0)), // Sa
      new CompetitionDay(LocalDateTime.of(2016, 7, 10, 8, 0), LocalDateTime.of(2016, 7, 10, 18, 0)) // Su
      );
  private static final List<TeamCode> TEAMS = TeamCode.of("AUVSI", "DBH", "EEPIS", "ERAU", "FAU", "GIT", "NCKU", "ODUSM", "ODUBB", "TUCE", "CUA", "UCF", "UF",
      "UOFM", "ULSAN", "UWF", "VU");
  private static final List<DockingBay> dockingBaysA = ImmutableList.of(new DockingBay(Symbol.circle, SymbolColor.black), new DockingBay(Symbol.circle,
      SymbolColor.green), new DockingBay(Symbol.triangle, SymbolColor.green));
  private static final List<DockingBay> dockingBaysB = ImmutableList.of(new DockingBay(Symbol.triangle, SymbolColor.black), new DockingBay(Symbol.circle, SymbolColor.blue), new DockingBay(Symbol.cruciform, SymbolColor.green));
  private static final List<DockingBay> dockingBaysOpenTest = ImmutableList.of(new DockingBay(Symbol.triangle, SymbolColor.red), new DockingBay( Symbol.cruciform, SymbolColor.black), new DockingBay(Symbol.cruciform, SymbolColor.blue));
  private static final List<Pinger> courseAPingers = ImmutableList.of(new Pinger(BuoyColor.black), new Pinger(BuoyColor.green), new Pinger(BuoyColor.red));
  private static final List<Pinger> courseBPingers = ImmutableList.of(new Pinger(BuoyColor.green), new Pinger(BuoyColor.red), new Pinger(BuoyColor.black));
  private static final List<Pinger> openTestPingers = ImmutableList.of(new Pinger(BuoyColor.black), new Pinger(BuoyColor.blue), new Pinger(BuoyColor.red), new Pinger(BuoyColor.yellow), new Pinger(BuoyColor.green));
  public static final Map<Course, CourseLayout> COURSE_LAYOUT_MAP;
  public static final AtomicInteger port = new AtomicInteger(9999);
  private final NMEAServer nmeaServer;

  static {
    try {
      COURSE_LAYOUT_MAP = ImmutableMap.of(
          Course.courseA, new CourseLayout(Course.courseA, courseAPingers, dockingBaysA, new URL("http://192.168.1.7:5000")),
          Course.courseB, new CourseLayout(Course.courseB, courseBPingers, dockingBaysB, new URL("http://192.168.1.8:5000")),
          Course.openTest, new CourseLayout(Course.openTest, openTestPingers, dockingBaysOpenTest, new URL("http://127.0.0.1:5000")));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public AdminResourceConfig() throws URISyntaxException {
    this(new CompetitionManagerImpl(new Competition(COMPETITION_NAME, COMPETITION_DAYS, TEAMS, COURSE_LAYOUT_MAP, true, OM_PROVIDER.getObjectMapper()),
        OM_PROVIDER.getObjectMapper()));
  }

  public AdminResourceConfig(CompetitionManager competitionManager) throws URISyntaxException {
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
