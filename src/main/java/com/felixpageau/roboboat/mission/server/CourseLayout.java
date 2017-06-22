package com.felixpageau.roboboat.mission.server;

import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.DockingBay;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class CourseLayout {
  private final Course course;
  private final URL pingerControlServer;
  private final URL sevenSegControlServer;
  private final URL carouselControlServer;
  private final List<DockingBay> dockingBays;

  @JsonCreator
  public CourseLayout(@JsonProperty(value = "course") Course course, 
      @JsonProperty(value = "dockingBays") List<DockingBay> dockingBays, 
      @JsonProperty(value = "pingerControlServer") URL pingerControlServer,
      @JsonProperty(value = "sevenSegControlServer") URL sevenSegControlServer,
      @JsonProperty(value = "carouselControlServer") URL carouselControlServer) {
    this.course = Preconditions.checkNotNull(course);
    this.dockingBays = Preconditions.checkNotNull(dockingBays);
    this.pingerControlServer = Preconditions.checkNotNull(pingerControlServer);
    this.sevenSegControlServer = Preconditions.checkNotNull(sevenSegControlServer);
    this.carouselControlServer = Preconditions.checkNotNull(carouselControlServer);
  }

  /**
   * @return the course
   */
  public Course getCourse() {
    return course;
  }

  public URL getCarouselControlServer() {
    return carouselControlServer;
  }

  public List<DockingBay> getDockingBays() {
    return dockingBays;
  }

  public URL getPingerControlServer() {
    return pingerControlServer;
  }

  public URL getSevenSegControlServer() {
    return sevenSegControlServer;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("course", course).add("dockingBays", dockingBays).add("carouselControlServer", carouselControlServer)
        .add("pingerControlServer", pingerControlServer).add("sevenSegControlServer", sevenSegControlServer).toString();
  }
}
