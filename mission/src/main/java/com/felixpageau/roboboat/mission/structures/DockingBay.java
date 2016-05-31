package com.felixpageau.roboboat.mission.structures;

import java.security.SecureRandom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class DockingBay {
  private final Symbol symbol;
  private final SymbolColor color;

  @JsonCreator
  public DockingBay(@JsonProperty(value = "symbol") Symbol symbol, @JsonProperty(value = "color") SymbolColor color) {
    this.symbol = Preconditions.checkNotNull(symbol, "symbol cannot be null");
    this.color = Preconditions.checkNotNull(color, "color cannot be null");
  }

  public static DockingBay generateRandomDockingBay() {
    return new DockingBay(Symbol.values()[new SecureRandom().nextInt(Symbol.values().length)], SymbolColor.values()[new SecureRandom().nextInt(SymbolColor
        .values().length)]);
  }

  public Symbol getSymbol() {
    return symbol;
  }

  public SymbolColor getColor() {
    return color;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("symbol", symbol).add("color", color).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(symbol, color);
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
      return Objects.equal(symbol, other.symbol) && Objects.equal(color, other.color);
    }
    return false;
  }
}
