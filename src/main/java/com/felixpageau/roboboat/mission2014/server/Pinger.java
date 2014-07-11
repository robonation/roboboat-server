package com.felixpageau.roboboat.mission2014.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felixpageau.roboboat.mission2014.structures.BuoyColor;
import com.felixpageau.roboboat.mission2014.structures.BuoyPosition;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Pinger{
    private final BuoyColor buoyColor;
    private final BuoyPosition buoyPosition;
    
    @JsonCreator
    public Pinger(
            @JsonProperty(value = "buoyColor") BuoyColor buoyColor, 
            @JsonProperty(value = "buoyPosition") BuoyPosition buoyPosition) {
        this.buoyColor = Preconditions.checkNotNull(buoyColor);
        this.buoyPosition = Preconditions.checkNotNull(buoyPosition);
    }
    
    public BuoyColor getBuoyColor() {
        return buoyColor;
    }
    
    public BuoyPosition getBuoyPosition() {
        return buoyPosition;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("buoyColor", buoyColor).add("buoyPosition",buoyPosition).toString();
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hashCode(buoyColor, buoyPosition);
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
        if (obj instanceof Pinger) {
            Pinger other = (Pinger) obj;
            return Objects.equal(buoyColor, other.buoyColor)
                    && Objects.equal(buoyPosition, other.buoyPosition);
        }
        return false;
    }
}
