package com.eldoheiri.realtime_analytics.dataobjects.events;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import com.eldoheiri.realtime_analytics.dataobjects.validators.EnumValidator;

import jakarta.validation.constraints.NotNull;

public class ApplicationEventDTO {
    private Integer id; 

    @DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    private Timestamp timestamp;

    @EnumValidator(
        enumClass = EventType.class,
        message = "The event type is not recognized"
    )
    private EventType type;

    private Map<String, Object> attributes;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    public EventType getType() {
        return this.type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Map<String,Object> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String,Object> payload) {
        this.attributes = payload;
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", type='" + getType() + "'" +
            ", payload='" + getAttributes() + "'" +
            "}";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
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
        ApplicationEventDTO other = (ApplicationEventDTO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
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
        if (attributes == null) {
            if (other.attributes != null)
                return false;
        } else if (!attributes.equals(other.attributes))
            return false;
        return true;
    }
    
}

