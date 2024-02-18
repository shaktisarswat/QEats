package com.javaproject.qeats.exchanges;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Implement GetRestaurantsRequest.
// Complete the class such that it is able to deserialize the incoming query params from
// REST API clients.
// For instance, if a REST client calls API
// /qeats/v1/restaurants?latitude=28.4900591&longitude=77.536386&searchFor=tamil,
// this class should be able to deserialize lat/long and optional searchFor from that.
public class GetRestaurantsRequest {
    @NotNull
    @Min(value = -90)
    @Max(value = 90)
    private Double latitude;
    @NotNull
    @Min(value = -180)
    @Max(value = 180)
    private Double longitude;
    private String searchFor;

    public GetRestaurantsRequest(@NotNull @Min(-90) @Max(90) Double latitude,
                                 @NotNull @Min(-180) @Max(180) Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GetRestaurantsRequest() {
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getSearchFor() {
        return searchFor;
    }

    public void setSearchFor(String searchFor) {
        this.searchFor = searchFor;
    }
}

