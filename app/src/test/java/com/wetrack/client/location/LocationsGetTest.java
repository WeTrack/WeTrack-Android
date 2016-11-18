package com.wetrack.client.location;

import com.google.gson.reflect.TypeToken;
import com.wetrack.client.EntityResponseHelper;
import com.wetrack.client.WeTrackClientTest;
import com.wetrack.model.Location;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class LocationsGetTest extends WeTrackClientTest {

    private EntityResponseHelper<List<Location>> entityHelper = new EntityResponseHelper<>(gson);

    private String username = "robert-peng";
    private LocalDateTime sinceTime = LocalDateTime.parse("2016-10-24T12:00:00.000");

    @Test
    public void testLocationsGetRequestFormat() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(200));
        client.getUserLocationsSince(username, sinceTime, entityHelper.callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("GET"));
        assertThat(request.getPath(), is("/users/" + username + "/locations?since=" + sinceTime.toString()));
    }

    @Test
    public void testLocationsGetOnOkResponse() {
        String testResponse = readResource("test_locations_get/200.json");
        server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));
        client.getUserLocationsSince(username, sinceTime, entityHelper.callback(200));

        entityHelper.assertReceivedEntity(200);
        List<Location> receivedLocations = entityHelper.getReceivedEntity();
        List<Location> actualLocations = gson.fromJson(testResponse, new TypeToken<List<Location>>(){}.getType());

        assertThat(receivedLocations.size(), is(actualLocations.size()));
        for (int i = 0; i < receivedLocations.size(); i++) {
            Location receivedLocation = receivedLocations.get(i);
            Location actualLocation = actualLocations.get(i);
            assertThat(receivedLocation.getLatitude(), is(actualLocation.getLatitude()));
            assertThat(receivedLocation.getLongitude(), is(actualLocation.getLongitude()));
            assertThat(receivedLocation.getTime(), is(actualLocation.getTime()));
        }
    }

}
