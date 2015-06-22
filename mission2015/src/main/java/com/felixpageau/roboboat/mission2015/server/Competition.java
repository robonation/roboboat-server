package com.felixpageau.roboboat.mission2015.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.felixpageau.roboboat.mission2015.structures.BuoyColor;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.felixpageau.roboboat.mission2015.utils.NMEAUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;

public class Competition {
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd-hh");
  private final DateTimeFormatter dateRunIdFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
  private final Map<Course, CourseLayout> layoutMap;
  private final Map<Course, TeamCode> teamInWater = new ConcurrentHashMap<>();
  private final List<CompetitionDay> competitionDays;
  private final Multimap<TimeSlot, RunArchiver> results = ArrayListMultimap.create();
  private final Map<Course, RunArchiver> activeRuns = new HashMap<>();
  private final Map<TimeSlot, TeamCode> schedule = new HashMap<>();
  private final List<TeamCode> teams;
  private final boolean activatePinger;
  private final EventBus eventBus = new EventBus();

  public Competition(List<CompetitionDay> competitionDays, List<TeamCode> teams, Map<Course, CourseLayout> layoutMap, boolean activatePinger)
      throws MalformedURLException {
    this.competitionDays = Preconditions.checkNotNull(competitionDays);
    this.layoutMap = Preconditions.checkNotNull(layoutMap);
    this.teams = ImmutableList.copyOf(Preconditions.checkNotNull(teams, "The provided team list cannot be null"));
    this.activatePinger = activatePinger;

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
    startNewRun(TimeSlot.DEFAULT_TIMESLOT, new TeamCode("AUVSI"));
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
    return results;
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
    return teamInWater.get(course);
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

  public TimeSlot findCurrentTimeSlot(Course course) {
    List<TimeSlot> slots = new ArrayList<>(schedule.keySet());
    Collections.sort(slots, new TimeSlotComparator());
    for (TimeSlot timeSlot : slots) {
      if (timeSlot.getCourse().equals(course) && timeSlot.getStartTime().isBefore(LocalDateTime.now()) && timeSlot.getEndTime().isAfter(LocalDateTime.now())) {
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

    Collection<RunArchiver> previousRuns = results.get(slot);
    RunArchiver lastRun = activeRuns.get(slot.getCourse());
    if (lastRun != null) {
      lastRun.endRun();
    }
    int runCount = (previousRuns != null) ? previousRuns.size() : 0;
    String runId = String.format("%s-%s-%s", slot.getCourse(), slot.getStartTime().format(dateRunIdFormat), runCount);
    CourseLayout layout = layoutMap.get(slot.getCourse());
    RunSetup newSetup = RunSetup.generateRandomSetup(layoutMap.get(slot.getCourse()), teamCode, runId);

    RunArchiver newRun = new RunArchiver(newSetup, new File("competition-log." + LocalDateTime.now().format(DATE_FORMAT)), eventBus);
    newRun
        .addEvent(new Event(newRun.getStartTime(), String.format("%s - %s - Start run (config %s)", newSetup.getCourse(), newSetup.getActiveTeam(), newSetup)));
    activeRuns.put(slot.getCourse(), newRun);
    results.put(slot, newRun);

    if (activatePinger && !slot.getCourse().equals(Course.openTest)) {
      boolean activated = false;
      for (int j = 0; j < 10 && !activated; j++) {
        try (Socket s = new Socket(layout.getPingerControlServer().getHost(), layout.getPingerControlServer().getPort());
            Writer w = new OutputStreamWriter(s.getOutputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
          for (int i = 0; i < layout.getPingers().size(); i++) {
            if (newSetup.getActivePinger().equals(layout.getPingers().get(i))) {
              String pingerActivationMessage = NMEAUtils.formatNMEAmessage(NMEAUtils.formatPingerNMEAmessage(slot.getCourse(), i + 1));
              w.write(pingerActivationMessage);
              System.out.println(pingerActivationMessage);
              w.flush();
              activated = true;
              break;
            }
          }
          System.out.println(r.readLine());
          System.out.println(r.readLine());
        } catch (UnknownHostException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return newSetup;
  }

  public synchronized void endRun(Course course, TeamCode teamCode) {
    RunArchiver ra = activeRuns.get(course);
    if (ra != null) {
      ra.endRun();
      activeRuns.remove(course);
    }
  }

  public static void main(String[] args) throws MalformedURLException {
    List<CompetitionDay> days = ImmutableList.of(new CompetitionDay(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1)));
    Map<Course, CourseLayout> layout = ImmutableMap.of(Course.courseA, new CourseLayout(Course.courseA, ImmutableList.of(new Pinger(BuoyColor.black)), new URL(
        "127.0.0.1:3000"), new URL("127.0.0.1:4000")));
    Competition c = new Competition(days, ImmutableList.of(new TeamCode("AUVSI")), layout, false);
    List<TimeSlot> slots = new ArrayList<>(c.schedule.keySet());

    c.startNewRun(slots.get(0), new TeamCode("AUVSI"));
  };
}
