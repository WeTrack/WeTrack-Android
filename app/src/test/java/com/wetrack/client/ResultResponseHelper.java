package com.wetrack.client;

public class ResultResponseHelper {

    private final int successfulStatusCode;

    private boolean successful;
    private int receivedStatusCode;
    private Throwable receivedException;

    public ResultResponseHelper(int successfulStatusCode) {
        this.successfulStatusCode = successfulStatusCode;
    }

    public void assertSucceeded() {
        if (!successful) {
            if (receivedException != null)
                throw new AssertionError("Expected to be successful, but failed with exception " +
                    receivedException.getClass().getName() + ": " + receivedException.getMessage());
            else
                throw new AssertionError("Expected to be successful, but failed with status code `"
                        + receivedStatusCode + "`.");
        }
    }

    public void assertFailed(int expectedStatusCode) {
        if (successful)
            throw new AssertionError("Expected to be failed, but succeeded with status code `"
                    + receivedStatusCode + "`.");
        if (receivedStatusCode != expectedStatusCode)
            throw new AssertionError("Expected to receive status code `" + expectedStatusCode
                    + "`, but received `" + receivedStatusCode + "`");
    }

    public ResultCallback callback() {
        return new ResultCallback(successfulStatusCode) {
            @Override
            protected void onSuccess() {
                successful = true;
                receivedStatusCode = successfulStatusCode;
                receivedException = null;
            }

            @Override
            protected void onFail(int failedStatusCode) {
                successful = false;
                receivedStatusCode = failedStatusCode;
                receivedException = null;
            }

            @Override
            protected void onError(Throwable ex) {
                successful = false;
                receivedStatusCode = -1;
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

    public Throwable getReceivedException() {
        return receivedException;
    }
}
