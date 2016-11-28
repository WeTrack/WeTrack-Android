package com.wetrack.service.ws;

public class WsMessage extends WsResponse {
    private String message;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static final int MESSAGE_ACK = 1000;
    public static final int TOKEN_VERIFIED = 1001;
    public static final int INVALID_MESSAGE = 2000;
    public static final int NOT_AUTHENTICATED = 2001;
    public static final int NOT_CHAT_MEMBER = 2002;
    public static final int INVALID_CHAT_ID = 2003;
    public static final int INVALID_TOKEN = 2004;
    public static final int TOKEN_USED_IN_OTHER_SESSION = 2005;
}
