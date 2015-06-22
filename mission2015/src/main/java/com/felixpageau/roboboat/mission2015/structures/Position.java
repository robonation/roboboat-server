package com.felixpageau.roboboat.mission2015.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Floats;

public class Position {
  public static final Position FOUNDERS = new Position(Datum.WGS84, new Latitude(36.8020641F), new Longitude(-76.1912658F));
  private final Datum datum;
  private final Longitude longitude;
  private final Latitude latitude;

  @JsonCreator
  public Position(@JsonProperty(value = "datum") Datum datum, @JsonProperty(value = "latitude") Latitude latitude,
      @JsonProperty(value = "longitude") Longitude longitude) {
    this.datum = Preconditions.checkNotNull(datum, "The provided datum cannot be null");
    this.longitude = Preconditions.checkNotNull(longitude, "The provided longitude cannot be null");
    this.latitude = Preconditions.checkNotNull(latitude, "The provided latitude cannot be null");
  }

  /**
   * NMEA deserializer for a {@link Position}
   * 
   * @param latitude
   * @param longitude
   * @return
   */
  public static Position fromNMEA(String latitude, String longitude) {
    Preconditions.checkNotNull(latitude, "The provided latitude cannot be null");
    Preconditions.checkNotNull(longitude, "The provided longitude cannot be null");
    Preconditions.checkArgument(Floats.tryParse(latitude) != null, String.format("The provided latitude %s is not a valid float number", latitude));
    Preconditions.checkArgument(Floats.tryParse(longitude) != null, String.format("The provided longitude %s is not a valid float number", longitude));
    return new Position(Datum.WGS84, new Latitude(Float.parseFloat(latitude)), new Longitude(Float.parseFloat(longitude)));
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
    return MoreObjects.toStringHelper(this).add("datum", datum).add("longitude", longitude).add("latitude", latitude).toString();
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
    if (!(obj instanceof Position)) return false;
    Position other = (Position) obj;
    return Objects.equal(datum, other.datum) && Objects.equal(longitude, other.longitude) && Objects.equal(latitude, other.latitude);
  }
}
