package com.wetrack.utils;

/**
 * Created by moziliang on 16/9/24.
 */
public class ConstantValues {
    //for debug
    public static String debugTab = "debug";
    public static String gpsDebug = "gps_debug";
    public static String markerDebug = "marker";
    public static String permission = "permission";
    public static String touchDebug = "touch_debug";
    public static String databaseDebug = "mydatabase";

    //marker arraylist operation code
    public static final int MARKERLIST_CLEAR = 0xffffffff;
    public static final int MARKERLIST_ADD = 0xfffffffe;

    //for handler
    public static final int MARKER_DEMO_TAG = 0xffffffef;
    public static final int NAVIGATION_DEMO_TAG = 0xffffffee;
    public static final int NAVIGATION_RESULT_TAG = 0xffffffed;
    public static final int CHECK_GPS = 0xffffffec;

    //for request permissions
    public static final int PERMISSION_ACCESS_FINE_LOCATION = 0xffffffdf;
    public static final int PERMISSION_ACCESS_COARSE_LOCATION = 0xffffffde;

    //for contact view mode
    final static public int CONTACT_MODE_NEW_GROUP = 0xffffffcf;
    final static public int CONTACT_MODE_ADD_FRIEND = 0xffffffce;

    //
    final static public String NAME_SEPERATE_STRING = "|";
}
