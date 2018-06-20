/**
 * 
 */
package com.felixpageau.roboboat.mission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.felixpageau.roboboat.mission.nmea.SentenceRegistry;
import com.felixpageau.roboboat.mission.resources.AutomatedDockingResource;
import com.felixpageau.roboboat.mission.resources.FollowTheLeaderResource;
import com.felixpageau.roboboat.mission.resources.HeartbeatResource;
import com.felixpageau.roboboat.mission.resources.RunResource;
import com.felixpageau.roboboat.mission.server.CompetitionDay;
import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.felixpageau.roboboat.mission.server.CourseLayout;
import com.felixpageau.roboboat.mission.structures.Code;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.DockingBay;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Base class for a CompetitionResourceConfig
 */
@ParametersAreNonnullByDefault
@ThreadSafe
public abstract class CompetitionResourceConfig extends ResourceConfig {
  private static final Logger LOG = LoggerFactory.getLogger(CompetitionResourceConfig.class);
  public static final JacksonObjectMapperProvider OM_PROVIDER = new JacksonObjectMapperProvider();
  public static final String COMPETITION_NAME = "RoboBoat 2018";
  public static final List<CompetitionDay> COMPETITION_DAYS = ImmutableList.<CompetitionDay> of(new CompetitionDay(LocalDateTime.of(2016, 7, 5, 8, 0),
      LocalDateTime.of(2016, 7, 5, 18, 0)), // Tu
      new CompetitionDay(LocalDateTime.of(2016, 7, 6, 8, 0), LocalDateTime.of(2016, 7, 6, 18, 0)), // We
      new CompetitionDay(LocalDateTime.of(2016, 7, 7, 8, 0), LocalDateTime.of(2016, 7, 7, 18, 0)), // Th
      new CompetitionDay(LocalDateTime.of(2016, 7, 8, 8, 0), LocalDateTime.of(2016, 7, 8, 18, 0)), // Fr
      new CompetitionDay(LocalDateTime.of(2016, 7, 9, 8, 0), LocalDateTime.of(2016, 7, 9, 18, 0)), // Sa
      new CompetitionDay(LocalDateTime.of(2016, 7, 10, 8, 0), LocalDateTime.of(2016, 7, 10, 18, 0)) // Su
      );
  public static final List<TeamCode> TEAMS = ImmutableList.of(new TeamCode("AUVSI"), new TeamCode("TUD"), new TeamCode("ERAU"), new TeamCode("FAU"), new TeamCode("GIT"), new TeamCode("HHS"), new TeamCode("ITSN"), new TeamCode("NHHS"), new TeamCode("SRM"), new TeamCode("VTEC"), new TeamCode("UNDIP"), new TeamCode("UCF"), new TeamCode("IOWA"), new TeamCode("UOFM"),new TeamCode("UOTTA"));
  protected static final List<DockingBay> bays = ImmutableList.copyOf(Arrays.stream(Code.values()).map(x -> new DockingBay(x)).collect(Collectors.toList()));
  protected static final Map<Course, String> PRIVATE_IP = ImmutableMap.of(Course.courseA, "192.168.1.10", Course.courseB, "192.168.1.20", Course.courseC, "192.168.1.30", Course.courseD, "192.168.1.40");
  public static final Map<Course, String> PUBLIC_IP = ImmutableMap.of(Course.courseA, "192.168.65.2", Course.courseB, "192.168.66.2", Course.courseC, "192.168.67.2", Course.courseD, "192.168.68.2");
  public static final Map<Course, CourseLayout> COURSE_LAYOUT_MAP;
  
  public static final AtomicInteger port = new AtomicInteger(9999);
  private final CompetitionManager competitionManager;
  private final NMEAServer nmeaServer;
  
  static {
    try {
      File f = new File("/home/robonation/course");
      Course configuredCourse = null;
      if (f.exists()) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
          configuredCourse = Course.fromString(br.readLine());
        } catch (IOException | NullPointerException | IllegalArgumentException e) {
          LOG.warn(e.getMessage(), e);
        }
      }
      String ipAddress = InetAddress.getLocalHost().getHostAddress().trim();
      List<DockingBay> bays = ImmutableList.copyOf(Arrays.stream(Code.values()).map(x -> new DockingBay(x)).collect(Collectors.toList()));
      ImmutableMap.Builder<Course, CourseLayout> builder = ImmutableMap.builder();
      if (Course.courseA.equals(configuredCourse) || ipAddress.equals(PRIVATE_IP.get(Course.courseA))) {
        LOG.info("** Configuring as COURSE A. Because my IP is '" + ipAddress + "' or because configured as " + configuredCourse);
        builder.put(Course.courseA, new CourseLayout(Course.courseA, bays, PUBLIC_IP.get(Course.courseA), new URL("http://192.168.1.5:4000"), new URL("http://192.168.1.12:5000"), new URL("https://192.168.1.13:6722")));
      } else if (Course.courseB.equals(configuredCourse) || ipAddress.equals(PRIVATE_IP.get(Course.courseB))) {
        LOG.info("** Configuring as COURSE B. Because my IP is '" + ipAddress + "' or because configured as " + configuredCourse);
        builder.put(Course.courseB, new CourseLayout(Course.courseB, bays, PUBLIC_IP.get(Course.courseB), new URL("http://192.168.1.6:4000"), new URL("http://192.168.1.22:5000"), new URL("https://192.168.1.23:6722")));
      } else if (Course.courseC.equals(configuredCourse) || ipAddress.equals(PRIVATE_IP.get(Course.courseC))) {
        LOG.info("** Configuring as COURSE C. Because my IP is '" + ipAddress + "' or because configured as " + configuredCourse);
        builder.put(Course.courseC, new CourseLayout(Course.courseC, bays, PUBLIC_IP.get(Course.courseC), new URL("http://192.168.1.7:4000"), new URL("http://192.168.1.32:5000"), new URL("https://192.168.1.33:6722")));
      } else if (Course.courseD.equals(configuredCourse) || ipAddress.equals(PRIVATE_IP.get(Course.courseD))) {
        LOG.info("** Configuring as COURSE D. Because my IP is '" + ipAddress + "' or because configured as " + configuredCourse);
        builder.put(Course.courseD, new CourseLayout(Course.courseD, bays, PUBLIC_IP.get(Course.courseD), new URL("http://192.168.1.8:4000"), new URL("http://192.168.1.42:5000"), new URL("https://192.168.1.43:6722")));
      } else {
        LOG.info("** Configuring as TEST SERVER. Because my IP is '" + ipAddress + "'");
        builder.put(Course.testCourse1, new CourseLayout(Course.testCourse1, bays, "127.0.0.1", new URL("http://127.0.0.1:4000"), new URL("http://127.0.0.1:5000"), new URL("http://127.0.0.1:6722")));
      }
      COURSE_LAYOUT_MAP = builder.build();
    } catch (MalformedURLException | UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @param competitionManager
   *          the competition manager instance
   * @param classes
   *          classes the classes to load in jetty's context
   */
  public CompetitionResourceConfig(CompetitionManager competitionManager, Class<?>... classes) {
    this(competitionManager, ImmutableSet.of(), classes);
    this.register(MultiPartFeature.class);
    this.register(JacksonFeatures.class);
    this.register(JacksonObjectMapperProvider.class);
    this.registerFinder(new PackageNamesScanner(new String[] { "com.fasterxml.jackson.jaxrs.base" }, false));
    this.register(new AutomatedDockingResource(competitionManager));
    this.register(new RunResource(competitionManager));
    this.register(new HeartbeatResource(competitionManager));
    this.register(new FollowTheLeaderResource(competitionManager));
    this.register(CORSResponseFilter.class);
  }

  /**
   * @param competitionManager
   *          the competition manager instance
   * @param original
   *          the parent {@link ResourceConfig}
   */
  public CompetitionResourceConfig(CompetitionManager competitionManager, Set<Object> components, Class<?>... classes) {
    super(classes);
    this.registerInstances(ImmutableSet.copyOf(components));
    this.competitionManager = Preconditions.checkNotNull(competitionManager, "competitionManager cannot be null");
    this.nmeaServer = createNMEAServer();
  }

  /**
   * @return the {@link CompetitionManager}
   */
  public CompetitionManager getCompetition() {
    return competitionManager;
  }

  /**
   * Overrideable creator for the {@link NMEAServer} instance
   * @return a {@link SentenceRegistry}
   */
  public SentenceRegistry createNMEASentenceRegistry() {
    return SentenceRegistryFactory.createNMEASentenceRegistry();
  }
  
  /**
   * @return the {@link NMEAServer}
   */
  public NMEAServer getNMEAServer() {
    return nmeaServer;
  }

  /**
   * Overrideable creator for the {@link NMEAServer} instance
   * @return a {@link SentenceRegistry}
   */
  public NMEAServer createNMEAServer() {
    NMEAServer nmeaServer = new NMEAServer(competitionManager, port.get(), createNMEASentenceRegistry(), true);
    nmeaServer.start();
    return nmeaServer;
  }
}
