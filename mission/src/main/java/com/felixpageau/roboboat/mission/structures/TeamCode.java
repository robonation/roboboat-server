package com.felixpageau.roboboat.mission.structures;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.felixpageau.roboboat.mission.utils.GuavaCollectors;
import com.felixpageau.roboboat.mission.utils.ReturnValuesAreNonNullByDefault;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

@ReturnValuesAreNonNullByDefault
@ParametersAreNonnullByDefault
@ThreadSafe
@Immutable
public class TeamCode {
  private static final Pattern TEAM_CODE_REGEX = Pattern.compile("[a-zA-Z]{2,5}");
  public static final TeamCode DEFAULT = new TeamCode("AUVSI");
  private final String value;

  @JsonCreator
  public TeamCode(String value) {
    this.value = Preconditions.checkNotNull(value);
    Preconditions.checkArgument(TEAM_CODE_REGEX.matcher(value).matches(),
        String.format("The provided team code %s does not match regex %s", value, TEAM_CODE_REGEX.pattern()));
  }

  @JsonIgnore
  public static TeamCode of(String names) {
    Preconditions.checkNotNull(names, "names cannot be null");
    return Iterables.getFirst(of(new String[] { names }), DEFAULT);
  }

  @JsonIgnore
  public static List<TeamCode> of(String... names) {
    Preconditions.checkNotNull(names, "names cannot be null");
    return Arrays.stream(names).map(n -> new TeamCode(n)).collect(GuavaCollectors.immutableList());
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
