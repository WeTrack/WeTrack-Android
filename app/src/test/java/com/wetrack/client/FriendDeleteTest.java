package com.wetrack.client;

import com.wetrack.client.test.MessageResponseHelper;
import com.wetrack.client.WeTrackClientTest;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FriendDeleteTest extends WeTrackClientTest {

    private MessageResponseHelper helper = new MessageResponseHelper(200);

    @Test
    public void testFriendDeleteRequestFormat() throws InterruptedException {
        MockResponse response = new MockResponse().setResponseCode(200)
                .setBody(readResource("test_friend_delete/200.json"));
        server.enqueue(response);

        String expectedPath = "/users/" + robertPeng.getUsername() + "/friends/" + mrDai.getUsername()
                + "?token=" + dummyToken;
        client.deleteFriend(robertPeng.getUsername(), dummyToken, mrDai.getUsername(), helper.callback());
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request.getMethod(), is("DELETE"));
        assertThat(request.getPath(), is(expectedPath));
        assertThat(request.getBody().readUtf8().isEmpty(), is(true));
    }
}
