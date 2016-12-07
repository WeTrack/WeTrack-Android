package com.wetrack.client;

import com.google.gson.Gson;
import com.wetrack.model.User;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;

import okhttp3.mockwebserver.MockWebServer;
import rx.schedulers.Schedulers;

public abstract class WeTrackClientTest {
    protected MockWebServer server;
    protected NetworkClient client;
    protected Gson gson;

    protected String dummyToken = "1234567890abcdef1234567890abcdef";
    protected User robertPeng = new User("robert-peng", "robert-peng", "Robert Peng");
    protected User windyChan = new User("windy-chan", "windy-chan", "Windy Chan");
    protected User mrDai = new User("mr-dai", "mr-dai", "Mr.Dai");
    protected User littleHearth = new User("little-hearth", "little-hearth", "Little Hearth");

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        client = new NetworkClient(server.url("/").toString(), 3, Schedulers.immediate(), Schedulers.immediate());

        try {
            Field gsonField = NetworkClient.class.getDeclaredField("gson");
            if (!gsonField.isAccessible())
                gsonField.setAccessible(true);
            gson = (Gson) gsonField.get(client);
        } catch (NoSuchFieldException ex) {
            throw new AssertionError("Cannot find field `gson` in class `NetworkClient`.");
        } catch (IllegalAccessException ex) {
            throw new AssertionError("Failed to access `gson` field of `NetworkClient`.");
        }
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    protected String readResource(String fileName) {
        URL resourceUrl = getClass().getClassLoader().getResource(fileName);
        if (resourceUrl == null)
            throw new AssertionError("Failed to find `" + fileName + "` in resources folder. Check if it is deleted.");
        try {
            return FileUtils.readFileToString(new File(resourceUrl.toURI()), Charsets.UTF_8);
        } catch (URISyntaxException ex) {
            AssertionError e = new AssertionError("Failed to convert URL `" + resourceUrl.toString() + "` to URI.");
            e.initCause(ex);
            throw e;
        } catch (IOException ex) {
            AssertionError e = new AssertionError("Failed to read from `" + resourceUrl.toString() + "`.");
            e.initCause(ex);
            throw e;
        }
    }
}
