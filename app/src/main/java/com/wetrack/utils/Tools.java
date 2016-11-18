package com.wetrack.utils;

import android.util.DisplayMetrics;

import com.wetrack.BaseApplication;

/**
 * Created by moziliang on 16/8/25.
 */
public class Tools {
    private static int screenW = -1, screenH = -1;

    public static int getScreenW() {
        if (screenW < 0) {
            initScreenDisplayParams();
        }
        return screenW;
    }

    public static int getScreenH() {
        if (screenH < 0) {
            initScreenDisplayParams();
        }
        return screenH;
    }

    private static void initScreenDisplayParams() {
        DisplayMetrics dm = BaseApplication.getContext().getResources()
                .getDisplayMetrics();
        screenW = dm.widthPixels;
        screenH = dm.heightPixels;
    }
}
