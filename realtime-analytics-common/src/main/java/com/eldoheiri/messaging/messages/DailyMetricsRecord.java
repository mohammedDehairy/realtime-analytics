package com.eldoheiri.messaging.messages;

public class DailyMetricsRecord {
    private int averageSessionsPerDevice;
    private int totalSessions;
    private int activeDevices;
    private final String applicationId;
    private final long timestamp;

    public DailyMetricsRecord(long timestamp, String applicationId) {
        this.timestamp = timestamp;
        this.applicationId = applicationId;
    }

    public void setAverageSessionsPerDevice(int averageSessionsPerDevice) {
        this.averageSessionsPerDevice = averageSessionsPerDevice;
    }

    public int getAverageSessionsPerDevice() {
        return averageSessionsPerDevice;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public void setActiveDevices(int activeDevices) {
        this.activeDevices = activeDevices;
    }

    public int getActiveDevices() {
        return activeDevices;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "DailyMetricsRecord{" +
                "averageSessionsPerDevice=" + averageSessionsPerDevice +
                ", totalSessions=" + totalSessions +
                ", activeDevices=" + activeDevices +
                ", applicationId='" + applicationId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
