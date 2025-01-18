package com.eldoheiri.realtime_analytics.services;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.eldoheiri.messaging.dataobjects.Application;
import com.eldoheiri.realtime_analytics.dataobjects.DeviceDTO;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.DeviceException;
import com.eldoheiri.realtime_analytics.security.idgeneration.IdentifierUtil;

import jakarta.validation.Valid;

public class DeviceService {

    @Autowired
    private IdentifierUtil identifierUtil;

    public DeviceDTO newDevice(@Valid @RequestBody DeviceDTO deviceDTO, @PathVariable String applicationId) {
        if (Application.fromId(applicationId) == null) {
            throw new DeviceException("Application not found");
        }
        try {
            deviceDTO.setId(identifierUtil.generateId(applicationId));
            deviceDTO.setApplicationId(applicationId);
            return deviceDTO;
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new DeviceException("Failed to generate device id", e);
        }
    }
}
