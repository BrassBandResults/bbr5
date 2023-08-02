package uk.co.bbr.services.map.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class LocationPoint {
    private final String type = "Point";
    private Double[] coordinates = new Double[2];

    public LocationPoint() {}

    public LocationPoint(String longitude, String latitude) {
        this.coordinates[0] = Double.parseDouble(longitude);
        this.coordinates[1] = Double.parseDouble(latitude);
    }

    public Double getLongitude() {
        return this.coordinates[0];
    }
    public Double getLatitude() {
        return this.coordinates[1];
    }
}
