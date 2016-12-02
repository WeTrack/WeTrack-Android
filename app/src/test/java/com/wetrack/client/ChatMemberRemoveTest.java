package com.wetrack.client;

import com.wetrack.client.test.MessageResponseHelper;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChatMemberRemoveTest extends WeTrackClientTest {

    private String dummyChatId = "12346523";

    private MessageResponseHelper helper = new MessageResponseHelper(200);

    @Test
    public void testChatMemberRemoveRequestFormat() throws InterruptedException {
        MockResponse response = new MockResponse().setResponseCode(200)
                .setBody(readResource("test_chat_member_delete/200.json"));
        server.enqueue(response);

        client.removeChatMember(dummyChatId, dummyToken, robertPeng.getUsername(), helper.callback());
        String expectedPath = "/chats/" + dummyChatId + "/members/" + robertPeng.getUsername() + "?token=" + dummyToken;
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("DELETE"));
        assertThat(request.getPath(), is(expectedPath));
        assertThat(request.getBody().readUtf8().isEmpty(), is(true));
    }

}
