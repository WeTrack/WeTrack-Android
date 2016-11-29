package com.wetrack.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by moziliang on 16/10/2.
 */
public class MarkerDataFormat {
    private String username;
    private LatLng latLng;
    private String information;

    public MarkerDataFormat(String username, LatLng latLng, String information) {
        this.username = username;
        this.latLng = latLng;
        this.information = information;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public String toString() {
        return "MarkerDataFormat{" +
                "username='" + username + '\'' +
                ", latLng=" + latLng +
                ", information='" + information + '\'' +
                '}';
    }
}
