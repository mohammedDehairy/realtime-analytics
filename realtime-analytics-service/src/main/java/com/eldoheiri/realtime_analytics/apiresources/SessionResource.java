package com.eldoheiri.realtime_analytics.apiresources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eldoheiri.realtime_analytics.dataobjects.SessionDTO;
import com.eldoheiri.realtime_analytics.dataobjects.events.HeartBeatDTO;
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
    public SessionDTO newSession(@Valid @RequestBody SessionDTO sessionRequest, @PathVariable Integer applicationId) {
        return sessionService.createNew(sessionRequest, applicationId);
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> cleanup() {
        int deletedRows = sessionService.cleanup();
        if (deletedRows < 1) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    

    @PostMapping("/{sessionId}/heartBeat")
    public HeartBeatDTO heartBeat(@Valid @RequestBody HeartBeatDTO sessionHeartBeat, @PathVariable Integer sessionId) {
        return heartBeatService.heartBeat(sessionHeartBeat, sessionId);
    }
}
