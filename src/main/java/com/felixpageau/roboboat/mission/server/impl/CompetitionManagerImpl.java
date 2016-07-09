/**
 * 
 */
package com.felixpageau.roboboat.mission.server.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felixpageau.roboboat.mission.WebApplicationExceptionWithContext;
import com.felixpageau.roboboat.mission.server.Competition;
import com.felixpageau.roboboat.mission.server.RunArchiver;
import com.felixpageau.roboboat.mission.server.StructuredEvent;
import com.felixpageau.roboboat.mission.structures.Challenge;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.ImageUploadDescriptor;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.felixpageau.roboboat.mission.structures.TeamCode;
import com.felixpageau.roboboat.mission.structures.Timestamp;
import com.felixpageau.roboboat.mission.structures.UploadStatus;

/**
 * @author felixpageau
 *
 */
@ThreadSafe
@Immutable
@ParametersAreNonnullByDefault
public class CompetitionManagerImpl extends MockCompetitionManager {
  private static final Logger LOG = LoggerFactory.getLogger(CompetitionManagerImpl.class);
  private static final DateTimeFormatter SMS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  private final AmazonSNSClient snsClient;

  public CompetitionManagerImpl(Competition competition, ObjectMapper om) {
    super(competition, om, new File("/etc/roboboat2015-images/uploads/" + DateTimeFormatter.ofPattern("YYYYMMdd/").format(LocalDateTime.now())));

    // create a new SNS client and set endpoint
    snsClient = new AmazonSNSClient(new BasicAWSCredentials("AKIAJ5HBSR3KIJUT32YQ", "4wxHI+VjbhsjVnsDQ3/2YsDKXf2OdxTo6BWXDosQ"));
    snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));
  }

  @Override
  public ReportStatus startRun(Course course, TeamCode teamCode) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive != null) {
      throw new WebApplicationExceptionWithContext(String.format("There is already a run active on course %s! You can't go until team %s get out of %s",
          course, archive.getRunSetup().getActiveTeam(), course), 400);
    }
    ReportStatus status = super.startRun(course, teamCode);
    archive = competition.getActiveRuns().get(course);
    if (archive != null) {
      // publish to an SNS topic
      String topicArn = "arn:aws:sns:us-east-1:976841718827:roboboat-server-new-run";
      String msg = String.format("Team: %s starting run: %s on course: %s at: %s", teamCode, archive.getRunSetup().getRunId(), course, LocalDateTime.now()
          .format(SMS_DATE_FORMAT));
      PublishRequest publishRequest = new PublishRequest(topicArn, msg);
      PublishResult publishResult = snsClient.publish(publishRequest);
      // print MessageId of message published to SNS topic
      System.out.println("MessageId - " + publishResult.getMessageId());
    }

    return status;
  }

  @Override
  public ReportStatus endRun(Course course, TeamCode teamCode) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive != null && !archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationExceptionWithContext(String.format("Another team is already in the water! You can't go until team %s get out of %s", archive
          .getRunSetup().getActiveTeam(), course), 400);
    }
    return super.endRun(course, teamCode);
  }

  @Override
  public UploadStatus uploadInteropImage(Course course, TeamCode teamCode, byte[] content) {
    RunArchiver archive = competition.getActiveRuns().get(course);
    if (archive == null) {
      throw new WebApplicationExceptionWithContext(String.format("You must first start a run! Try doing a POST against /run/start/%s/%s", course, teamCode),
          400);
    }
    if (!archive.getRunSetup().getActiveTeam().equals(teamCode)) {
      throw new WebApplicationExceptionWithContext(String.format("Another team is already in the water! You can't go until team %s get out of %s", teamCode,
          course), 400);
    }

    UUID imageId = UUID.randomUUID();
    try {
      File f = new File(basePath, imageId.toString() + getExtension(content));
      ImageUploadDescriptor iud = new ImageUploadDescriptor(course, teamCode, new Timestamp(), imageId.toString(), f.getName());
      File fDesc = new File(basePath, imageId.toString() + ".desc");
      if (!f.createNewFile()) {
        LOG.warn("Selected UUID already exist: {}", imageId);
        throw new RuntimeException(String.format("Selected UUID already exist: %s", imageId));
      }

      try (OutputStream os = new BufferedOutputStream(new FileOutputStream(f)); OutputStream oDesc = new BufferedOutputStream(new FileOutputStream(fDesc))) {
        os.write(content);
        oDesc.write(om.writeValueAsBytes(iud));
      }
    } catch (IOException e) {
      LOG.warn("IOExcepton while uploading an image for team {} on course {}", teamCode, course, e);
    }

    archive.addEvent(new StructuredEvent(course, teamCode, Challenge.interop, String.format("uploaded image (%s)", imageId)));
    archive.uploadedImage(imageId.toString());
    return new UploadStatus(imageId.toString());
  }
}
