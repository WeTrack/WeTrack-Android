package com.wetrack.client;

import com.wetrack.client.test.EntityResponseHelper;
import com.wetrack.client.WeTrackClientTest;
import com.wetrack.model.Chat;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserChatGetTest extends WeTrackClientTest {

    private EntityResponseHelper<List<Chat>> entityHelper = new EntityResponseHelper<>(gson);

    @Test
    public void testUserChatGetRequestFormat() throws InterruptedException {
        MockResponse response = new MockResponse().setResponseCode(200)
                .setBody(readResource("test_user_chat_get/200.json"));
        server.enqueue(response);

        client.getUserChatList(robertPeng.getUsername(), dummyToken, entityHelper.callback(200));
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("GET"));
        assertThat(request.getPath(), is("/users/" + robertPeng.getUsername() + "/chats?token=" + dummyToken));
        assertThat(request.getBody().readUtf8().isEmpty(), is(true));
    }

}
