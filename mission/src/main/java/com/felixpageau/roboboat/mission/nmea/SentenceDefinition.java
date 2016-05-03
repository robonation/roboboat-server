package com.felixpageau.roboboat.mission.nmea;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Sentence definition
 * 
 * @author felixpageau
 */
@ThreadSafe
@Immutable
@ParametersAreNonnullByDefault
public class SentenceDefinition implements Cloneable {
  private final List<Field> fields;
  private final String description;
  private final String sentenceId;
  private final String talkerId;
  private final String example;

  private SentenceDefinition(String talkerId, String sentenceId, String description, List<Field> fields, String example) {
    Preconditions.checkArgument(Preconditions.checkNotNull(talkerId).length() == 2);
    Preconditions.checkArgument(Preconditions.checkNotNull(sentenceId).length() == 3);
    this.talkerId = talkerId;
    this.sentenceId = sentenceId;
    this.description = Preconditions.checkNotNull(description, "description cannot be null");
    this.fields = ImmutableList.copyOf(Preconditions.checkNotNull(fields, "fields cannot be null"));
    this.example = Preconditions.checkNotNull(example, "The example cannot be null");
  }

  public static SentenceDefinition create(String talkerId, String sentenceId, String description, List<Field> fields, String example) {
    SentenceDefinition def = new SentenceDefinition(talkerId, sentenceId, description, fields, example);
    NMEASentence.validateSyntax(example);
    NMEASentence.validateFields(def, example);
    return def;
  }

  @Nonnull
  public String getTalkerId() {
    return talkerId;
  }

  @Nonnull
  public String getSentenceId() {
    return sentenceId;
  }

  @Nonnull
  public String getDescription() {
    return description;
  }

  @Nonnull
  public List<Field> getFields() {
    return fields;
  }

  @Nonnull
  public String getExample() {
    return example;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(sentenceId, talkerId, description, fields);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null) return false;
    if (!(obj instanceof SentenceDefinition)) return false;
    SentenceDefinition other = (SentenceDefinition) obj;
    return Objects.equal(talkerId, other.talkerId) && Objects.equal(sentenceId, other.sentenceId) && Objects.equal(description, other.description)
        && Objects.equal(fields, other.fields);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("talkedId", talkerId).add("sentenceId", sentenceId).add("description", description).add("fields", fields)
        .toString();
  }
}
