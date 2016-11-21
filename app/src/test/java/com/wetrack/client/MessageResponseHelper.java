package com.wetrack.client;

public class MessageResponseHelper {
    private final int successfulStatusCode;

    private boolean successful;
    private int receivedStatusCode;
    private String receivedMessage;
    private Throwable receivedException;

    public MessageResponseHelper(int successfulStatusCode) {
        this.successfulStatusCode = successfulStatusCode;
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

    public MessageCallback callback() {
        return new MessageCallback(successfulStatusCode) {
            @Override
            protected void onSuccess(String message) {
                successful = true;
                receivedStatusCode = successfulStatusCode;
                receivedMessage = message;
                receivedException = null;
            }

            @Override
            protected void onFail(String message, int failedStatusCode) {
                successful = false;
                receivedStatusCode = failedStatusCode;
                receivedMessage = message;
                receivedException = null;
            }

            @Override
            protected void onError(Throwable ex) {
                successful = false;
                receivedStatusCode = -1;
                receivedMessage = null;
                receivedException = ex;
            }
        };
    }

    public boolean isSuccessful() {
        return successful;
    }

    public int getReceivedStatusCode() {
        return receivedStatusCode;
    }

    public String getReceivedMessage() {
        return receivedMessage;
    }

    public Throwable getReceivedException() {
        return receivedException;
    }
}
