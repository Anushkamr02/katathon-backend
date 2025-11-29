package com.safewalk.springboot.backend.dto;

/**
 * DTO for a pair of latitude/longitude coordinates.
 */
public class LocationDto {
    private Double lat;
    private Double lng;
    private String rawAddress;

    public LocationDto() {}
    public LocationDto(Double lat, Double lng) { this.lat = lat; this.lng = lng; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public String getRawAddress() { return rawAddress; }
    public void setRawAddress(String rawAddress) { this.rawAddress = rawAddress; }
}
