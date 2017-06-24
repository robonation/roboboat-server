package com.felixpageau.roboboat.mission.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felixpageau.roboboat.mission.App;
import com.felixpageau.roboboat.mission.structures.Challenge;
import com.felixpageau.roboboat.mission.structures.Code;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.felixpageau.roboboat.mission.utils.NMEAUtils;
import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.CharStreams;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "UEC_USE_ENUM_COLLECTIONS", justification = "There are no ConcurrentEnumMap or EnumMultimap")
public class Competition {
  private static final Logger LOG = LoggerFactory.getLogger(Competition.class);
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd-hh");
  private static final DateTimeFormatter DATE_RUN_ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
  private final String name;
  private final Map<Course, CourseLayout> layoutMap;
  private final Map<Course, TeamCode> teamInWater = new ConcurrentHashMap<>();
  private final List<CompetitionDay> competitionDays;
  private final Multimap<TimeSlot, RunArchiver> results = ArrayListMultimap.create();
  private final Map<Course, RunArchiver> activeRuns = new HashMap<>();
  protected final Map<TimeSlot, TeamCode> schedule = new HashMap<>();
  private final List<TeamCode> teams;
  private final boolean activateCarousel;
  private final boolean activatePinger;
  private final boolean activateLCD;
  private final Executor exec = Executors.newCachedThreadPool();
  private final EventBus bus = new EventBus();
  private final File f;
  protected final ObjectMapper om;

  @SuppressFBWarnings(value = { "EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS", "SIC_INNER_SHOULD_BE_STATIC_ANON" })
  public Competition(String name, List<CompetitionDay> competitionDays, List<TeamCode> teams, Map<Course, CourseLayout> layoutMap, boolean activatePinger,
      boolean activateLCD, boolean activateCarousel, ObjectMapper om) {
    this.name = Preconditions.checkNotNull(name, "name cannot be null");
    this.competitionDays = Preconditions.checkNotNull(competitionDays, "competitionDays cannot be null");
    this.layoutMap = Preconditions.checkNotNull(layoutMap, "layoutMap cannot be null");
    this.teams = ImmutableList.copyOf(Preconditions.checkNotNull(teams, "The provided team list cannot be null"));
    this.activatePinger = activatePinger;
    this.activateLCD = activateLCD;
    this.activateCarousel = activateCarousel;
    this.om = Preconditions.checkNotNull(om);
    this.bus.register(new EventBusChangeRecorder());

    // Generate timeslots
    for (CompetitionDay day : competitionDays) {
      LocalDateTime timeSlotStart = day.getStartTime();
      int slotDurationMs = Config.TIME_SLOT_DURATION_MIN.get() * 60 * 1000;
      int courseOffset = Config.TIME_SLOT_DURATION_MIN.get() * 60 * 1000 / 2;
      while (timeSlotStart.isBefore(day.getEndTime())) {
        schedule.put(
            new TimeSlot(Course.courseA, timeSlotStart.plus(courseOffset, ChronoUnit.MILLIS), timeSlotStart.plus(courseOffset + slotDurationMs,
                ChronoUnit.MILLIS)), null);
        schedule.put(
            new TimeSlot(Course.courseB, timeSlotStart.plus(courseOffset, ChronoUnit.MILLIS), timeSlotStart.plus(courseOffset + slotDurationMs,
                ChronoUnit.MILLIS)), null);
        timeSlotStart = timeSlotStart.plus(slotDurationMs, ChronoUnit.MILLIS);
      }
    }

    // Always have a default run in OpenTest
    // startNewRun(TimeSlot.DEFAULT_TIMESLOT, new TeamCode("AUVSI"));

    this.f = new File(String.format("%s/competition-log.%s.json", name, LocalDateTime.now().format(DATE_FORMAT)));
    if (!f.exists()) {
      try {
        if (!f.getParentFile().mkdirs() && !f.createNewFile()) {
          throw new RuntimeException(String.format("Unable to create json file at (%s). Restart server", f.getAbsolutePath()));
        }
      } catch (IOException e) {
        throw new RuntimeException(String.format("Unable to create json file at (%s). Restart server", f.getAbsolutePath()), e);
      }
    } else {
      try {
        results.putAll(om.readValue(f, new TypeReference<ArrayListMultimap<TimeSlot, RunArchiver>>() {}));
      } catch (IOException e) {
        LOG.warn("Unable to read the json file");
      }
    }
  }

  public List<TeamCode> getTeams() {
    return ImmutableList.copyOf(teams);
  }

  public Map<TimeSlot, TeamCode> getSchedule() {
    return ImmutableMap.copyOf(schedule);
  }

  public Map<Course, RunArchiver> getActiveRuns() {
    return ImmutableMap.copyOf(activeRuns);
  }

  public RunArchiver getActiveRun(Course course) {
    return Optional.ofNullable(activeRuns.get(course)).orElse(null);
  }

  public List<CompetitionDay> getCompetitionDays() {
    return ImmutableList.copyOf(competitionDays);
  }

  public Multimap<TimeSlot, RunArchiver> getResults() {
    return ImmutableMultimap.copyOf(results);
  }

  public synchronized void assignTeam(TimeSlot slot, TeamCode teamCode) {
    Preconditions.checkNotNull(slot);

    if (schedule.containsKey(slot) && schedule.get(slot) == null) {
      schedule.put(slot, teamCode);
    }
  }

  /**
   * @return the teamInWater
   */
  public TeamCode getTeamInWater(Course course) {
    RunArchiver ra = activeRuns.get(course);
    if (ra != null) {
      return ra.getRunSetup().getActiveTeam();
    }
    return null;
  }

  /**
   * @return the CourseLayout
   */
  public CourseLayout getCourseLayout(Course course) {
    return layoutMap.get(course);
  }

  /**
   * @param teamInWater
   *          the teamInWater to set
   */
  public void setTeamInWater(Course course, TeamCode team) {
    teamInWater.put(course, team);
  }

  public TimeSlot findPreviousTimeSlot(Course course) {
    TimeSlot current = findCurrentTimeSlot(course);
    List<TimeSlot> slots = new ArrayList<>(schedule.keySet());
    Collections.sort(slots, new TimeSlotComparator());
    slots.remove(current);
    for (TimeSlot timeSlot : slots) {
      if (timeSlot.getCourse() == course && timeSlot.getStartTime().isBefore(LocalDateTime.now()) && timeSlot.getEndTime().isAfter(LocalDateTime.now())) {
        return timeSlot;
      }
    }
    // TODO remove this
    TimeSlot newSlot = new TimeSlot(course, LocalDateTime.now(), LocalDateTime.now().plusMinutes(20));
    schedule.put(newSlot, null);
    return newSlot;
  }

  public TimeSlot findCurrentTimeSlot(Course course) {
    List<TimeSlot> slots = new ArrayList<>(schedule.keySet());
    Collections.sort(slots, new TimeSlotComparator());
    for (TimeSlot timeSlot : slots) {
      if (timeSlot.getCourse() == course && timeSlot.getStartTime().isBefore(LocalDateTime.now()) && timeSlot.getEndTime().isAfter(LocalDateTime.now())) {
        return timeSlot;
      }
    }
    // TODO remove this
    TimeSlot newSlot = new TimeSlot(course, LocalDateTime.now(), LocalDateTime.now().plusMinutes(20));
    schedule.put(newSlot, null);
    return newSlot;
  }

  public synchronized RunSetup startNewRun(TimeSlot slot, TeamCode teamCode) {
    Preconditions.checkNotNull(slot);
    Preconditions.checkNotNull(teamCode);

    RunArchiver lastRun = activeRuns.get(slot.getCourse());
    if (lastRun != null) {
      endRun(slot.getCourse(), teamCode);
    }
    List<RunArchiver> previousRuns = new ArrayList<>(results.get(findPreviousTimeSlot(slot.getCourse())));
    previousRuns.addAll(results.get(slot));
    Collections.sort(previousRuns, new Comparator<RunArchiver>() {
      @Override
      public int compare(RunArchiver o1, RunArchiver o2) {
        if (o1 == null) {
          return -1;
        }
        if (o2 == null) {
          return 1;
        }
        return o1.getStartTime().compareTo(o2.getStartTime());
      }
    });
    if (previousRuns != null && previousRuns.size() > 0) {
      lastRun = Iterables.getLast(previousRuns);
    }
    if (lastRun == null || !lastRun.getRunSetup().getActiveTeam().equals(teamCode)) {
      lastRun = null;
    }
    int runCount = (lastRun != null) ? Integer.parseInt(lastRun.getRunSetup().getRunId().replaceFirst(".*-", "")) + 1 : 1;
    String runId = String.format("%s-%s-%s", slot.getCourse(), slot.getStartTime().format(DATE_RUN_ID_FORMATTER), runCount);
    CourseLayout layout = layoutMap.get(slot.getCourse());
    RunSetup newSetup = RunSetup.generateRandomSetup(layoutMap.get(slot.getCourse()), teamCode, runId);

    RunArchiver newRun = new RunArchiver(newSetup, new File(String.format("%s/competition-log.%s", name, LocalDateTime.now().format(DATE_FORMAT))), bus);
    newRun.addEvent(new StructuredEvent(newSetup.getCourse(), newSetup.getActiveTeam(), Challenge.none, String.format("Start run (config %s)", newSetup)));
    activeRuns.put(slot.getCourse(), newRun);
    results.put(slot, newRun);

    if (activateLCD && slot.getCourse() != Course.openTest) {
      exec.execute(new ActivateLCD(layout, newSetup));
      LOG.info("Activated LCD");
    } else {
      LOG.info("Don't activate LCD");
    }
    
    if (activateCarousel && slot.getCourse() != Course.openTest) {
      exec.execute(new ActivateCarousel(layout, true));
      LOG.info("Activated Carousel");
    } else {
      LOG.info("Don't activate Carousel");
    }

    if (activatePinger && slot.getCourse() != Course.openTest) {
      exec.execute(new ActivatePinger(layout, newSetup));
      LOG.info("Activated Pinger");
    } else {
      LOG.info("Don't activate Pinger");
    }

    return newSetup;
  }

  public static class ActivateCarousel implements Runnable {
    private final CourseLayout layout;
    private final boolean enabled;

    public ActivateCarousel(CourseLayout layout, boolean enabled) {
      this.layout = Preconditions.checkNotNull(layout, "layout cannot be null");
      this.enabled = enabled;
    }

    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY")
    @Override
    public void run() {
      boolean activated = false;
      for (int j = 0; j < 10 && !activated; j++) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
          if (!enabled) {
            CloseableHttpResponse resp = client.execute(new HttpHost(layout.getCarouselControlServer().getHost(), layout.getCarouselControlServer().getPort()),
                new HttpGet(layout.getCarouselControlServer().toString() + "/clear"));
            LOG.info(String.format("TURN OFF LCD: %d %s", resp.getStatusLine().getStatusCode(),
                CharStreams.toString(new InputStreamReader(resp.getEntity().getContent(), Charsets.UTF_8))));
            activated = true;
          } else {
            String value = "1";
            CloseableHttpResponse resp = client.execute(new HttpHost(layout.getCarouselControlServer().getHost(), layout.getCarouselControlServer().getPort()),
                new HttpGet(layout.getCarouselControlServer().toString() + "/activate/" + value.toLowerCase()));
            LOG.error("Activation URL: " + layout.getCarouselControlServer().toString() + "/activate/" + value.toLowerCase());
            if (resp.getStatusLine().getStatusCode() == 200) {
              activated = true;
            }
          }
        } catch (UnknownHostException e) {
          LOG.error(String.format("Failed to find carousel server (%s)", layout.getCarouselControlServer().toString()), e);
        } catch (IOException e) {
          LOG.error(String.format("Comm failed with carousel server (%s)", layout.getCarouselControlServer().toString()), e);
        }
      }
    }
  }
  
  public static class ActivatePinger implements Runnable {
    private final CourseLayout layout;
    private final RunSetup newSetup;

    public ActivatePinger(CourseLayout layout, RunSetup newSetup) {
      this.layout = Preconditions.checkNotNull(layout, "layout cannot be null");
      this.newSetup = Preconditions.checkNotNull(newSetup, "newSetup cannot be null");
    }

    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY")
    @Override
    public void run() {
      boolean activated = false;
      for (int j = 0; j < 10 && !activated; j++) {
        try (Socket s = new Socket(layout.getPingerControlServer().getHost(), layout.getPingerControlServer().getPort());
            Writer w = new OutputStreamWriter(s.getOutputStream(), App.APP_CHARSET);
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream(), App.APP_CHARSET))) {
          
          LOG.error("*** Pinger value in setup: " + newSetup);
          LOG.error("*** Pinger value in docking sequence: " + newSetup.getActiveDockingSequence());
          LOG.error("*** Pinger value in active pinger: " + newSetup.getActiveDockingSequence().getActivePinger());
          
          if (com.felixpageau.roboboat.mission.structures.Code.none.equals(newSetup.getActiveDockingSequence().getActivePinger())) {
            String pingerActivationMessage = NMEAUtils.formatNMEAmessage(NMEAUtils.formatPingerNMEAmessage(layout.getCourse(), 0));
            w.write(pingerActivationMessage);
            System.out.println("TURN OFF PINGER: " + pingerActivationMessage);
            w.flush();
            activated = true;
          } else {
            System.out.println(String.format("** Available pingers: %s **", Arrays.stream(Code.values()).map(x -> x.getValue()).collect(Collectors.joining())));
            System.out.println(String.format("** Active pingers: %s **", newSetup.getActiveDockingSequence().getActivePinger().getValue()));
            int activate = Integer.parseInt(newSetup.getActiveDockingSequence().getActivePinger().getValue());

            System.out.println("**** Activation values: " + activate);
            String pingerActivationMessage = NMEAUtils.formatNMEAmessage(NMEAUtils.formatPingerNMEAmessage(layout.getCourse(), activate));
            w.write(pingerActivationMessage);
            System.out.println(pingerActivationMessage);
            w.flush();
            activated = true;
          }
          System.out.println(r.readLine());
          System.out.println(r.readLine());
        } catch (UnknownHostException e) {
          LOG.error(String.format("Failed to find pinger server (%s)", layout.getPingerControlServer().toString()), e);
        } catch (IOException e) {
          LOG.error(String.format("Comm failed with pinger server (%s)", layout.getPingerControlServer().toString()), e);
        }
      }
    }
  }

  public static class ActivateLCD implements Runnable {
    private final CourseLayout layout;
    private final RunSetup newSetup;

    public ActivateLCD(CourseLayout layout, RunSetup newSetup) {
      this.layout = Preconditions.checkNotNull(layout, "layout cannot be null");
      this.newSetup = Preconditions.checkNotNull(newSetup, "newSetup cannot be null");
    }

    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY")
    @Override
    public void run() {
      boolean activated = false;
      for (int j = 0; j < 10 && !activated; j++) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
          if (com.felixpageau.roboboat.mission.structures.Code.none == newSetup.getActiveDockingSequence().get7Seg()) {
            CloseableHttpResponse resp = client.execute(new HttpHost(layout.getSevenSegControlServer().getHost(), layout.getSevenSegControlServer().getPort()),
                new HttpGet(layout.getSevenSegControlServer().toString() + "/clear"));
            LOG.info(String.format("TURN OFF LCD: %d %s", resp.getStatusLine().getStatusCode(),
                CharStreams.toString(new InputStreamReader(resp.getEntity().getContent(), Charsets.UTF_8))));
            activated = true;
          } else {
            String value = newSetup.getActiveDockingSequence().get7Seg().getValue();
            CloseableHttpResponse resp = client.execute(new HttpHost(layout.getSevenSegControlServer().getHost(), layout.getSevenSegControlServer().getPort()),
                new HttpGet(layout.getSevenSegControlServer().toString() + "/activate/" + value.toLowerCase()));
            LOG.error("Activation URL: " + layout.getSevenSegControlServer().toString() + "/activate/" + value.toLowerCase());
            if (resp.getStatusLine().getStatusCode() == 200) {
              activated = true;
            }
          }
        } catch (UnknownHostException e) {
          LOG.error(String.format("Failed to find lcd server (%s)", layout.getSevenSegControlServer().toString()), e);
        } catch (IOException e) {
          LOG.error(String.format("Comm failed with lcd server (%s)", layout.getSevenSegControlServer().toString()), e);
        }
      }
    }
  }
 
  public synchronized void endRun(Course course, TeamCode teamCode) {
    RunArchiver ra = activeRuns.get(course);
    if (ra != null) {
      ra.endRun(new StructuredEvent(course, teamCode, Challenge.none, "End run"));
      if (activateCarousel && Course.openTest != course) {
        exec.execute(new ActivateCarousel(layoutMap.get(course), false));
      }
      if (activatePinger && Course.openTest != course) {
        exec.execute(new ActivateLCD(layoutMap.get(course), RunSetup.NO_RUN));
      }
      if (activatePinger && Course.openTest != course) {
        exec.execute(new ActivatePinger(layoutMap.get(course), RunSetup.NO_RUN));
      }
      activeRuns.remove(course);
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("competitionDays", competitionDays).add("teams", teams).toString();
  }

  class EventBusChangeRecorder {
    @Subscribe
    public void recordCustomerChange(Event e) {
      try {
        om.writeValue(f, results);
      } catch (IOException e1) {
        LOG.warn(e.getMessage(), e);
      }
    }
  }
}
