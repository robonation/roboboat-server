package com.felixpageau.roboboat.mission2015.server;

import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.Pinger;
import com.google.common.base.Preconditions;

public class CourseLayout {
    private final Course course;
    private final URL lightControlServer;
    private final URL pingerControlServer;
    private final List<Pinger> pingers;
    
    @JsonCreator
    public CourseLayout(
            @JsonProperty(value = "course") Course course, 
            @JsonProperty(value = "pingers") List<Pinger> pingers,
            @JsonProperty(value = "lightControlServer") URL lightControlServer,
            @JsonProperty(value = "pingerControlServer") URL pingerControlServer) {
        this.course = Preconditions.checkNotNull(course);
        this.pingers = Preconditions.checkNotNull(pingers);
        this.lightControlServer = Preconditions.checkNotNull(lightControlServer);
        this.pingerControlServer = Preconditions.checkNotNull(pingerControlServer);
    }
    
    /**
     * @return the course
     */
    public Course getCourse() {
        return course;
    }

    /**
     * @return the buoyPositions
     */
    public List<Pinger> getPingers() {
        return pingers;
    }
    
    public URL getLightControlServer() {
        return lightControlServer;
    }
    
    public URL getPingerControlServer() {
        return pingerControlServer;
    }
}
