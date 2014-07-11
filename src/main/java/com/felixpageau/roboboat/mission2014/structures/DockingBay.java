package com.felixpageau.roboboat.mission2014.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;


public class DockingBay {
    private final Symbol dockingBay;
    
    @JsonCreator
    public DockingBay (@JsonProperty(value = "dockingBay") Symbol dockingBay) {
        this.dockingBay = dockingBay;
    }
    
    public static DockingBay generateRandomDockingBay() {
        Symbol[] symbols = Symbol.values();
        int index = Math.round((float)Math.random()*symbols.length-0.5f);
        return new DockingBay(symbols[index]);
    }

    public Symbol getDockingBay() {
        return dockingBay;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("symbol", dockingBay).toString();
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hashCode(dockingBay);
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
        if (obj instanceof DockingBay) {
            DockingBay other = (DockingBay) obj;
            return Objects.equal(dockingBay, other.dockingBay);
        }
        return false;
    }
}
