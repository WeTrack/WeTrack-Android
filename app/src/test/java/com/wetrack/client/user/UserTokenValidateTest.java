package com.wetrack.client.user;

import com.google.gson.JsonObject;
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

public class UserTokenValidateTest extends WeTrackClientTest {

    private String username = "robert-peng";
    private String token = "12345678absd";

    private EntityResponseHelper<UserToken> entityHelper = new EntityResponseHelper<>(gson);

    @Test
    public void testTokenValidateRequestFormat() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(200));
        client.tokenVerify(username, token, entityHelper.callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is("/users/" + username + "/tokenVerify"));
        assertThat(request.getBody().readUtf8(), is(token));
    }

    @Test
    public void testTokenValidateOnErrorResponse() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401).setBody(readResource("test_token_verify/401.json")));
        client.tokenVerify(username, token, entityHelper.callback(200));

        entityHelper.assertReceivedErrorMessage(401);
    }

    @Test
    public void testTokenValidateOnOkResponse() throws Exception {
        String testResponse = readResource("test_token_verify/200.json");
        server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));

        client.userLogin(username, token, entityHelper.callback(200));

        entityHelper.assertReceivedEntity(200);

        UserToken receivedToken = entityHelper.getReceivedEntity();
        JsonObject responseBody = gson.fromJson(testResponse, JsonObject.class);
        assertThat(receivedToken.getToken(), is(responseBody.get("token").getAsString()));
        assertThat(receivedToken.getUsername(), is(responseBody.get("username").getAsString()));
        assertThat(receivedToken.getExpireTime().toString(), is(responseBody.get("expireTime").getAsString()));
    }

}
