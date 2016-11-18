package com.wetrack.client.user;

import com.wetrack.client.MessageResponseHelper;
import com.wetrack.client.WeTrackClientTest;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserChatExitTest extends WeTrackClientTest {

    private String dummyChatId = "12346523";

    private MessageResponseHelper helper = new MessageResponseHelper(200);

    @Test
    public void testUserChatExitRequestFormat() throws InterruptedException {
        MockResponse response = new MockResponse().setResponseCode(200)
                .setBody(readResource("test_user_chat_exit/200.json"));
        server.enqueue(response);

        String expectedPath = "/users/" + robertPeng.getUsername() + "/chats/" + dummyChatId
                + "?token=" + dummyToken;

        client.exitChat(robertPeng.getUsername(), dummyToken, dummyChatId, helper.callback());
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("DELETE"));
        assertThat(request.getPath(), is(expectedPath));
        assertThat(request.getBody().readUtf8().isEmpty(), is(true));
    }

}
