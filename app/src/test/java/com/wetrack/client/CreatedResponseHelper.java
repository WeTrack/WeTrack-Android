package com.wetrack.client;

import com.wetrack.client.CreatedMessageCallback;

public class CreatedResponseHelper {

    private boolean successful;
    private int receivedStatusCode;
    private String receivedNewEntityId;
    private String receivedMessage;
    private Throwable receivedException;

    public CreatedMessageCallback callback() {
        return new CreatedMessageCallback() {
            @Override
            protected void onSuccess(String newEntityId, String message) {
                successful = true;
                receivedStatusCode = 201;
                receivedNewEntityId = newEntityId;
                receivedMessage = message;
                receivedException = null;
            }

            @Override
            protected void onFail(String message, int failedStatusCode) {
                successful = false;
                receivedStatusCode = failedStatusCode;
                receivedNewEntityId = null;
                receivedMessage = message;
                receivedException = null;
            }

            @Override
            protected void onError(Throwable ex) {
                successful = false;
                receivedStatusCode = -1;
                receivedNewEntityId = null;
                receivedMessage = null;
                receivedException = ex;
            }
        };
    }

    public void assertReceivedSuccessfulMessage() {
        if (!successful) {
            if (receivedMessage != null)
                throw new AssertionError("Expected to be successful, but failed with status code `"
                        + receivedStatusCode + "` and message `" + receivedMessage + "`.");
            else if (receivedException != null)
                throw new AssertionError("Expected to be successful, but failed with exception `" +
                        receivedException.getClass().getName() + ": " + receivedException.getMessage(), receivedException);
            else
                throw new AssertionError("Expected to be successful, but failed.");
        }
        assertReceivedMessage();
    }

    public void assertReceivedSuccessfulMessage(String expectedNewEntityId) {
        if (!successful) {
            if (receivedMessage != null)
                throw new AssertionError("Expected to be successful, but failed with status code `"
                        + receivedStatusCode + "` and message `" + receivedMessage + "`.");
            else if (receivedException != null)
                throw new AssertionError("Expected to be successful, but failed with exception `" +
                        receivedException.getClass().getName() + ": " + receivedException.getMessage(), receivedException);
            else
                throw new AssertionError("Expected to be successful, but failed.");
        } else if (!receivedNewEntityId.equals(expectedNewEntityId))
            throw new AssertionError("Expected to receive new entity id `" + expectedNewEntityId
                    + "`, but received `" + receivedNewEntityId + "`.");
        assertReceivedMessage();
    }

    public void assertReceivedFailedMessage(int expectedStatusCode) {
        if (successful)
            throw new AssertionError("Expected to be failed, but succeeded with status code `"
                    + receivedStatusCode + "` and message `" + receivedMessage + "`.");
        if (receivedStatusCode != expectedStatusCode)
            throw new AssertionError("Expected to receive status code `" + expectedStatusCode
                    + "`, but received status code `" + receivedStatusCode
                    + "` and message `" + receivedMessage + "`.");
        assertReceivedMessage();
    }

    private void assertReceivedMessage() {
        if (receivedMessage == null) {
            if (receivedException != null)
                throw new AssertionError("Expected to receive message, but received exception " +
                        receivedException.getClass().getName() + ": " + receivedException.getMessage(), receivedException);
            else
                throw new AssertionError("Expected to receive message, but received nothing.");
        }
    }

    public boolean isSuccessful() {
        return successful;
    }

    public int getReceivedStatusCode() {
        return receivedStatusCode;
    }

    public String getReceivedNewEntityId() {
        return receivedNewEntityId;
    }

    public String getReceivedMessage() {
        return receivedMessage;
    }

    public Throwable getReceivedException() {
        return receivedException;
    }
}
