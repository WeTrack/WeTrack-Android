package com.wetrack.utils;

public abstract class ConstantValues {
    //for debug
    public static String debugTab = "debug";
    public static String gpsDebug = "gps_debug";
    public static String markerDebug = "marker";
    public static String permission = "permission";
    public static String touchDebug = "touch_debug";
    public static String databaseDebug = "mydatabase";

    //for broadcast action name
    public static String ACTION_UPDATE_GROUP_LIST = "com.wetrack.action.friendList";
    public static String ACTION_UPDATE_FRIEND_LIST = "com.wetrack.action.friendList";
    public static String ACTION_UPDATE_USER_INFO = "com.wetrack.action.userInfo";
    public static String ACTION_UPDATE_CHAT_MSG = "com.wetrack.action.chatMsg";
    public static String ACTION_UPDATE_USER_POS = "com.wetrack.action.userPos";

    //activity transition
    final public static int CHAT_LIST_REQUEST_CODE = 1;
    final public static int CREATE_CHAT_REQUEST_CODE = 2;
    final public static int ADD_FRIEND_REQUEST_CODE = 3;

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


    //for server connection
    final static public String serverBaseUrl = "http://www.robertshome.com.cn/";
    final static public int timeoutSeconds = 5;
}
