package com.eldoheiri.realtime_analytics.dataobjects;

import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

import com.eldoheiri.realtime_analytics.dataobjects.events.HeartBeatDTO;

public class SessionDTO {
    private Integer id;

    private Integer applicationId;

    private String token;

    @NotNull
    private Integer deviceId;

    private Timestamp createAt;

    private HeartBeatDTO heartBeat;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public HeartBeatDTO getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(HeartBeatDTO heartBeat) {
        this.heartBeat = heartBeat;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((applicationId == null) ? 0 : applicationId.hashCode());
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
        result = prime * result + ((createAt == null) ? 0 : createAt.hashCode());
        result = prime * result + ((heartBeat == null) ? 0 : heartBeat.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SessionDTO other = (SessionDTO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (applicationId == null) {
            if (other.applicationId != null)
                return false;
        } else if (!applicationId.equals(other.applicationId))
            return false;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        if (deviceId == null) {
            if (other.deviceId != null)
                return false;
        } else if (!deviceId.equals(other.deviceId))
            return false;
        if (createAt == null) {
            if (other.createAt != null)
                return false;
        } else if (!createAt.equals(other.createAt))
            return false;
        if (heartBeat == null) {
            if (other.heartBeat != null)
                return false;
        } else if (!heartBeat.equals(other.heartBeat))
            return false;
        return true;
    }
}
