package com.wetrack.client;

import com.wetrack.model.Message;

/**
 * Asynchronous callback object for methods in {@code WeTrackClient} which returns {@link Message}
 * when succeeds.
 * <p>
 * A {@code successfulStatusCode} can be assigned to a {@code MessageCallback} via constructor,
 * through which it can indicates the client a customized successful response status code.
 * By default, the {@code successfulStatusCode} is set to {@code 200}.
 *
 * @see #onSuccess(String)
 * @see #onFail(String, int)
 * @see #onError(Throwable)
 */
public class MessageCallback {
    private final int successfulStatusCode;

    /**
     * Creates a {@code MessageCallback} with {@code successfulStatusCode} set to {@code 200}.
     */
    public MessageCallback() {
        this(200);
    }

    /**
     * Creates a {@code MessageCallback} with the given {@code successfulStatusCode}.
     *
     * @param successfulStatusCode the given {@code successfulStatusCode}.
     */
    public MessageCallback(int successfulStatusCode) {
        this.successfulStatusCode = successfulStatusCode;
    }

    /**
     * Invoked when a successful response (with the designated successful status code) is received
     * from the server and a {@link Message} object has been successfully extracted from the
     * response body.
     *
     * @param message the {@code message} field of the received {@code Message} object.
     */
    protected void onSuccess(String message) {}

    /**
     * Invoked when a failed response (whose status code does not equal to the given successful
     * status code) is received from the server and a {@link Message} object representing the
     * error message has been successfully extracted from the response body.
     *
     * @param message the {@code message} field of the received {@code Message} object.
     * @param failedStatusCode status code of the received failed response.
     */
    protected void onFail(String message, int failedStatusCode) {}

    /**
     * Invoked when exception occurred when connecting to the server.
     *
     * @param ex the occurred exception.
     */
    protected void onError(Throwable ex) {}

    public int getSuccessfulStatusCode() {
        return successfulStatusCode;
    }

}
