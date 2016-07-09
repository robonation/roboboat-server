package com.felixpageau.roboboat.mission.server;

import static com.felixpageau.roboboat.mission.MissionResourceConfig.COMPETITION_NAME;
import static com.felixpageau.roboboat.mission.MissionResourceConfig.COURSE_LAYOUT_MAP;
import static com.felixpageau.roboboat.mission.MissionResourceConfig.OM_PROVIDER;
import static com.felixpageau.roboboat.mission.MissionResourceConfig.TEAMS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;

import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.google.common.collect.ImmutableList;

public class CompetitionTest {

  @Test
  public void testStartNewRun_sequential() {
    TeamCode teamCode = new TeamCode("AUVSI");
    Course course = Course.courseA;
    Competition competition = new Competition(COMPETITION_NAME, COMPETITION_DAYS, TEAMS, COURSE_LAYOUT_MAP, false, false, OM_PROVIDER.getObjectMapper());
    RunSetup rs = competition.startNewRun(competition.findCurrentTimeSlot(course), teamCode);
    assertEquals("1", rs.getRunId().replaceFirst(".*-", ""));
    competition.endRun(course, teamCode);

    rs = competition.startNewRun(competition.findCurrentTimeSlot(course), teamCode);
    assertEquals("2", rs.getRunId().replaceFirst(".*-", ""));
    competition.endRun(course, teamCode);

    rs = competition.startNewRun(competition.findCurrentTimeSlot(course), teamCode);
    assertEquals("3", rs.getRunId().replaceFirst(".*-", ""));
    competition.endRun(course, teamCode);
  }

  @Test
  public void testStartNewRun_newTeam() {
    TeamCode teamCode = new TeamCode("AUVSI");
    Course course = Course.courseA;
    Competition competition = new Competition(COMPETITION_NAME, COMPETITION_DAYS, TEAMS, COURSE_LAYOUT_MAP, false, false, OM_PROVIDER.getObjectMapper());
    RunSetup rs = competition.startNewRun(competition.findCurrentTimeSlot(course), teamCode);
    assertEquals("1", rs.getRunId().replaceFirst(".*-", ""));
    competition.endRun(course, teamCode);

    TeamCode teamCode2 = new TeamCode("DBH");
    rs = competition.startNewRun(competition.findCurrentTimeSlot(course), teamCode2);
    assertEquals("1", rs.getRunId().replaceFirst(".*-", ""));
    competition.endRun(course, teamCode2);

    rs = competition.startNewRun(competition.findCurrentTimeSlot(course), teamCode);
    assertEquals("1", rs.getRunId().replaceFirst(".*-", ""));
    competition.endRun(course, teamCode);
  }

  public static final List<CompetitionDay> COMPETITION_DAYS = ImmutableList.<CompetitionDay> of(new CompetitionDay(LocalDateTime.of(2016, 7, 5, 8, 0),
      LocalDateTime.of(2016, 7, 5, 18, 0)), // Tu
      new CompetitionDay(LocalDateTime.of(2016, 7, 6, 8, 0), LocalDateTime.of(2016, 7, 6, 18, 0)), // We
      new CompetitionDay(LocalDateTime.of(2016, 7, 7, 8, 0), LocalDateTime.of(2016, 7, 7, 18, 0)), // Th
      new CompetitionDay(LocalDateTime.of(2016, 7, 8, 8, 0), LocalDateTime.of(2016, 7, 8, 18, 0)), // Fr
      new CompetitionDay(LocalDateTime.of(2016, 7, 9, 8, 0), LocalDateTime.of(2016, 7, 9, 18, 0)), // Sa
      new CompetitionDay(LocalDateTime.of(2016, 7, 10, 8, 0), LocalDateTime.of(2016, 7, 10, 18, 0)) // Su
      );

  @Test
  public void testStartNewRun_acrossTimeSlot() throws InterruptedException {
    List<CompetitionDay> days = ImmutableList.<CompetitionDay> of(new CompetitionDay(LocalDateTime.now(), LocalDateTime.now().plusSeconds(2)));
    TeamCode teamCode = new TeamCode("AUVSI");
    Course course = Course.courseA;
    Competition competition = new Competition(COMPETITION_NAME, days, TEAMS, COURSE_LAYOUT_MAP, false, false, OM_PROVIDER.getObjectMapper());
    competition.schedule.clear();
    competition.schedule.put(new TimeSlot(Course.courseB, LocalDateTime.now().minusSeconds(1), LocalDateTime.now().plusSeconds(2)), null);
    assertEquals(1, competition.schedule.size());

    RunSetup rs = competition.startNewRun(competition.findCurrentTimeSlot(course), teamCode);
    assertEquals("1", rs.getRunId().replaceFirst(".*-", ""));
    competition.endRun(course, teamCode);

    rs = competition.startNewRun(competition.findCurrentTimeSlot(course), teamCode);
    assertEquals("2", rs.getRunId().replaceFirst(".*-", ""));
    competition.endRun(course, teamCode);

    Thread.sleep(2500);

    rs = competition.startNewRun(competition.findCurrentTimeSlot(course), teamCode);
    assertEquals("3", rs.getRunId().replaceFirst(".*-", ""));
    competition.endRun(course, teamCode);
    assertTrue(competition.schedule.size() >= 2);

    rs = competition.startNewRun(competition.findCurrentTimeSlot(course), teamCode);
    assertEquals("4", rs.getRunId().replaceFirst(".*-", ""));
    competition.endRun(course, teamCode);
  }
}
