package com.felixpageau.roboboat.mission2015.server;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class TimeSlot {
    private final Course course;
    private final DateTime startTime;
    private final DateTime endTime;
    
    @JsonCreator
    public TimeSlot(Course course, DateTime startTime, DateTime endTime) {
        this.course = Preconditions.checkNotNull(course);
        this.startTime = Preconditions.checkNotNull(startTime);
        this.endTime = Preconditions.checkNotNull(endTime);
    }

    /**
     * @return the course
     */
    public Course getCourse() {
        return course;
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
        if (!(obj instanceof TimeSlot)) {
            return false;
        }
        TimeSlot other = (TimeSlot) obj;

        return Objects.equal(course, other.course)
                && Objects.equal(startTime, other.startTime)
                && Objects.equal(endTime, other.endTime);
    }
    
    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hashCode(course, startTime, endTime);
    }
    
    @JsonIgnore
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("course", course)
                .add("startTime", Config.DATE_FORMATTER.get().format(startTime.toDate()))
                .add("endTime", Config.DATE_FORMATTER.get().format(endTime.toDate())).toString();
    }
}
