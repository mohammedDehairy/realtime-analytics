package com.eldoheiri.realtime_analytics.apiresources;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eldoheiri.realtime_analytics.dataobjects.AcknowledgeResponse;
import com.eldoheiri.realtime_analytics.dataobjects.SessionDTO;
import com.eldoheiri.realtime_analytics.dataobjects.events.HeartBeatDTO;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.heartbeat.HeartBeatException;
import com.eldoheiri.realtime_analytics.services.HeartBeatService;
import com.eldoheiri.realtime_analytics.services.SessionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/{applicationId}/session")
public class SessionResource {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private HeartBeatService heartBeatService;
    
    @PostMapping
    public SessionDTO newSession(@Valid @RequestBody SessionDTO sessionRequest, @PathVariable String applicationId) {
        return sessionService.createNew(sessionRequest, applicationId);
    }

    @PostMapping("/{sessionId}/heartBeat")
    public AcknowledgeResponse heartBeat(@Valid @RequestBody HeartBeatDTO sessionHeartBeat, @PathVariable String sessionId, @PathVariable String applicationId) {
        try {
            heartBeatService.heartBeatRecieved(sessionHeartBeat, sessionHeartBeat.getDeviceId(), sessionId, applicationId);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new HeartBeatException("Failed to validate device id", e);
        } catch (HeartBeatException e) {
            e.printStackTrace();
            throw e;
        }
        return new AcknowledgeResponse("success");
    }
}
