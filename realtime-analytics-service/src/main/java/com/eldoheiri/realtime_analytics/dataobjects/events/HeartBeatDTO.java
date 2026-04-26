package com.eldoheiri.realtime_analytics.dataobjects.events;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.DateTimeFormat;

import com.eldoheiri.realtime_analytics.dataobjects.validators.IsoLocale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public final class HeartBeatDTO {
    private String id;

    @DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    private Timestamp timestamp;

    @NotBlank
    private String applicationVersion;

    @NotNull
    private String releaseChannel;

    @NotBlank
    @Pattern(regexp = "(?i)^(ios|android|web)$")
    private String platform;

    @NotBlank
    private String osVersion;

    @NotBlank
    private String deviceModel;

    @NotBlank
    private String deviceBrand;

    @NotNull
    @IsoLocale
    private String deviceLocale;

    @UniqueElements
    @NotNull
    @Valid
    private List<ApplicationEventDTO> events;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getApplicationVersion() {
        return this.applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public String getReleaseChannel() {
        return this.releaseChannel;
    }

    public void setReleaseChannel(String releaseChannel) {
        this.releaseChannel = releaseChannel;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceModel() {
        return this.deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceBrand() {
        return this.deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getDeviceLocale() {
        return this.deviceLocale;
    }

    public void setDeviceLocale(String deviceLocale) {
        this.deviceLocale = deviceLocale;
    }


    public List<ApplicationEventDTO> getEvents() {
        if (this.events == null) {
            return new ArrayList<>();
        }
        return this.events;
    }

    public void setEvents(List<ApplicationEventDTO> events) {
        this.events = events;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((applicationVersion == null) ? 0 : applicationVersion.hashCode());
        result = prime * result + ((releaseChannel == null) ? 0 : releaseChannel.hashCode());
        result = prime * result + ((platform == null) ? 0 : platform.hashCode());
        result = prime * result + ((osVersion == null) ? 0 : osVersion.hashCode());
        result = prime * result + ((deviceModel == null) ? 0 : deviceModel.hashCode());
        result = prime * result + ((deviceBrand == null) ? 0 : deviceBrand.hashCode());
        result = prime * result + ((deviceLocale == null) ? 0 : deviceLocale.hashCode());
        result = prime * result + ((events == null) ? 0 : events.hashCode());
        return result;
    }
    

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", applicationVersion='" + getApplicationVersion() + "'" +
            ", releaseChannel='" + getReleaseChannel() + "'" +
            ", platform='" + getPlatform() + "'" +
            ", osVersion='" + getOsVersion() + "'" +
            ", deviceModel='" + getDeviceModel() + "'" +
            ", deviceBrand='" + getDeviceBrand() + "'" +
            ", deviceLocale='" + getDeviceLocale() + "'" +
            ", events='" + getEvents() + "'" +
            "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HeartBeatDTO other = (HeartBeatDTO) obj;
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
        if (applicationVersion == null) {
            if (other.applicationVersion != null)
                return false;
        } else if (!applicationVersion.equals(other.applicationVersion))
            return false;
        if (releaseChannel == null) {
            if (other.releaseChannel != null)
                return false;
        } else if (!releaseChannel.equals(other.releaseChannel))
            return false;
        if (platform == null) {
            if (other.platform != null)
                return false;
        } else if (!platform.equals(other.platform))
            return false;
        if (osVersion == null) {
            if (other.osVersion != null)
                return false;
        } else if (!osVersion.equals(other.osVersion))
            return false;
        if (deviceModel == null) {
            if (other.deviceModel != null)
                return false;
        } else if (!deviceModel.equals(other.deviceModel))
            return false;
        if (deviceBrand == null) {
            if (other.deviceBrand != null)
                return false;
        } else if (!deviceBrand.equals(other.deviceBrand))
            return false;
        if (deviceLocale == null) {
            if (other.deviceLocale != null)
                return false;
        } else if (!deviceLocale.equals(other.deviceLocale))
            return false;
        if (events == null) {
            if (other.events != null)
                return false;
        } else if (!events.equals(other.events))
            return false;
        return true;
    }
    
}
