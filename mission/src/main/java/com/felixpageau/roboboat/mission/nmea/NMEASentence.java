/**
 * 
 */
package com.felixpageau.roboboat.mission.nmea;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author felixpageau
 *
 */
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
@SuppressFBWarnings(value = "CD_CIRCULAR_DEPENDENCY", justification = "")
public class NMEASentence {
  private static final Pattern NMEA_SENTENCE = Pattern.compile("^[\\$!][A-Z0-9]{3,10}[,][a-zA-Z0-9,\\.\\-]{1,77}\\*[A-F0-9]{2}[\r\n]?$");
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
    String[] values = nmea.substring(nmea.indexOf(FIELD_DELIMITER) + 1, findChecksum(nmea)).split(String.valueOf(FIELD_DELIMITER), -1);
    return new NMEASentence(sentenceId, talkerId, definition, ImmutableList.copyOf(values));
  }

  public static boolean validateSyntax(@Nullable String nmea) {
    if (nmea == null) {
      return false;
    }
    if (!NMEA_SENTENCE.matcher(nmea).matches()) {
      if (nmea.charAt(0) != BEGIN_CHAR) {
        throw new IllegalArgumentException(String.format("Invalid data (invalid initial character: %c but valid is %c): %s", nmea.charAt(0), BEGIN_CHAR, nmea));
      } else if (!nmea.contains(Character.toString(CHECKSUM_DELIMITER)) || nmea.indexOf(CHECKSUM_DELIMITER) != nmea.lastIndexOf(CHECKSUM_DELIMITER)) {
        throw new IllegalArgumentException(String.format("Invalid data (no checksum separator present '*'): %s", nmea));
      } else {
        String actual = nmea.substring(nmea.length() - 2);
        String expected = checksum(nmea);
        if (!expected.equalsIgnoreCase(actual)) {
          throw new IllegalArgumentException(
              String.format("Invalid data (invalid checksum. Received '%s' but correct one is '%s'): %s", actual, expected, nmea));
        }
        throw new IllegalArgumentException(String.format("Invalid data (unidentified problem): %s", nmea));
      }
    } else if (!checksum(nmea).equals(nmea.substring(nmea.indexOf(CHECKSUM_DELIMITER) + 1, nmea.indexOf(CHECKSUM_DELIMITER) + 3))) {
      throw new IllegalArgumentException(String.format("Invalid checksum. Received %s but proper checksum is %s", nmea, checksum(nmea)));
    }
    return true;
  }

  public static boolean validateFields(SentenceDefinition def, String nmea) {
    Preconditions.checkNotNull(def, "The provided SentenceDefinition cannot be null");
    Preconditions.checkNotNull(def, "The provided nmea string cannot be null");
    try {
      String[] values = nmea.substring(nmea.indexOf(FIELD_DELIMITER) + 1, findChecksum(nmea)).split(String.valueOf(FIELD_DELIMITER), -1);
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

  @Nonnull
  public List<String> getFields() {
    return fields;
  }

  public String getField(int index) {
    Preconditions.checkArgument(index < 0, String.format("index (%d) is negative", index));
    Preconditions.checkArgument(index >= fields.size(), String.format("index (%d) is greater than fields size (%d)", index, fields.size()));
    return fields.get(index);
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
    sbuild.append(CHECKSUM_DELIMITER);
    sbuild.append(checksum(sbuild.toString()));
    return sbuild.toString();
  }

  @SuppressFBWarnings(value = "WEM_WEAK_EXCEPTION_MESSAGING")
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
    if (nmea.contains(Character.toString(CHECKSUM_DELIMITER))) {
      return nmea.indexOf(CHECKSUM_DELIMITER);
    }
    return -1;
  }

  /**
   * Calculates the checksum of sentence String. Checksum is a XOR of each
   * character between, but not including, the $ and * characters. The resulting
   * hex value is returned as a String in two digit format, padded with a
   * leading zero if necessary. The method will calculate the checksum for any
   * given String and the sentence validity is not checked.
   * 
   * @param nmea
   *          NMEA Sentence with or without checksum.
   * @return Checksum hex value, padded with leading zero if necessary.
   */
  public static String checksum(String nmea) {
    Preconditions.checkNotNull(nmea, "nmea cannot be null");
    if (nmea.startsWith(Character.toString(BEGIN_CHAR)) || nmea.startsWith(Character.toString(ALTERNATIVE_BEGIN_CHAR))) {
      nmea = nmea.substring(1);
    }
    if (nmea.contains(Character.toString(CHECKSUM_DELIMITER))) {
      nmea = nmea.substring(0, nmea.indexOf(CHECKSUM_DELIMITER));
    }
    return String.format("%02X", nmea.chars().map(x -> (byte) x).collect(XorCollector::new, XorCollector::accept, XorCollector::combiner).getValue());
  }

  /**
   * Collects int values by xor-ing them
   */
  public static final class XorCollector implements IntConsumer {
    private int checksum = 0;

    @Override
    public void accept(int value) {
      checksum ^= value;
    }

    public void combiner(XorCollector other) {
      checksum ^= other.checksum;
    }

    public int getValue() {
      return checksum;
    }
  }
}
