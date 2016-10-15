package com.wetrack.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by moziliang on 16/10/2.
 */
public class MarkerDataFormat {
    public MarkerDataFormat() {
    }

    public MarkerDataFormat(String title, LatLng latLng, String information) {
        this.title = title;
        this.latLng = latLng;
        this.information = information;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String title;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng latLng;

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String information;

    @Override
    public String toString() {
        return "MarkerDataFormat{" +
                "title='" + title + '\'' +
                ", latLng=" + latLng +
                ", information='" + information + '\'' +
                '}';
    }
}
