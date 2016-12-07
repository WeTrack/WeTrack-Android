package com.wetrack.client;

import com.google.gson.JsonParseException;
import com.wetrack.client.test.EntityResponseHelper;
import com.wetrack.client.WeTrackClientTest;
import com.wetrack.model.User;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserGetTest extends WeTrackClientTest {

    private String username = "windy-chan";

    private EntityResponseHelper<User> entityHelper = new EntityResponseHelper<>(gson);

    @Test
    public void testUserGetRequestFormat() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(404));

        client.getUserInfo(username, entityHelper.callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        // Assert the request is sent as-is
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("GET"));
        assertThat(request.getPath(), is("/users/" + username));
    }

    @Test
    public void testUserGetOnErrorResponse() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404).setBody(readResource("test_user_get/404.json")));

        client.getUserInfo(username, entityHelper.callback(200));

        // Assert the error response is received and the subscriber is triggered
        entityHelper.assertReceivedErrorMessage(404);
    }

    @Test
    public void testUserGetOnInvalidResponse() throws Exception {
        String testResponse = readResource("test_user_get/200_invalid_field.json");

        server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));
        client.getUserInfo(username, entityHelper.callback(200));
        // Assert the error is received and the subscriber is triggered
        assertThat(entityHelper.getReceivedException(), notNullValue());
        assertThat(entityHelper.getReceivedException() instanceof JsonParseException, is(true));
        assertThat(entityHelper.getReceivedEntity(), nullValue());
    }
}
