package com.wetrack.client;

import com.wetrack.model.Message;

import retrofit2.Response;

/**
 * Asynchronous callback object for methods in {@link NetworkClient}, with four callback methods and its
 * default implementations (which do nothing at all).
 *
 * @see NetworkClient
 * @see #onReceive(Object)
 * @see #onResponse(Response)
 * @see #onException(Throwable)
 * @see #onErrorMessage(Message)
 *
 * @param <T> the expected type of the response entity
 */
public abstract class EntityCallback<T> {

    /**
     * Invoked on entity successfully received from response body. This method will be invoked
     * after {@link #onResponse(Response)} if the status code of the response was {@code 200}.
     *
     * @param value the received entity.
     */
    protected void onReceive(T value) {}

    /**
     * Invoked on response received from server. This method will always be invoked unless
     * there is an exception occurred when connecting to the server.
     *
     * @param response the received raw response.
     */
    protected void onResponse(Response<T> response) {}

    /**
     * Invoked when there is an exception occurred when connecting to the server.
     *
     * @param ex the exception occurred when connecting to the server.
     */
    protected void onException(Throwable ex) {}

    /**
     * Invoked on response received from server and the status code of the response is not {@code 200}
     * (receiving a error response).
     *
     * @param response the received raw response.
     */
    protected void onErrorMessage(Message response) {}

}
