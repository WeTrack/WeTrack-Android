package com.wetrack.utils;

public abstract class Tags {

    /**
     * Filter for {@link Chat}: {@code Chat*}
     */
    public static abstract class Chat {
        public static final String ACTIVITY = "Chat Activity";
        public static final String SERVICE = "Chat Service";
        public static final String WS_MANAGER = "Chat Ws Manager";
        public static final String WS_MANAGER_TASK = "Chat Ws Manager Task";
        public static final String WS_MANAGER_WORKER = "Chat Ws Manager Worker";
    }

    public static abstract class Location{
        public static final String SERVICE = "LocationService";
    }

}
