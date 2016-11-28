package com.wetrack.service.ws;

public abstract class WsResponse {
    private int code;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
}
