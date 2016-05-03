package com.felixpageau.roboboat.mission.structures;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;

public class DisplayStatus {
  public Map<Course, DisplayReport> reports;

  @JsonCreator
  public DisplayStatus(@JsonProperty("reports") Map<Course, DisplayReport> reports) {
    this.reports = Preconditions.checkNotNull(reports, "Reports cannot be null");
  }

  @JsonValue
  public Map<Course, DisplayReport> getReports() {
    return reports;
  }
}
