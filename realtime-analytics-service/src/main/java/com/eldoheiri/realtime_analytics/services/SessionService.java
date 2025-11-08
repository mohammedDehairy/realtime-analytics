package com.eldoheiri.realtime_analytics.services;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.eldoheiri.realtime_analytics.dataobjects.SessionDTO;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.heartbeat.HeartBeatException;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.session.SessionException;
import com.eldoheiri.messaging.dataobjects.Application;
import com.eldoheiri.realtime_analytics.security.authentication.JWTUtil;
import com.eldoheiri.realtime_analytics.security.idgeneration.IdentifierUtil;

public class SessionService {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private IdentifierUtil identifierUtil;

    @Autowired
    private HeartBeatService heartBeatService;

    public SessionDTO createNew(SessionDTO sessionRequest, String applicationId) {
        if (Application.fromId(applicationId) == null) {
            throw new SessionException("Application not found");
        }

        try {
            if (!identifierUtil.validateIdentifier(applicationId, sessionRequest.getDeviceId())) {
                throw new SessionException("Invalid device id");
            }

            SessionDTO response = new SessionDTO();
            String sessionId = identifierUtil.generateId(applicationId);
            response.setId(sessionId);
            response.setApplicationId(applicationId.toString());
            Date tokenExpiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 3);
            response.setToken(jwtUtil.generateTokenString(sessionId, tokenExpiration));
            response.setDeviceId(sessionRequest.getDeviceId());
            response.setCreateAt(new Timestamp(System.currentTimeMillis()));
            response.setHeartBeat(sessionRequest.getHeartBeat());

            if (sessionRequest.getHeartBeat() != null) {
                sessionRequest.getHeartBeat().setDeviceId(sessionRequest.getDeviceId());
                heartBeatService.heartBeatRecieved(sessionRequest.getHeartBeat(), sessionRequest.getDeviceId(), sessionId, applicationId);
            }
            return response;
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new SessionException("Failed to validate device id", e);
        } catch (SessionException e) {
            e.printStackTrace();
            throw e;
        } catch (HeartBeatException e) {
            e.printStackTrace();
            throw new SessionException("Failed to process heartbeat", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SessionException(e);
        }
    }
}
