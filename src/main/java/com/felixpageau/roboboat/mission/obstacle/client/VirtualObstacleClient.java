/**
 * 
 */
package com.felixpageau.roboboat.mission.obstacle.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

import com.felixpageau.roboboat.mission.server.CourseLayout;
import com.felixpageau.roboboat.mission.server.RunSetup;
import com.felixpageau.roboboat.mission.structures.ReportStatus;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;

/**
 *  This @link {@link ObstacleClient} is used for 'virtual' obstacles since they don't need to be activated/turned off
 */
@ThreadSafe
@ParametersAreNonnullByDefault
@ReturnValuesAreNonNullByDefault
public class VirtualObstacleClient implements ObstacleClient {
  @Override
  public Future<ReportStatus> activate(ExecutorService e, CourseLayout layout, RunSetup setup) {
    return CompletableFuture.completedFuture(new ReportStatus(true, null));
  }

  @Override
  public Future<ReportStatus> turnOff(ExecutorService e, CourseLayout layout, RunSetup setup) {
    return CompletableFuture.completedFuture(new ReportStatus(true, null));
  }
}