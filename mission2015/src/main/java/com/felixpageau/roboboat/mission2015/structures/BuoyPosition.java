package com.felixpageau.roboboat.mission2015.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class BuoyPosition {
  private final Datum datum;
  private final Longitude longitude;
  private final Latitude latitude;

  @JsonCreator
  public BuoyPosition(@JsonProperty(value = "datum") Datum datum, @JsonProperty(value = "latitude") Latitude latitude,
      @JsonProperty(value = "longitude") Longitude longitude) {
    this.datum = datum;
    this.longitude = longitude;
    this.latitude = latitude;
  }

  public Datum getDatum() {
    return datum;
  }

  public Longitude getLongitude() {
    return longitude;
  }

  public Latitude getLatitude() {
    return latitude;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("datum", datum).add("longitude", longitude).add("latitude", latitude).toString();
  }

  @JsonIgnore
  @Override
  public int hashCode() {
    return Objects.hashCode(datum, longitude, latitude);
  }

  @JsonIgnore
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null) return false;
    if (!(obj instanceof BuoyPosition)) return false;
    BuoyPosition other = (BuoyPosition) obj;
    return Objects.equal(datum, other.datum) && Objects.equal(longitude, other.longitude) && Objects.equal(latitude, other.latitude);
  }
}
