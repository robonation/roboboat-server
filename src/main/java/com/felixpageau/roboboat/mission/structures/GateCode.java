package com.felixpageau.roboboat.mission.structures;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Defines a GateCode to use in navigation tasks
 */
@ReturnValuesAreNonNullByDefault
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public class GateCode {
  private static final Pattern CODE_REGEX = Pattern.compile("^\\(([1-3]{1}),([X-Z]{1})\\)$");
  private final int entrance;
  private final String exit;

  @JsonCreator
  public GateCode(@JsonProperty(value = "gateCode") String code) {
    Preconditions.checkNotNull(code);
    Matcher m = CODE_REGEX.matcher(code);
    Preconditions.checkArgument(m.matches());
    m.reset();
    Preconditions.checkArgument(m.find());
    this.entrance = Integer.parseInt(m.group(1));
    this.exit = m.group(2);
  }

  public GateCode(int entrance, String exit) {
    this(String.format("(%d,%s)", entrance, Preconditions.checkNotNull(exit)));
  }

  public static GateCode generateRandomGateCode() {
    
    int entrance = new SecureRandom().nextInt(3)+1;
    String exit = Character.toString((char) (new SecureRandom().nextInt(3)+1 + 87));
    return new GateCode(entrance, exit);
  }

  @JsonIgnore
  public int getEntrance() {
    return entrance;
  }

  @JsonIgnore
  public String getExit() {
    return exit;
  }

  public String getGateCode() {
    return String.format("(%d,%s)", entrance, exit);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("code", getGateCode()).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(getGateCode());
  }

  @JsonIgnore
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj instanceof GateCode) {
      GateCode other = (GateCode) obj;
      return Objects.equal(getGateCode(), other.getGateCode());
    }
    return false;
  }
}
