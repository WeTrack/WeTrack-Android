package com.wetrack.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.wetrack.client.test.CreatedResponseHelper;
import com.wetrack.model.Chat;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChatCreateTest extends WeTrackClientTest {

    private CreatedResponseHelper messageHelper = new CreatedResponseHelper();

    @Test
    public void testChatCreateRequestFormat() throws InterruptedException {
        MockResponse response = new MockResponse().setResponseCode(201)
                .setBody(readResource("test_chat_create/201.json"));
        server.enqueue(response);
        server.enqueue(response);

        Chat chat = new Chat("Chat chat");
        chat.getMemberNames().add(robertPeng.getUsername());
        chat.getMemberNames().add(windyChan.getUsername());

        client.createChat(dummyToken, chat, messageHelper.callback());
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is("/chats?token=" + dummyToken));
        JsonObject requestEntity = gson.fromJson(request.getBody().readUtf8(), JsonObject.class);
        assertThat(requestEntity.get("name").getAsString(), is(chat.getName()));
        assertThat(requestEntity.get("members").getAsJsonArray().contains(new JsonPrimitive(robertPeng.getUsername())), is(true));
        assertThat(requestEntity.get("members").getAsJsonArray().contains(new JsonPrimitive(windyChan.getUsername())), is(true));
        assertThat(requestEntity.has("chatId"), is(false));
    }
}
