/**
 * 
 */
package com.felixpageau.roboboat.mission.nmea;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.felixpageau.roboboat.mission.App;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

/**
 * @author felixpageau
 */
@Immutable
@ThreadSafe
@ParametersAreNonnullByDefault
public class SentenceRegistry {
  private final Map<String, SentenceDefinition> definitions;

  public SentenceRegistry(List<SentenceDefinition> definitions) {
    Preconditions.checkNotNull(definitions, "definition cannot be null");
    this.definitions = ImmutableMap.copyOf(definitions.stream().collect(Collectors.toMap((SentenceDefinition def) -> {
      Preconditions.checkArgument(def.getTalkerId().equals(def.getTalkerId().toUpperCase()), "TalkerId must be uppercase");
      Preconditions.checkArgument(def.getSentenceId().equals(def.getSentenceId().toUpperCase()), "SentenceId must be uppercase");
      return def.getTalkerId() + def.getSentenceId();
    }, def -> def)));
  }

  /**
   * Find the {@link SentenceDefinition}
   * 
   * @param sentenceId
   * @return
   */
  @CheckForNull
  public SentenceDefinition getDefinition(String talkerId, String sentenceId) {
    Preconditions.checkNotNull(sentenceId, "sentenceId cannot be null");
    return definitions.get(talkerId.toUpperCase(App.APP_LOCALE) + sentenceId.toUpperCase(App.APP_LOCALE));
  }

  @Nonnull
  public Collection<SentenceDefinition> listDefinitions() {
    return definitions.values();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(SentenceRegistry.class).add("definitions", definitions).toString();
  }
}
