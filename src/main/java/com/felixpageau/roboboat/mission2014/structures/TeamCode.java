package com.felixpageau.roboboat.mission2014.structures;

import java.util.regex.Pattern;

import jersey.repackaged.com.google.common.base.Objects;
import jersey.repackaged.com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

public class TeamCode {
    private static final Pattern TEAM_CODE_REGEX = Pattern.compile("[a-zA-Z]{2,5}");
    private final String value;
    
    @JsonCreator
    public TeamCode(String value) {
        Preconditions.checkNotNull(value);
        Preconditions.checkArgument(TEAM_CODE_REGEX.matcher(value).matches());

        this.value = value;
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
        return String.format("TeamCode: %s", value);
    }
}
