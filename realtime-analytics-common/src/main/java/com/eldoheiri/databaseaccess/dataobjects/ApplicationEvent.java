package com.eldoheiri.databaseaccess.dataobjects;

import java.sql.Timestamp;
import java.util.Map;

import com.eldoheiri.datastore.annotations.DataBaseTable;
import com.eldoheiri.datastore.annotations.DatabaseColumn;

@DataBaseTable(tableName = "application_events", primaryKeyColumn = "id")
public class ApplicationEvent {
    @DatabaseColumn(columnName = "id")
    private Integer id;

    @DatabaseColumn(columnName = "session_id")
    private Integer sessionId;

    @DatabaseColumn(columnName = "time_stamp")
    private Timestamp timestamp;

    @DatabaseColumn(columnName = "type", castToTypeName = "application_event_type")
    private String type;

    @DatabaseColumn(columnName = "payload", castToTypeName = "JSONB")
    private Map<String, Object> payload;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String,Object> getPayload() {
        return this.payload;
    }

    public void setPayload(Map<String,Object> payload) {
        this.payload = payload;
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", sessionId='" + getSessionId() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", type='" + getType() + "'" +
            ", payload='" + getPayload() + "'" +
            "}";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((payload == null) ? 0 : payload.hashCode());
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
        ApplicationEvent other = (ApplicationEvent) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (sessionId == null) {
            if (other.sessionId != null)
                return false;
        } else if (!sessionId.equals(other.sessionId))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (payload == null) {
            if (other.payload != null)
                return false;
        } else if (!payload.equals(other.payload))
            return false;
        return true;
    }
    
}
