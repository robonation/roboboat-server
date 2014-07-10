package com.felixpageau.roboboat.mission2014.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import jersey.repackaged.com.google.common.base.Preconditions;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import jersey.repackaged.com.google.common.collect.Multimap;

import org.joda.time.DateTime;

import com.felixpageau.roboboat.mission2014.structures.BuoyColor;
import com.felixpageau.roboboat.mission2014.structures.BuoyPosition;
import com.felixpageau.roboboat.mission2014.structures.Course;
import com.felixpageau.roboboat.mission2014.structures.Datum;
import com.felixpageau.roboboat.mission2014.structures.Latitude;
import com.felixpageau.roboboat.mission2014.structures.Longitude;
import com.felixpageau.roboboat.mission2014.structures.TeamCode;
import com.felixpageau.roboboat.mission2014.utils.NMEAUtils;

public class Competition {
    private final DateFormat dateRunIdFormat = new SimpleDateFormat("yyyyMMddHHmm");
    private final Map<Course, CourseLayout> layoutMap;
    private final Map<Course, TeamCode> teamInWater = new ConcurrentHashMap<>();
    private final List<CompetitionDay> competitionDays;
    private final Multimap<TimeSlot, RunArchiver> results = new ArrayListMultimap();
    private final Map<Course, RunArchiver> activeRuns = new HashMap<>();
    private final Map<TimeSlot, TeamCode> schedule = new HashMap<>();
    private final List<TeamCode> teams = new CopyOnWriteArrayList<>();
    
    public Competition() throws MalformedURLException {
        DateTime s, e;
        Calendar c = Calendar.getInstance(Config.TIME_ZONE);
        c.set(2014, 6, 8, 12, 0); s = new DateTime(c.getTime().getTime());
        c.set(2014, 6, 8, 18, 0); e = new DateTime(c.getTime().getTime());
        CompetitionDay tu = new CompetitionDay(s, e);
        
        c.set(2014, 6, 9, 8, 0); s = new DateTime(c.getTime().getTime());
        c.set(2014, 6, 9, 18, 0); e = new DateTime(c.getTime().getTime());
        CompetitionDay w = new CompetitionDay(s, e);
        
        c.set(2014, 6, 10, 8, 0); s = new DateTime(c.getTime().getTime());
        c.set(2014, 6, 10, 18, 0); e = new DateTime(c.getTime().getTime());
        CompetitionDay th = new CompetitionDay(s, e);
        
        c.set(2014, 6, 11, 8, 0); s = new DateTime(c.getTime().getTime());
        c.set(2014, 6, 11, 18, 0); e = new DateTime(c.getTime().getTime());
        CompetitionDay f = new CompetitionDay(s, e);
        
        c.set(2014, 6, 12, 8, 0); s = new DateTime(c.getTime().getTime());
        c.set(2014, 6, 12, 18, 0); e = new DateTime(c.getTime().getTime());
        CompetitionDay sa = new CompetitionDay(s, e);
        
        c.set(2014, 6, 13, 8, 0); s = new DateTime(c.getTime().getTime());
        c.set(2014, 6, 13, 18, 0); e = new DateTime(c.getTime().getTime());
        CompetitionDay su = new CompetitionDay(s, e);
        
        competitionDays = ImmutableList.copyOf(Arrays.asList(tu, w, th, f, sa, su));
        
        for (CompetitionDay day : competitionDays) {
            DateTime timeSlotStart = day.getStartTime();
            int slotDurationMs = Config.TIME_SLOT_DURATION_MIN.get()*60*1000;
            int courseBOffset = Config.TIME_SLOT_DURATION_MIN.get()*60*1000/2;
            while (timeSlotStart.isBefore(day.getEndTime())) {
                schedule.put(new TimeSlot(Course.courseA, new DateTime(timeSlotStart.getMillis()), new DateTime(timeSlotStart.getMillis()+slotDurationMs)), null);
                schedule.put(new TimeSlot(Course.courseB, new DateTime(timeSlotStart.getMillis()+courseBOffset), new DateTime(timeSlotStart.getMillis()+courseBOffset+slotDurationMs)), null);
                timeSlotStart = new DateTime(timeSlotStart.getMillis() + slotDurationMs);
            }
        }
        
        ImmutableMap.Builder<Course, CourseLayout> layoutBuilder = ImmutableMap.builder();
        List<Pinger> courseAPingers = new LinkedList<>();
//        courseAPingers.add(new Pinger(BuoyColor.black, new BuoyPosition(Datum.WGS84, new Latitude(36.80266f), new Longitude(-76.19143f))));
        courseAPingers.add(new Pinger(BuoyColor.blue, new BuoyPosition(Datum.WGS84, new Latitude(36.80281f), new Longitude(-76.19152f))));
//        courseAPingers.add(new Pinger(BuoyColor.green, new BuoyPosition(Datum.WGS84, new Latitude(36.80280f), new Longitude(-76.19130f))));
        courseAPingers.add(new Pinger(BuoyColor.red, new BuoyPosition(Datum.WGS84, new Latitude(36.80275f), new Longitude(-76.19141f))));
        courseAPingers.add(new Pinger(BuoyColor.yellow, new BuoyPosition(Datum.WGS84, new Latitude(36.80273f), new Longitude(-76.19131f))));
        layoutBuilder.put(Course.courseA, new CourseLayout(Course.courseA, ImmutableList.copyOf(courseAPingers), new URL("http://192.168.1.7:5000"), new URL("http://192.168.1.5:4000")));
        List<Pinger> courseBPingers = new LinkedList<>();
        courseBPingers.add(new Pinger(BuoyColor.green, new BuoyPosition(Datum.WGS84, new Latitude(36.80189f), new Longitude(-76.29232f))));
        courseBPingers.add(new Pinger(BuoyColor.black, new BuoyPosition(Datum.WGS84, new Latitude(36.80174f), new Longitude(-76.19233f))));
//        courseBPingers.add(new Pinger(BuoyColor.blue, new BuoyPosition(Datum.WGS84, new Latitude(36.80188f), new Longitude(-76.19229f))));      
        courseBPingers.add(new Pinger(BuoyColor.red, new BuoyPosition(Datum.WGS84, new Latitude(36.80194f), new Longitude(-76.19223f))));
//        courseBPingers.add(new Pinger(BuoyColor.yellow, new BuoyPosition(Datum.WGS84, new Latitude(36.80189f), new Longitude(-76.19230f))));
        layoutBuilder.put(Course.courseB, new CourseLayout(Course.courseB, ImmutableList.copyOf(courseBPingers), new URL("http://192.168.1.8:5000"), new URL("http://192.168.1.6:4000")));
        
        layoutMap = layoutBuilder.build();
        
        teams.addAll(Arrays.asList(
                new TeamCode("AUVSI"),
                new TeamCode("cedar"), 
                new TeamCode("dipo"), 
                new TeamCode("ERAU"), 
                new TeamCode("FAU"), 
                new TeamCode("GIT"), 
                new TeamCode("NCKU"), 
                new TeamCode("ODU"), 
                new TeamCode("UCF"), 
                new TeamCode("UF"), 
                new TeamCode("UTA"), 
                new TeamCode("UM"), 
                new TeamCode("URI"),
                new TeamCode("VU")
        ));
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
     * @param teamInWater the teamInWater to set
     */
    public void setTeamInWater(Course course, TeamCode team) {
        teamInWater.put(course, team);
    }
    
    public TimeSlot findCurrentTimeSlot(Course course) {
        List<TimeSlot> slots = new ArrayList<>(schedule.keySet());
        Collections.sort(slots, new TimeSlotComparator());
        for (TimeSlot timeSlot : slots) {
            if (timeSlot.getCourse().equals(course) && 
                    timeSlot.getStartTime().isBeforeNow() &&
                    timeSlot.getEndTime().isAfterNow()) {
                return timeSlot;
            }
        }
        //TODO remove this
        TimeSlot newSlot = new TimeSlot(course, new DateTime(), new DateTime().plusMinutes(20));
        schedule.put(newSlot, null);
        return newSlot;
    }
    
    public synchronized RunSetup startNewRun(TimeSlot slot, TeamCode teamCode) {
        Preconditions.checkNotNull(slot);
        Preconditions.checkNotNull(teamCode);
        
        if (schedule.containsKey(slot));

        Collection<RunArchiver> previousRuns = results.get(slot);
        RunArchiver lastRun = activeRuns.get(slot.getCourse());
        if (lastRun != null) {
            lastRun.endRun();
        }
        int runCount = (previousRuns != null) ? previousRuns.size() : 0;
        String runId = String.format("%s-%s-%s", slot.getCourse(), dateRunIdFormat.format(slot.getStartTime().toDate()), runCount);
        CourseLayout layout = layoutMap.get(slot.getCourse());
        RunSetup newSetup = RunSetup.generateRandomSetup(layoutMap.get(slot.getCourse()), teamCode, runId);
        RunArchiver newRun = new RunArchiver(newSetup);
        activeRuns.put(slot.getCourse(), newRun);
        results.put(slot, newRun);

        try (Socket s = new Socket(layout.getPingerControlServer().getHost(), layout.getPingerControlServer().getPort()); 
                Writer w = new OutputStreamWriter(s.getOutputStream()); 
                BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            for (int i = 0; i < layout.getPingers().size(); i++) {
                if(newSetup.getActivePinger().equals(layout.getPingers().get(i))) {
                    String pingerActivationMessage = NMEAUtils.formatNMEAmessage(NMEAUtils.formatPingerNMEAmessage(slot.getCourse(), i+1));
                    w.write(pingerActivationMessage);
                    System.out.println(pingerActivationMessage);
                    w.flush();
                    break;
                }
            }
            System.out.println(r.readLine());
            System.out.println(r.readLine());
            System.out.println(r.readLine());
            System.out.println(r.readLine());
            System.out.println(r.readLine());
            System.out.println(r.readLine());
            System.out.println(r.readLine());
            System.out.println(r.readLine());
            System.out.println(r.readLine());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return newSetup;
    }
    
    public static void main(String[] args) throws MalformedURLException {
        Competition c = new Competition();
//        for (CompetitionDay d: c.competitionDays) {
//            System.out.println(d);
//        }
//        System.out.println("\n\n");
//        List<TimeSlot> slots = new ArrayList<>(c.schedule.keySet());
//        Collections.sort(slots, new TimeSlotComparator());
//        for (TimeSlot entry: slots) {
//            System.out.println(entry);
//        }
        List<TimeSlot> slots = new ArrayList<>(c.schedule.keySet());
        
        c.startNewRun(slots.get(0), new TeamCode("AUVSI"));
    };
}
