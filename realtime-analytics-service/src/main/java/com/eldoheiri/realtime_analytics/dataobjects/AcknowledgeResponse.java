package com.eldoheiri.realtime_analytics.dataobjects;

public final class AcknowledgeResponse {
    private String result;

    public AcknowledgeResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
