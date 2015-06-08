/**
 * 
 */
package com.felixpageau.roboboat.mission2015;

import java.util.Set;

import org.glassfish.jersey.server.ResourceConfig;

import com.felixpageau.roboboat.mission2015.server.Competition;
import com.felixpageau.roboboat.mission2015.server.CompetitionManager;
import com.google.common.base.Preconditions;

/**
 * Base class for a CompetitionResourceConfig
 */
public abstract class CompetitionResourceConfig extends ResourceConfig {
  private final CompetitionManager competitionManager;

  /**
   * 
   */
  public CompetitionResourceConfig(CompetitionManager competitionManager) {
    this.competitionManager = Preconditions.checkNotNull(competitionManager, "competitionManager cannot be null");
  }

  /**
   * @param classes
   */
  public CompetitionResourceConfig(CompetitionManager competitionManager, Set<Class<?>> classes) {
    super(classes);
    this.competitionManager = Preconditions.checkNotNull(competitionManager, "competitionManager cannot be null");
  }

  /**
   * @param classes
   */
  public CompetitionResourceConfig(CompetitionManager competitionManager, Class<?>... classes) {
    super(classes);
    this.competitionManager = Preconditions.checkNotNull(competitionManager, "competitionManager cannot be null");
  }

  /**
   * @param original
   */
  public CompetitionResourceConfig(CompetitionManager competitionManager, ResourceConfig original) {
    super(original);
    this.competitionManager = Preconditions.checkNotNull(competitionManager, "competitionManager cannot be null");
  }

  public Competition getCompetition() {
    return competitionManager.getCompetition();
  }
}
