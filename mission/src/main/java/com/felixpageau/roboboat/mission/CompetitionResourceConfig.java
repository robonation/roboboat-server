/**
 * 
 */
package com.felixpageau.roboboat.mission;

import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

import org.glassfish.jersey.server.ResourceConfig;

import com.felixpageau.roboboat.mission.nmea.SentenceRegistry;
import com.felixpageau.roboboat.mission.server.CompetitionManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

/**
 * Base class for a CompetitionResourceConfig
 */
@ParametersAreNonnullByDefault
@ThreadSafe
public abstract class CompetitionResourceConfig extends ResourceConfig {
  private final CompetitionManager competitionManager;

  /**
   * @param competitionManager
   *          the competition manager instance
   * @param classes
   *          classes the classes to load in jetty's context
   */
  public CompetitionResourceConfig(CompetitionManager competitionManager, Class<?>... classes) {
    this(competitionManager, ImmutableSet.of(), classes);
  }

  /**
   * @param competitionManager
   *          the competition manager instance
   * @param original
   *          the parent {@link ResourceConfig}
   */
  public CompetitionResourceConfig(CompetitionManager competitionManager, Set<Object> components, Class<?>... classes) {
    super(classes);
    this.registerInstances(ImmutableSet.copyOf(components));
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
