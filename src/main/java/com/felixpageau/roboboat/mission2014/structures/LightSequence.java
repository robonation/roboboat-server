package com.felixpageau.roboboat.mission2014.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jersey.repackaged.com.google.common.base.Objects;
import jersey.repackaged.com.google.common.base.Objects.ToStringHelper;
import jersey.repackaged.com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LightSequence {
    private LightColor[] sequence;

    @JsonCreator
    public LightSequence(@JsonProperty(value = "sequence") LightColor[] sequence) {
        this.sequence = Preconditions.checkNotNull(sequence);
        Preconditions.checkArgument(sequence.length == 3);
    }

    public static LightSequence generateRandomLightSequence() {
        List<LightColor> lightColors = new ArrayList<>(Arrays.asList(LightColor.values()));
        Collections.shuffle(lightColors);

        while (lightColors.size() > 3) {
            lightColors.remove(Math.round((float) Math.random() * lightColors.size() - 0.5f));
        }

        return new LightSequence(lightColors.toArray(new LightColor[lightColors.size()]));
    }

    public LightColor[] getSequence() {
        return Arrays.copyOf(sequence, sequence.length);
    }
    
    public String lightSequenceString() {
        String str = "";
        for (LightColor lc : sequence) {
            str += lc.toString().charAt(0);
        }
        return str.toLowerCase();
    }
    
    @JsonIgnore
    @Override
    public String toString() {
        ToStringHelper helper = Objects.toStringHelper(this);
        for (LightColor lc : sequence) {
            helper.addValue(lc);
        }
        return helper.toString();
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        int hash = 31;
        for (LightColor c : sequence) {
            hash *= Objects.hashCode(c);
        }
        return hash;
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
        if (obj instanceof LightSequence) {
            LightSequence other = (LightSequence) obj;
            if (other.getSequence().length != sequence.length) {
                return false;
            }
            for (int i = 0; i < sequence.length; i++) {
                if (sequence[i] != other.sequence[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
