package com.wetrack.client.chat;

import com.wetrack.client.EntityResponseHelper;
import com.wetrack.client.WeTrackClientTest;
import com.wetrack.model.User;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChatMemberGetTest extends WeTrackClientTest {

    private String dummyChatId = "12346523";

    private EntityResponseHelper<List<User>> entityHelper = new EntityResponseHelper<>(gson);

    @Test
    public void testChatMemberGetRequestFormat() throws InterruptedException {
        MockResponse response = new MockResponse().setResponseCode(200)
                .setBody(readResource("test_chat_member_get/200.json"));
        server.enqueue(response);

        client.getChatMembers(dummyChatId, dummyToken, entityHelper.callback(200));
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("GET"));
        assertThat(request.getPath(), is("/chats/" + dummyChatId + "/members?token=" + dummyToken));
        assertThat(request.getBody().readUtf8().isEmpty(), is(true));
    }

}
