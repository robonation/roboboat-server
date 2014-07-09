package com.felixpageau.roboboat.mission2014.server;

import java.text.SimpleDateFormat;

import jersey.repackaged.com.google.common.base.Objects;
import jersey.repackaged.com.google.common.base.Preconditions;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CompetitionDay {
    private final DateTime startTime;
    private final DateTime endTime;
    
    @JsonCreator
    public CompetitionDay(
            @JsonProperty(value = "startTime") DateTime startTime, 
            @JsonProperty(value = "endTime") DateTime endTime) {
        this.startTime = Preconditions.checkNotNull(startTime);
        this.endTime = Preconditions.checkNotNull(endTime);
    }
    
    @JsonIgnore
    public String getDay() {
        return new SimpleDateFormat("EEE").format(startTime);
    }

    /**
     * @return the startTime
     */
    public DateTime getStartTime() {
        return startTime;
    }

    /**
     * @return the endTime
     */
    public DateTime getEndTime() {
        return endTime;
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
        if (!(obj instanceof CompetitionDay)) {
            return false;
        }
        CompetitionDay other = (CompetitionDay) obj;

        return Objects.equal(startTime, other.startTime)
                && Objects.equal(endTime, other.endTime);
    }
    
    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hashCode(startTime, endTime);
    }
    
    @JsonIgnore
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("day", getDay())
                .add("startTime", Config.DATE_FORMATTER.get().format(startTime.toDate()))
                .add("endTime", Config.DATE_FORMATTER.get().format(endTime.toDate())).toString();
    }
}
