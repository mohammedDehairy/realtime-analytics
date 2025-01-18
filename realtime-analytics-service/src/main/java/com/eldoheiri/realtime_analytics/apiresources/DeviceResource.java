package com.eldoheiri.realtime_analytics.apiresources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eldoheiri.realtime_analytics.dataobjects.DeviceDTO;
import com.eldoheiri.realtime_analytics.services.DeviceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/{applicationId}/device")
public class DeviceResource {

    @Autowired
    private DeviceService deviceService;

    @PostMapping
    public DeviceDTO newDevice(@Valid @RequestBody DeviceDTO deviceDTO, @PathVariable String applicationId) {
        return deviceService.newDevice(deviceDTO, applicationId);
    }
}
