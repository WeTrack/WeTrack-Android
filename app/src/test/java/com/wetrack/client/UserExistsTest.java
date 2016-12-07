package com.wetrack.client;

import com.wetrack.client.test.ResultResponseHelper;
import com.wetrack.client.WeTrackClientTest;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UserExistsTest extends WeTrackClientTest {

    private String username = "robert-peng";
    private ResultResponseHelper resultHelper = new ResultResponseHelper(200);

    @Test
    public void testUserExistsRequestFormat() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(200));
        client.userExists(username, resultHelper.callback());

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("HEAD"));
        assertThat(request.getPath(), is("/users/" + username));
    }

    @Test
    public void testUserExistsOnErrorResponse() {
        server.enqueue(new MockResponse().setResponseCode(404));
        client.userExists(username, resultHelper.callback());

        resultHelper.assertFailed(404);
    }

    @Test
    public void testUserExistsOnOkResponse() {
        server.enqueue(new MockResponse().setResponseCode(200));
        client.userExists(username, resultHelper.callback());

        resultHelper.assertSucceeded();
    }

}
