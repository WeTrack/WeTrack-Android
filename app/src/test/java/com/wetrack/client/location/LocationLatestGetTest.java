package com.wetrack.client.location;

import com.wetrack.client.EntityResponseHelper;
import com.wetrack.client.WeTrackClientTest;
import com.wetrack.model.Location;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class LocationLatestGetTest extends WeTrackClientTest {

    private EntityResponseHelper<Location> entityHelper = new EntityResponseHelper<>(gson);

    private String username = "robert-peng";

    @Test
    public void testLocationLatestGetRequestFormat() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(200));
        client.getUserLatestLocation(username, entityHelper.callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("GET"));
        assertThat(request.getPath(), is("/users/" + username + "/locations/latest"));
    }

    @Test
    public void testLocationLatestGetOnOkResponse() {
        String testResponse = readResource("test_location_latest_get/200.json");
        server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));
        client.getUserLatestLocation(username, entityHelper.callback(200));

        entityHelper.assertReceivedEntity(200);
        Location receivedLocation = entityHelper.getReceivedEntity();
        Location actualLocation = gson.fromJson(testResponse, Location.class);

        assertThat(receivedLocation.getUsername(), is(actualLocation.getUsername()));
        assertThat(receivedLocation.getLongitude(), is(actualLocation.getLongitude()));
        assertThat(receivedLocation.getLatitude(), is(actualLocation.getLatitude()));
        assertThat(receivedLocation.getTime(), is(actualLocation.getTime()));
    }

    @Test
    public void testLocationLatestGetOnErrorResponse() {
        server.enqueue(new MockResponse().setResponseCode(404)
                .setBody(readResource("test_location_latest_get/404.json")));
        client.getUserLatestLocation(username, entityHelper.callback(200));

        entityHelper.assertReceivedErrorMessage(404);
    }
}
