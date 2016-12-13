package com.wetrack.utils;

public abstract class Tags {

    public static abstract class Dao {
        public static final String LOCATION = "Dao Location";
    }

    /**
     * Filter for {@link Client}: {@code Client*}
     */
    public static abstract class Client {
        public static final String CALLBACK = "Client Callback";
        public static final String NETWORK = "Client Network";
        public static final String CACHED = "Client Cached";
    }

    /**
     * Filter for {@link Chat}: {@code Chat*}
     */
    public static abstract class Chat {
        public static final String ACTIVITY = "Chat Activity";
        public static final String SERVICE = "ChatService";
        public static final String WS_MANAGER = "Chat Ws Manager";
        public static final String WS_MANAGER_TASK = "Chat Ws Manager Task";
        public static final String WS_MANAGER_WORKER = "Chat Ws Manager Worker";
    }

    public static abstract class Location{
        public static final String SERVICE = "LocationService";
    }

    public static abstract class Map{
        public static final String MARKER = "MapMarker";
    }

    public static abstract class UserInfo{
        public static final String PORTRAIT = "PORTRAIT";
    }

    public static abstract class Setting{
        public static final String SHARE_LOC_PERMISSION = "sharePermission";
    }
}
