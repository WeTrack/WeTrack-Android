package com.wetrack.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MathUtils {

    static public double[] getCenterAndLengthRange(ArrayList<LatLng> allLatLng) {
        double latitudeMax = -90, latitudeMin = 90;
        double longitudeMax = -180, longitudeMin = 180;
        for (LatLng aLatLng : allLatLng) {
            if (aLatLng.latitude > latitudeMax) {
                latitudeMax = aLatLng.latitude;
            }
            if (aLatLng.latitude < latitudeMin) {
                latitudeMin = aLatLng.latitude;
            }
            if (aLatLng.longitude > longitudeMax) {
                longitudeMax = aLatLng.longitude;
            }
            if (aLatLng.longitude < longitudeMin) {
                longitudeMin = aLatLng.longitude;
            }
        }
        double latitudeRangeLength = Math.abs(latitudeMax - latitudeMin);
        double longitudeRangeLength = longitudeMax - longitudeMin;
        if (longitudeRangeLength > 180) {
            longitudeRangeLength = 360 - longitudeRangeLength;
        }
        double centerLatitude = (latitudeMax + latitudeMin) / 2;
        double centerLongitude = (longitudeMax + longitudeMin) / 2;

        return new double[]{centerLatitude, centerLongitude, latitudeRangeLength, longitudeRangeLength};
    }

    static public float getZoomFromLatLngRange(
            int viewHeight, int viewWidth, double latitudeRangeLength, double longitudeRangeLength) {
        if (Math.abs(latitudeRangeLength) < 1e-9 && Math.abs(longitudeRangeLength) < 1e-9) {
            return 14;
        }

        //the basic concept is :
        //for zoom == n, the width of the whole world in the app is 256 * 2 ^ n, the longitude range is (-180, 180)
        double rate = 0, temp = 0, answer = 0;
        double tempLatitude = longitudeRangeLength / viewWidth * viewHeight;
        if (tempLatitude <= latitudeRangeLength) {
            rate = viewHeight * (4.0 / 16) / latitudeRangeLength;// == (256 * 2^n) / (2 * 180)
            temp = rate * (2 * 180) / 256;
            answer = Math.log(temp) / Math.log(2.0);
        } else {
            rate = viewWidth * (4.0 / 16) / longitudeRangeLength;// == 256 * 2^n / (2 * 180)
            temp = rate * (2 * 180) / 256;
            answer = Math.log(temp) / Math.log(2.0);
        }

        return (float)answer;
    }
}
