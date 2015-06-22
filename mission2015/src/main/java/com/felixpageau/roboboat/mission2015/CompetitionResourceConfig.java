/**
 * 
 */
package com.felixpageau.roboboat.mission2015;

import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

import org.glassfish.jersey.server.ResourceConfig;

import com.felixpageau.roboboat.mission2015.nmea.SentenceRegistry;
import com.felixpageau.roboboat.mission2015.server.CompetitionManager;
import com.google.common.base.Preconditions;

/**
 * Base class for a CompetitionResourceConfig
 */
@ParametersAreNonnullByDefault
@ThreadSafe
public abstract class CompetitionResourceConfig extends ResourceConfig {
  private final CompetitionManager competitionManager;

  /**
   * @param competitionManager the competition manager instance
   */
  public CompetitionResourceConfig(CompetitionManager competitionManager) {
    this.competitionManager = Preconditions.checkNotNull(competitionManager, "competitionManager cannot be null");
  }

  /**
   * @param competitionManager the competition manager instance
   * @param classes the classes to load in jetty's context
   */
  public CompetitionResourceConfig(CompetitionManager competitionManager, Set<Class<?>> classes) {
    super(classes);
    this.competitionManager = Preconditions.checkNotNull(competitionManager, "competitionManager cannot be null");
  }

  /**
   * @param competitionManager the competition manager instance
   * @param classes classes the classes to load in jetty's context
   */
  public CompetitionResourceConfig(CompetitionManager competitionManager, Class<?>... classes) {
    super(classes);
    this.competitionManager = Preconditions.checkNotNull(competitionManager, "competitionManager cannot be null");
  }

  /**
   * @param competitionManager the competition manager instance
   * @param original the parent {@link ResourceConfig}
   */
  public CompetitionResourceConfig(CompetitionManager competitionManager, ResourceConfig original) {
    super(original);
    this.competitionManager = Preconditions.checkNotNull(competitionManager, "competitionManager cannot be null");
  }

  /**
   * @return the {@link CompetitionManager}
   */
  public CompetitionManager getCompetition() {
    return competitionManager;
  }

  /**
   * @return the {@link SentenceRegistry}
   */
  public abstract SentenceRegistry createNMEASentenceRegistry();

  /**
   * @return the {@link NMEAServer}
   */
  public abstract NMEAServer getNMEAServer();
}
