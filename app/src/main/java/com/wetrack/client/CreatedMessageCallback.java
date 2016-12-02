package com.wetrack.client;

import com.wetrack.model.CreatedMessage;
import com.wetrack.model.Message;

/**
 * Asynchronous callback object for methods in {@code NetworkClient} which creates a given entity
 * on the server and returns {@link CreatedMessage} when succeeds.
 *
 * @see #onSuccess(String, String)
 * @see #onFail(String, int)
 * @see #onError(Throwable)
 */
public abstract class CreatedMessageCallback {

    /**
     * Invoked when a {@code 201 Created} response is received from the server and a
     * {@link CreatedMessage} object has been successfully extracted from the
     * response body.
     *
     * @param newEntityId the {@code entity_url} field of the extracted {@code CreatedMessage}.
     * @param message the {@code message} field of the extracted {@code CreatedMessage}.
     */
    protected void onSuccess(String newEntityId, String message) {}

    /**
     * Invoked when a failed response (with status code other then {@code 201}) is received from
     * the server and a {@link Message} object has been successfully extracted from
     * the response body.
     *
     * @param message the {@code message} field of the extracted {@code Message}.
     * @param failedStatusCode the status code of the received response.
     */
    protected void onFail(String message, int failedStatusCode) {}

    /**
     * Invoked when exception occurred when connection to the server.
     *
     * @param ex the occurred exception.
     */
    protected void onError(Throwable ex) {}
}
