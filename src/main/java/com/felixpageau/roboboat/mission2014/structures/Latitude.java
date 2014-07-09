package com.felixpageau.roboboat.mission2014.structures;

import jersey.repackaged.com.google.common.base.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

public class Latitude {
    private final float value;
    
    @JsonCreator
    public Latitude(float value) {
        this.value = value;
    }
    
    @JsonValue
    public float getValue() {
        return value;
    }
    
    @JsonIgnore
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("value", value).toString();
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
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
        if (obj instanceof Latitude) {
            Latitude other = (Latitude) obj;
            return Objects.equal(value, other.value);
        }
        return false;
    }
}
