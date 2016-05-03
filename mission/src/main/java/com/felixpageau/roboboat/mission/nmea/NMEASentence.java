/**
 * 
 */
package com.felixpageau.roboboat.mission.nmea;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import net.sf.marineapi.nmea.parser.SentenceParser;
import net.sf.marineapi.nmea.sentence.Checksum;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * @author felixpageau
 *
 */
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public class NMEASentence {
  public static final char ALTERNATIVE_BEGIN_CHAR = '!';
  public static final char BEGIN_CHAR = '$';
  public static final char CHECKSUM_DELIMITER = '*';
  public static final char FIELD_DELIMITER = ',';
  public static final int MAX_LENGTH = 82;
  public static final java.lang.String TERMINATOR = "\r\n";

  private final String sentenceId;
  private final String talkerId;
  private final SentenceDefinition definition;
  private final List<String> fields;

  public NMEASentence(String sentenceId, String talkerId, SentenceDefinition definition, List<String> fields) {
    this.sentenceId = Preconditions.checkNotNull(sentenceId, "sentenceId cannot be null");
    this.talkerId = Preconditions.checkNotNull(talkerId, "talkerId cannot be null");
    this.definition = Preconditions.checkNotNull(definition, "definition cannot be null");
    this.fields = ImmutableList.copyOf(Preconditions.checkNotNull(fields, "fields cannot be null"));
  }

  @Nonnull
  public static NMEASentence parse(SentenceRegistry registry, String nmea) {
    Preconditions.checkNotNull(registry, "registry cannot be null");
    validateSyntax(nmea);

    String talkerId = nmea.substring(1, 3);
    String sentenceId = nmea.substring(3, 6);
    SentenceDefinition definition = registry.getDefinition(talkerId, sentenceId);
    if (definition == null) {
      throw new IllegalArgumentException(String.format("Invalid data (unknown talker + sentenceId '%s'. Valid ones are: %s): %s", talkerId + sentenceId,
          registry.listDefinitions().stream().map(def -> def.getTalkerId() + def.getSentenceId()).collect(Collectors.joining(", ")), nmea));
    }
    validateFields(definition, nmea);
    String[] values = nmea.substring(nmea.indexOf(Sentence.FIELD_DELIMITER) + 1, findChecksum(nmea)).split(String.valueOf(FIELD_DELIMITER), -1);
    return new NMEASentence(sentenceId, talkerId, definition, ImmutableList.copyOf(values));
  }

  public static boolean validateSyntax(String nmea) {
    if (!SentenceValidator.isValid(nmea)) {
      if (nmea.charAt(0) != BEGIN_CHAR) {
        throw new IllegalArgumentException(String.format("Invalid data (invalid initial character: %c but valid is $c): %s", nmea.charAt(0), BEGIN_CHAR, nmea));
      } else if (!SentenceValidator.isSentence(nmea)) {
        throw new IllegalArgumentException(String.format("Invalid data (not properly formatted NMEA sentence): %s", nmea));
      } else if (!nmea.contains(Character.toString(CHECKSUM_DELIMITER)) || nmea.indexOf(CHECKSUM_DELIMITER) != nmea.lastIndexOf(CHECKSUM_DELIMITER)) {
        throw new IllegalArgumentException(String.format("Invalid data (no checksum separator present '*'): %s", nmea));
      } else {
        String actual = nmea.substring(nmea.length() - 2);
        String expected = Checksum.calculate(nmea);
        if (!expected.equalsIgnoreCase(actual)) {
          throw new IllegalArgumentException(
              String.format("Invalid data (invalid checksum. Received '%s' but correct one is '%s'): %s", actual, expected, nmea));
        }
        throw new IllegalArgumentException(String.format("Invalid data (unidentified problem): %s", actual, expected, nmea));
      }
    }
    return true;
  }

  public static boolean validateFields(SentenceDefinition def, String nmea) {
    Preconditions.checkNotNull(def, "The provided SentenceDefinition cannot be null");
    Preconditions.checkNotNull(def, "The provided nmea string cannot be null");
    try {
      String[] values = nmea.substring(nmea.indexOf(Sentence.FIELD_DELIMITER) + 1, findChecksum(nmea)).split(String.valueOf(FIELD_DELIMITER), -1);
      List<Field> fields = def.getFields();
      Preconditions.checkArgument(fields.size() == values.length,
          String.format("%s should have %d fields but %d were received", def.getDescription(), fields.size(), values.length));
      for (int i = 0; i < fields.size(); i++) {
        Field f = fields.get(i);
        if (!f.isValid(values[i])) {
          throw new IllegalArgumentException(String.format("Value of field %s did not meet validation criteria: %s", f.name(), f.validityCondition()));
        }
      }
      return true;
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e.getMessage()
          + String.format("\t Your message was: '%s' but a valid one would look like: '%s'", nmea, def.getExample()), e.getCause());
    }
  }

  public List<String> getFields() {
    return fields;
  }

  @Nonnull
  public String getSentenceId() {
    return sentenceId;
  }

  public int getFieldCount() {
    return definition.getFields().size();
  }

  @Nonnull
  public String getTalkerId() {
    return talkerId;
  }

  public char getBeginChar() {
    return BEGIN_CHAR;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(sentenceId, talkerId, definition, fields);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null) return false;
    if (!(obj instanceof NMEASentence)) return false;
    NMEASentence other = (NMEASentence) obj;
    return Objects.equal(talkerId, other.talkerId) && Objects.equal(sentenceId, other.sentenceId) && Objects.equal(definition, other.definition)
        && Objects.equal(fields, other.fields);
  }

  @Override
  @Nonnull
  public String toString() {
    StringBuilder sbuild = new StringBuilder();
    sbuild.append(getBeginChar()).append(talkerId).append(sentenceId);
    for (String value : fields) {
      sbuild.append(FIELD_DELIMITER).append(value);
    }
    sbuild.append(SentenceParser.CHECKSUM_DELIMITER);
    sbuild.append(Checksum.calculate(sbuild.toString()));
    return sbuild.toString();
  }

  @Nonnull
  public String toString(int maxLength) {
    String sentence = toString();
    if (sentence.length() > maxLength) {
      throw new IllegalArgumentException("Sentence is too long");
    }
    return sentence;
  }

  /**
   * Find the index of the checksum separator or returns -1
   * 
   * @param nmea
   *          the sentence in which to look for a checksum
   * @return index of the checksum or -1
   */
  public static int findChecksum(String nmea) {
    Preconditions.checkNotNull(nmea, "The provided nmea sentence cannot be null");
    if (nmea.contains(Character.toString(Sentence.CHECKSUM_DELIMITER))) {
      return nmea.indexOf(Sentence.CHECKSUM_DELIMITER);
    }
    return -1;
  }
}
