package com.eldoheiri.databaseaccess.dataobjects;

import com.eldoheiri.datastore.annotations.DataBaseTable;
import com.eldoheiri.datastore.annotations.DatabaseColumn;
import com.eldoheiri.datastore.annotations.TableRelation;

import java.util.List;

@DataBaseTable(tableName = "devices", primaryKeyColumn = "id")
public class Device {
    @DatabaseColumn(columnName = "id")
    private Integer id;

    @DatabaseColumn(columnName = "application_id")
    private Integer applicationId;

    @DatabaseColumn(columnName = "model")
    private String model;

    @TableRelation(exportedForeignKeyName = "device_id")
    private List<Session> sessions;

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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((applicationId == null) ? 0 : applicationId.hashCode());
        result = prime * result + ((model == null) ? 0 : model.hashCode());
        result = prime * result + ((sessions == null) ? 0 : sessions.hashCode());
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
        Device other = (Device) obj;
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
        if (model == null) {
            if (other.model != null)
                return false;
        } else if (!model.equals(other.model))
            return false;
        if (sessions == null) {
            if (other.sessions != null)
                return false;
        } else if (!sessions.equals(other.sessions))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", applicationId='" + getApplicationId() + "'" +
            ", model='" + getModel() + "'" +
            ", sessions='" + getSessions() + "'" +
            "}";
    }
}
