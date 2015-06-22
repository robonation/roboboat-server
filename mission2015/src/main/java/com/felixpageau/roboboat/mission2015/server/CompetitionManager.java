package com.felixpageau.roboboat.mission2015.server;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.felixpageau.roboboat.mission2015.structures.BeaconReport;
import com.felixpageau.roboboat.mission2015.structures.Course;
import com.felixpageau.roboboat.mission2015.structures.DockingSequence;
import com.felixpageau.roboboat.mission2015.structures.GateCode;
import com.felixpageau.roboboat.mission2015.structures.HeartbeatReport;
import com.felixpageau.roboboat.mission2015.structures.InteropReport;
import com.felixpageau.roboboat.mission2015.structures.ReportStatus;
import com.felixpageau.roboboat.mission2015.structures.TeamCode;
import com.felixpageau.roboboat.mission2015.structures.UploadStatus;
import com.google.common.base.Preconditions;

@ThreadSafe
@Immutable
@ParametersAreNonnullByDefault
public interface CompetitionManager {
  @Nonnull
  ReportStatus startRun(Course course, TeamCode teamCode);

  @Nonnull
  ReportStatus endRun(Course course, TeamCode teamCode);

  @Nonnull
  GateCode getObstacleCourseCode(Course course, TeamCode teamCode);

  @Nonnull
  DockingSequence getDockingSequence(Course course, TeamCode teamCode);

  @Nonnull
  ReportStatus reportPinger(Course course, TeamCode teamCode, BeaconReport payload);

  @Nonnull
  ReportStatus reportInterop(Course course, TeamCode teamCode, InteropReport report);

  @Nonnull
  List<String> listInteropImages(Course course, TeamCode teamCode);

  @Nonnull
  Optional<byte[]> getInteropImage(Course course, TeamCode teamCode, String filename);

  @Nonnull
  UploadStatus uploadInteropImage(Course course, TeamCode teamCode, byte[] content);

  @Nonnull
  ReportStatus reportHeartbeat(Course course, TeamCode teamCode, HeartbeatReport report);

  @Nonnull
  Competition getCompetition();

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

    throw new WebApplicationException("image uploaded is neither jpg or png", Response.Status.BAD_REQUEST);
  }
}
