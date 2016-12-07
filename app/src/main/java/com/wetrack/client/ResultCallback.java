package com.wetrack.client;

/**
 * Asynchronous callback object for methods in {@code NetworkClient} which returns empty body
 * in all cases. Whether the request is successful will be determined by comparing the received
 * status code with the successful status code provided by this callback object.
 */
public class ResultCallback {
    private final int successfulStatusCode;

    /**
     * Creates a {@code ResultCallback} with {@code successfulStatusCode} set to {@code 200}.
     */
    public ResultCallback() {
        this(200);
    }

    /**
     * Creates a {@code ResultCallback} with the given {@code successfulStatusCode}.
     *
     * @param successfulStatusCode the given {@code successfulStatusCode}.
     */
    public ResultCallback(int successfulStatusCode) {
        this.successfulStatusCode = successfulStatusCode;
    }

    /**
     * Invoked when a response with the provided successful status code is received.
     */
    protected void onSuccess() {}

    /**
     * Invoked with the received status code when a response with a status code other than the provided
     * successful status code is received.
     *
     * @param failedStatusCode the received status code.
     */
    protected void onFail(int failedStatusCode) {}

    /**
     * Invoked when an exception occurred during the connection.
     *
     * @param ex the occurred exception.
     */
    protected void onError(Throwable ex) {}

    public int getSuccessfulStatusCode() {
        return successfulStatusCode;
    }
}
