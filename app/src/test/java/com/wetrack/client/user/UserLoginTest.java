package com.wetrack.client.user;

import com.google.gson.JsonObject;
import com.wetrack.client.CryptoUtils;
import com.wetrack.client.EntityResponseHelper;
import com.wetrack.client.WeTrackClientTest;
import com.wetrack.model.UserToken;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UserLoginTest extends WeTrackClientTest {

    private String username = "robert-peng";
    private String password = "Not matter";

    private EntityResponseHelper<UserToken> entityHelper = new EntityResponseHelper<>(gson);

    @Test
    public void testUserLoginRequestFormat() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(400));

        client.userLogin(username, password, entityHelper.callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        // Assert the request is sent as-is
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is("/login"));

        JsonObject requestBody = gson.fromJson(request.getBody().readUtf8(), JsonObject.class);
        assertThat(requestBody.entrySet().size(), is(2));
        assertThat(requestBody.get("username").getAsString(), is(username));
        assertThat(requestBody.get("password").getAsString(), is(CryptoUtils.md5Digest(password)));
    }

    @Test
    public void testUserLoginOnOkResponse() throws Exception {
        String testResponse = readResource("test_user_login/200.json");
        server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));

        client.userLogin(username, password, entityHelper.callback(200));

        entityHelper.assertReceivedEntity(200);

        UserToken receivedToken = entityHelper.getReceivedEntity();
        JsonObject responseBody = gson.fromJson(testResponse, JsonObject.class);
        assertThat(receivedToken.getToken(), is(responseBody.get("token").getAsString()));
        assertThat(receivedToken.getUsername(), is(responseBody.get("username").getAsString()));
        assertThat(receivedToken.getExpireTime().toString(), is(responseBody.get("expireTime").getAsString()));
    }

    @Test
    public void testUserLoginOnErrorResponse() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401).setBody(readResource("test_user_login/401.json")));

        client.userLogin(username, password, entityHelper.callback(200));

        // Assert the error response is received and triggers the observer
        entityHelper.assertReceivedErrorMessage(401);
    }

}
