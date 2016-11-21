package com.wetrack.client.user;

import com.google.gson.JsonObject;
import com.wetrack.client.CryptoUtils;
import com.wetrack.client.MessageResponseHelper;
import com.wetrack.client.WeTrackClientTest;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UserPasswordUpdateTest extends WeTrackClientTest {

    private String username = "robert-peng";
    private String oldPassword = "Not matter";
    private String newPassword = "Not matter";

    private MessageResponseHelper messageHelper = new MessageResponseHelper(200);

    @Test
    public void testUserPasswordUpdateRequestFormat() throws Exception {
        String testResponseBody = readResource("test_user_password_update/200.json");
        MockResponse testResponse = new MockResponse().setResponseCode(200).setBody(testResponseBody);
        server.enqueue(testResponse);

        client.updateUserPassword(username, oldPassword, newPassword, messageHelper.callback());

        // Assert the request is sent as-is
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("PUT"));
        assertThat(request.getPath(), is("/users/" + username + "/password"));

        JsonObject requestBody = gson.fromJson(request.getBody().readUtf8(), JsonObject.class);
        assertThat(requestBody.entrySet().size(), is(2));
        assertThat(requestBody.get("old_password").getAsString(), is(CryptoUtils.md5Digest(oldPassword)));
        assertThat(requestBody.get("new_password").getAsString(), is(newPassword));
    }

    @Test
    public void testUserPasswordUpdateOnOkResponse() throws Exception {
        String testResponseBody = readResource("test_user_password_update/200.json");
        MockResponse testResponse = new MockResponse().setResponseCode(200).setBody(testResponseBody);
        server.enqueue(testResponse);

        client.updateUserPassword(username, oldPassword, newPassword, messageHelper.callback());

        messageHelper.assertReceivedSuccessfulMessage();
        JsonObject testResponseJson = gson.fromJson(testResponseBody, JsonObject.class);
        assertThat(messageHelper.getReceivedMessage(), is(testResponseJson.get("message").getAsString()));
    }

    @Test
    public void testUserPasswordUpdateOnErrorResponse() throws Exception {
        String testResponseBody = readResource("test_user_password_update/401.json");
        MockResponse testResponse = new MockResponse().setResponseCode(401).setBody(testResponseBody);
        server.enqueue(testResponse);

        client.updateUserPassword(username, oldPassword, newPassword, messageHelper.callback());

        messageHelper.assertReceivedFailedMessage(401);
        JsonObject testResponseJson = gson.fromJson(testResponseBody, JsonObject.class);
        assertThat(messageHelper.getReceivedMessage(), is(testResponseJson.get("message").getAsString()));
    }

}
