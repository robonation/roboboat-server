package com.felixpageau.roboboat.mission2015.structures;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class TeamCode {
  private static final Pattern TEAM_CODE_REGEX = Pattern.compile("[a-zA-Z]{2,5}");
  private final String value;

  @JsonCreator
  public TeamCode(String value) {
    this.value = Preconditions.checkNotNull(value);
    Preconditions.checkArgument(TEAM_CODE_REGEX.matcher(value).matches(),
        String.format("The provided team code %s does not match regex %s", value, TEAM_CODE_REGEX.pattern()));
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonIgnore
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null) return false;
    if (!(obj instanceof TeamCode)) return false;
    TeamCode other = (TeamCode) obj;
    return Objects.equal(other.getValue(), value);
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @JsonIgnore
  @Override
  public String toString() {
    return value;
  }
}
