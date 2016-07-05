package com.felixpageau.roboboat.mission.server;

import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.DockingBay;
import com.felixpageau.roboboat.mission.structures.Pinger;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class CourseLayout {
  private final Course course;
  private final URL pingerControlServer;
  private final URL lcdControlServer;
  private final List<Pinger> pingers;
  private final List<DockingBay> dockingBays;

  @JsonCreator
  public CourseLayout(@JsonProperty(value = "course") Course course, @JsonProperty(value = "pingers") List<Pinger> pingers,
      @JsonProperty(value = "dockingBays") List<DockingBay> dockingBays, @JsonProperty(value = "pingerControlServer") URL pingerControlServer,
      @JsonProperty(value = "lcdControlServer") URL lcdControlServer) {
    this.course = Preconditions.checkNotNull(course);
    this.pingers = Preconditions.checkNotNull(pingers);
    this.dockingBays = Preconditions.checkNotNull(dockingBays);
    this.pingerControlServer = Preconditions.checkNotNull(pingerControlServer);
    this.lcdControlServer = Preconditions.checkNotNull(lcdControlServer);
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

  public List<DockingBay> getDockingBays() {
    return dockingBays;
  }

  public URL getPingerControlServer() {
    return pingerControlServer;
  }

  public URL getLcdControlServer() {
    return lcdControlServer;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("course", course).add("dockingBays", dockingBays).add("pingers", pingers)
        .add("pingerControlServer", pingerControlServer).add("lcdControlServer", lcdControlServer).toString();
  }
}
