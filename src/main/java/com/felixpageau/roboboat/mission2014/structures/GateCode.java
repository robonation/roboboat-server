package com.felixpageau.roboboat.mission2014.structures;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class GateCode {
    private static final Pattern EXIT_REGEX = Pattern.compile("[X-Z]{1}");
    private final String gateCode;
    
    @JsonCreator
    public GateCode(int entrance, String exit) {
        Preconditions.checkNotNull(exit);
        Preconditions.checkArgument(EXIT_REGEX.matcher(exit).matches());
        Preconditions.checkArgument(entrance >= 1 && entrance <= 3);
        this.gateCode = String.format("(%d,%s)", entrance, exit);
    }
    
    public static GateCode generateRandomGateCode() {
        int entrance = (int) Math.round(Math.random() * 3f + 0.5f);
        String exit = "X";
        return new GateCode(entrance, exit);
    }

    public String getGateCode() {
        return gateCode;
    }
    
    @JsonIgnore
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("code", gateCode).toString();
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hashCode(gateCode);
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
            return Objects.equal(gateCode, other.gateCode);
        }
        return false;
    }
}
