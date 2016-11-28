package com.wetrack.utils;

public abstract class Tags {

    public static abstract class Dao {
        public static final String LOCATION = "Dao Location";
    }

    /**
     * Filter for {@link Client}: {@code Client*}
     */
    public static abstract class Client {
        public static final String NETWORK = "Client Network";
        public static final String DB_CACHE = "Client DB Cache";
    }

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
