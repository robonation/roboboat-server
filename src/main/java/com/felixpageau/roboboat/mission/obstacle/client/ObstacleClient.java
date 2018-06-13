package com.felixpageau.roboboat.mission.obstacle.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

import com.felixpageau.roboboat.mission.server.CourseLayout;
import com.felixpageau.roboboat.mission.server.RunSetup;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;

/**
 * Interface for clients used to control competition obstacles.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
@ReturnValuesAreNonNullByDefault
public interface ObstacleClient {
  Future<ReportStatus> activate(ExecutorService e, CourseLayout layout, RunSetup setup);
  Future<ReportStatus> turnOff(ExecutorService e, CourseLayout layout, RunSetup setup);
}
