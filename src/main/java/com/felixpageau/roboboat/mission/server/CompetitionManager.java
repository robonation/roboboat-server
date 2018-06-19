package com.felixpageau.roboboat.mission.server;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import javax.ws.rs.core.Response;

import com.felixpageau.roboboat.mission.WebApplicationExceptionWithContext;
import com.felixpageau.roboboat.mission.structures.CarouselStatus;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.DisplayStatus;
import com.felixpageau.roboboat.mission.structures.DockingSequence;
import com.felixpageau.roboboat.mission.structures.HeartbeatReport;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.felixpageau.roboboat.mission.structures.UploadStatus;
import com.google.common.base.Preconditions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@ThreadSafe
@Immutable
@ParametersAreNonnullByDefault
public interface CompetitionManager {
  @Nonnull
  ReportStatus startRun(Course course, TeamCode teamCode);

  @Nonnull
  ReportStatus endRun(Course course, TeamCode teamCode);

  @Nonnull
  DockingSequence getDockingSequence(Course course, TeamCode teamCode);

  @Nonnull
  CarouselStatus getLeaderSequence(Course course, TeamCode teamCode);

  @Nonnull
  Optional<byte[]> getUploadedImage(String imageId);

  @Nonnull
  UploadStatus uploadDockingImage(Course course, TeamCode teamCode, byte[] content);

  @Nonnull
  ReportStatus reportHeartbeat(Course course, TeamCode teamCode, HeartbeatReport report);

  @Nonnull
  DisplayStatus getDisplayStatus(List<Course> courses);

  @Nonnull
  Competition getCompetition();

  @SuppressFBWarnings(value = "WEM_WEAK_EXCEPTION_MESSAGING")
  default public String getExtension(byte[] content) {
    byte[] jpg = new byte[] { (byte) 0xFF, (byte) 0xD8 };
    byte[] png = new byte[] { (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47 };
    Preconditions.checkNotNull(content, "content cannot be null");
    if (content.length > jpg.length) {
      boolean valid = true;
      for (int i = 0; i < jpg.length && valid; i++) {
        valid &= content[i] == jpg[i];
      }
      if (valid) return ".jpg";
    }
    if (content.length > png.length) {
      boolean valid = true;
      for (int i = 0; i < png.length && valid; i++) {
        valid &= content[i] == png[i];
      }
      if (valid) return ".png";
    }

    throw new WebApplicationExceptionWithContext("image uploaded is neither jpg or png", Response.Status.BAD_REQUEST);
  }
}
