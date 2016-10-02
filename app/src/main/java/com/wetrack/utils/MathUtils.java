package com.wetrack.utils;

import android.content.Context;

/**
 * Created by moziliang on 16/10/3.
 */
public class MathUtils {

    static public float getZoomFromLatLngRange(int viewHeight, int viewWidth, double latitudeRangeLength, double longitudeRangeLength) {

        double rate = 0, temp = 0, answer = 0;
        double tempLatitude = longitudeRangeLength / viewWidth * viewHeight;
        if (tempLatitude <= latitudeRangeLength) {
            rate = viewHeight * (9.0 / 16) / latitudeRangeLength;// == (256 * 2^n) / (2 * 180)
            temp = rate * (2 * 180) / 256;
            answer = Math.log(temp) / Math.log(2.0);
        } else {
            rate = viewWidth * (9.0 / 16) / longitudeRangeLength;// == 256 * 2^n / (2 * 180)
            temp = rate * (2 * 180) / 256;
            answer = Math.log(temp) / Math.log(2.0);
        }

        return (int)answer;
    }
}
