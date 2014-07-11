package com.felixpageau.roboboat.mission2014.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission2014.structures.BuoyColor;
import com.felixpageau.roboboat.mission2014.structures.BuoyPosition;
import com.felixpageau.roboboat.mission2014.structures.Course;
import com.felixpageau.roboboat.mission2014.structures.Datum;
import com.felixpageau.roboboat.mission2014.structures.DockingBay;
import com.felixpageau.roboboat.mission2014.structures.GateCode;
import com.felixpageau.roboboat.mission2014.structures.Latitude;
import com.felixpageau.roboboat.mission2014.structures.LightSequence;
import com.felixpageau.roboboat.mission2014.structures.Longitude;
import com.felixpageau.roboboat.mission2014.structures.TeamCode;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class RunSetup {
    private final String runId;
    private final TeamCode activeTeam;
    private final Course course;
    private final GateCode activeGateCode;
    private final DockingBay activeDockingBay;
    private final Pinger activePinger;
    private final LightSequence activeLightSequence;
    
    @JsonCreator
    public RunSetup(
            @JsonProperty(value = "runId") String runId,
            @JsonProperty(value = "course") Course course, 
            @JsonProperty(value = "activeTeam") TeamCode activeTeam, 
            @JsonProperty(value = "activeGateCode") GateCode activeGateCode, 
            @JsonProperty(value = "activeDockingBay") DockingBay activeDockingBay, 
            @JsonProperty(value = "activePinger") Pinger activePinger, 
            @JsonProperty(value = "activeLightSequence") LightSequence activeLightSequence) {
        this.runId = Preconditions.checkNotNull(runId);
        this.course = Preconditions.checkNotNull(course);
        this.activeTeam = Preconditions.checkNotNull(activeTeam);
        this.activeGateCode = Preconditions.checkNotNull(activeGateCode);
        this.activeDockingBay = Preconditions.checkNotNull(activeDockingBay);
        this.activePinger = Preconditions.checkNotNull(activePinger);
        this.activeLightSequence = Preconditions.checkNotNull(activeLightSequence);
    }
    
    public static RunSetup generateRandomSetup(CourseLayout courseLayout, TeamCode teamCode, String runId) {
        List<Pinger> pingers = courseLayout.getPingers();
        Pinger activePinger = new ArrayList<Pinger>(pingers).get(Math.round((float)Math.random()*pingers.size()-0.5f));

        return new RunSetup(runId, courseLayout.getCourse(), teamCode, GateCode.generateRandomGateCode(), DockingBay.generateRandomDockingBay(), activePinger, LightSequence.generateRandomLightSequence());
    }

    /**
     * @return the activeTeam
     */
    public TeamCode getActiveTeam() {
        return activeTeam;
    }

    /**
     * @return the course
     */
    public Course getCourse() {
        return course;
    }

    /**
     * @return the activeGateCode
     */
    public GateCode getActiveGateCode() {
        return activeGateCode;
    }

    /**
     * @return the activeDockingBay
     */
    public DockingBay getActiveDockingBay() {
        return activeDockingBay;
    }

    /**
     * @return the activePinger
     */
    public Pinger getActivePinger() {
        return activePinger;
    }

    /**
     * @return the activeLightSequence
     */
    public LightSequence getActiveLightSequence() {
        return activeLightSequence;
    }
    
    @JsonIgnore
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RunSetup)) {
            return false;
        }
        RunSetup other = (RunSetup) obj;

        return Objects.equal(runId, other.runId)
                && Objects.equal(activeTeam, other.activeTeam)
                && Objects.equal(course, other.course)
                && Objects.equal(activeGateCode, other.activeGateCode)
                && Objects.equal(activeDockingBay, other.activeDockingBay)
                && Objects.equal(activePinger, other.activePinger)
                && Objects.equal(activeLightSequence, other.activeLightSequence);
    }
    
    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hashCode(runId, activeTeam, course, activeGateCode, activeDockingBay, activePinger, activeLightSequence);
    }
    
    @JsonIgnore
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("runId", runId)
                .add("course", course)
                .add("activeTeam", activeTeam)
                .add("activeGateCode", activeGateCode)
                .add("activeDockingBay", activeDockingBay)
                .add("activePinger", activePinger)
                .add("activeLightSequence", activeLightSequence).toString();
    }
    
    public static void main(String[] args) throws MalformedURLException {
        Pinger black = new Pinger(BuoyColor.black, new BuoyPosition(Datum.WGS84, new Latitude(40.689247f), new Longitude(-70.044500f)));
        Pinger red = new Pinger(BuoyColor.red, new BuoyPosition(Datum.WGS84, new Latitude(41.689247f), new Longitude(-71.044500f)));
        Pinger green = new Pinger(BuoyColor.green, new BuoyPosition(Datum.WGS84, new Latitude(42.689247f), new Longitude(-72.044500f)));
        Pinger blue = new Pinger(BuoyColor.blue, new BuoyPosition(Datum.WGS84, new Latitude(43.689247f), new Longitude(-73.044500f)));
        Pinger yellow = new Pinger(BuoyColor.yellow, new BuoyPosition(Datum.WGS84, new Latitude(44.689247f), new Longitude(-74.044500f)));
        CourseLayout layout = new CourseLayout(Course.courseA, ImmutableList.copyOf(Arrays.asList(black, red, green, blue, yellow)), new URL("http://127.0.0.1:5000"), new URL("http://192.168.1.5:4000"));
        
        for (int i = 0; i < 100; i++) {
            System.out.println(generateRandomSetup(layout, new TeamCode("AUVSI"), "runId-1"));
        }
    }
}
