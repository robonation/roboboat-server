package com.felixpageau.roboboat.mission2014.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class BeaconReport {
    private final Course course;
    private final TeamCode team;
    private final BuoyColor buoyColor;
    private final BuoyPosition buoyPosition;
    
    @JsonCreator
    public BeaconReport(
            @JsonProperty(value = "course") Course course, 
            @JsonProperty(value = "team") TeamCode team, 
            @JsonProperty(value = "buoyColor") BuoyColor buoyColor, 
            @JsonProperty(value = "buoyPosition") BuoyPosition buoyPosition) {
        this.course = Preconditions.checkNotNull(course);
        this.team = Preconditions.checkNotNull(team);
        this.buoyColor = Preconditions.checkNotNull(buoyColor);
        this.buoyPosition = Preconditions.checkNotNull(buoyPosition);
    }
    
    public Course getCourse() {
        return course;
    }
    
    public TeamCode getTeam() {
        return team;
    }
    
    public BuoyColor getBuoyColor() {
        return buoyColor;
    }
    
    public BuoyPosition getBuoyPosition() {
        return buoyPosition;
    }
    
    @JsonIgnore
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("course", course)
                .add("team",team)
                .add("buoyColor",buoyColor)
                .add("buoyPosition", buoyPosition).toString();
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hashCode(course, team, buoyColor, buoyPosition);
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
        if (obj instanceof BeaconReport) {
            BeaconReport other = (BeaconReport) obj;
            return Objects.equal(course, other.course)
                    && Objects.equal(team, other.team)
                    && Objects.equal(buoyColor, other.buoyColor)
                    && Objects.equal(buoyPosition, other.buoyPosition);
        }
        return false;
    }
}
