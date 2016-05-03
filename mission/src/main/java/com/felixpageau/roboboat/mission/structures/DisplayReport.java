package com.felixpageau.roboboat.mission.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class DisplayReport {
  public static DisplayReport NO_REPORT_A = new DisplayReport(Course.courseA, null, null, 0, null, null, false, null, false, null, null, false, false, null, null, null);
  public static DisplayReport NO_REPORT_B = new DisplayReport(Course.courseB, null, null, 0, null, null, false, null, false, null, null, false, false, null, null, null);
  public static DisplayReport NO_REPORT_OT = new DisplayReport(Course.openTest, null, null, 0, null, null, false, null, false, null, null, false, false, null, null, null);
  private final Course course;
  private final TeamCode teamCode;
  private final Position currentPosition;
  private final long lastHeartbeat;
  private final Challenge currentChallenge;
  private final GateCode assignedGateCode;
  private final boolean requestedGateCode;
  private final DockingSequence dockingSequence;
  private final boolean requestedDockingSequence;
  private final BuoyColor activePinger;
  private final BuoyColor reportedPinger;
  private final boolean requestedInteropImages;
  private final boolean requestedInteropImage;
  private final String uploadedImage;
  private final Shape activeShape;
  private final Shape reportedShape;

  @JsonCreator
  public DisplayReport(@JsonProperty(value = "course") Course course, @JsonProperty(value = "teamCode") TeamCode teamCode,
      @JsonProperty(value = "currentPosition") Position currentPosition, @JsonProperty(value = "lastHeartbeat") long lastHeartbeat,
      @JsonProperty(value = "currentChallenge") Challenge currentChallenge, @JsonProperty(value = "assignedGateCode") GateCode assignedGateCode,
      @JsonProperty(value = "requestedGateCode") boolean requestedGateCode, @JsonProperty(value = "dockingSequence") DockingSequence dockingSequence,
      @JsonProperty(value = "requestedDockingSequence") boolean requestedDockingSequence, @JsonProperty(value = "activePinger") BuoyColor activePinger,
      @JsonProperty(value = "reportedPinger") BuoyColor reportedPinger, @JsonProperty(value = "requestedInteropImages") boolean requestedInteropImages,
      @JsonProperty(value = "requestedInteropImage") boolean requestedInteropImage, @JsonProperty(value = "uploadedImage") String uploadedImage,
      @JsonProperty(value = "activeShape") Shape activeShape, @JsonProperty(value = "reportedShape") Shape reportedShape) {
    this.course = course;
    this.teamCode = teamCode;
    this.currentPosition = currentPosition;
    this.lastHeartbeat = lastHeartbeat;
    this.currentChallenge = currentChallenge;
    this.assignedGateCode = assignedGateCode;
    this.requestedGateCode = requestedGateCode;
    this.dockingSequence = dockingSequence;
    this.requestedDockingSequence = requestedDockingSequence;
    this.activePinger = activePinger;
    this.reportedPinger = reportedPinger;
    this.requestedInteropImages = requestedInteropImages;
    this.requestedInteropImage = requestedInteropImage;
    this.uploadedImage = uploadedImage;
    this.activeShape = activeShape;
    this.reportedShape = reportedShape;
  }

  /**
   * @return the course
   */
  public Course getCourse() {
    return course;
  }

  /**
   * @return the teamCode
   */
  public TeamCode getTeamCode() {
    return teamCode;
  }

  /**
   * @return the currentPosition
   */
  public Position getCurrentPosition() {
    return currentPosition;
  }

  /**
   * @return the lastHeartbeat
   */
  public long getLastHeartbeat() {
    return lastHeartbeat;
  }

  /**
   * @return the currentChallenge
   */
  public Challenge getCurrentChallenge() {
    return currentChallenge;
  }

  /**
   * @return the assignedGateCode
   */
  public GateCode getAssignedGateCode() {
    return assignedGateCode;
  }

  /**
   * @return the requestedGateCode
   */
  public boolean isRequestedGateCode() {
    return requestedGateCode;
  }

  /**
   * @return the dockingSequence
   */
  public DockingSequence getDockingSequence() {
    return dockingSequence;
  }

  /**
   * @return the requestedDockingSequence
   */
  public boolean isRequestedDockingSequence() {
    return requestedDockingSequence;
  }

  /**
   * @return the activePinger
   */
  public BuoyColor getActivePinger() {
    return activePinger;
  }

  /**
   * @return the reportedPinger
   */
  public BuoyColor getReportedPinger() {
    return reportedPinger;
  }

  /**
   * @return the requestedInteropImages
   */
  public boolean isRequestedInteropImages() {
    return requestedInteropImages;
  }

  /**
   * @return the requestedInteropImage
   */
  public boolean isRequestedInteropImage() {
    return requestedInteropImage;
  }

  /**
   * @return the uploadedImage
   */
  public String getUploadedImage() {
    return uploadedImage;
  }

  /**
   * @return the activeShape
   */
  public Shape getActiveShape() {
    return activeShape;
  }

  /**
   * @return the reportedShape
   */
  public Shape getReportedShape() {
    return reportedShape;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("course", course).add("teamCode", teamCode).add("currentPosition", currentPosition)
        .add("lastHeartbeat", lastHeartbeat).add("currentChallenge", currentChallenge).add("assignedGateCode", assignedGateCode)
        .add("requestedGateCode", requestedGateCode).add("dockingSequence", dockingSequence).add("requestedDockingSequence", requestedDockingSequence)
        .add("activePinger", activePinger).add("reportedPinger", reportedPinger).add("requestedInteropImages", requestedInteropImages)
        .add("requestedInteropImage", requestedInteropImage).add("activeShape", activeShape).add("reportedShape", reportedShape).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(course, teamCode, currentPosition, lastHeartbeat, currentChallenge, assignedGateCode, requestedGateCode, dockingSequence,
        requestedDockingSequence, activePinger, reportedPinger, requestedInteropImages, requestedInteropImage, activeShape, reportedShape);
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
    if (obj instanceof DisplayReport) {
      DisplayReport other = (DisplayReport) obj;
      return Objects.equal(course, other.course) && Objects.equal(teamCode, other.teamCode) && Objects.equal(currentPosition, other.currentPosition)
          && Objects.equal(lastHeartbeat, other.lastHeartbeat) && Objects.equal(currentChallenge, other.currentChallenge)
          && Objects.equal(assignedGateCode, other.assignedGateCode) && Objects.equal(requestedGateCode, other.requestedGateCode)
          && Objects.equal(dockingSequence, other.dockingSequence) && Objects.equal(requestedDockingSequence, other.requestedDockingSequence)
          && Objects.equal(activePinger, other.activePinger) && Objects.equal(reportedPinger, other.reportedPinger)
          && Objects.equal(requestedInteropImages, other.requestedInteropImages) && Objects.equal(requestedInteropImage, other.requestedInteropImage)
          && Objects.equal(activeShape, other.activeShape) && Objects.equal(reportedShape, other.reportedShape);
    }
    return false;
  }
}
