package com.eldoheiri.databaseaccess.dataobjects;

import com.eldoheiri.datastore.annotations.DataBaseTable;
import com.eldoheiri.datastore.annotations.DatabaseColumn;
import com.eldoheiri.datastore.annotations.TableRelation;

import java.util.List;
import java.sql.Timestamp;

@DataBaseTable(tableName = "sessions", primaryKeyColumn = "id")
public final class Session {
    @DatabaseColumn(columnName = "id")
    private Integer id;

    @DatabaseColumn(columnName = "application_id")
    private Integer applicationId;

    @DatabaseColumn(columnName = "device_id")
    private Integer deviceId;

    @DatabaseColumn(columnName = "created_at")
    private Timestamp createdAt;

    @TableRelation(exportedForeignKeyName = "session_id")
    private List<ApplicationEvent> events;

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

    public List<ApplicationEvent> getEvents() {
        return events;
    }

    public void setEvents(List<ApplicationEvent> events) {
        this.events = events;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((applicationId == null) ? 0 : applicationId.hashCode());
        result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
        result = prime * result + ((events == null) ? 0 : events.hashCode());
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
        Session other = (Session) obj;
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
        if (deviceId == null) {
            if (other.deviceId != null)
                return false;
        } else if (!deviceId.equals(other.deviceId))
            return false;
        if (events == null) {
            if (other.events != null)
                return false;
        } else if (!events.equals(other.events))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", applicationId='" + getApplicationId() + "'" +
            ", deviceId='" + getDeviceId() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", events='" + getEvents() + "'" +
            "}";
    }
    
}
