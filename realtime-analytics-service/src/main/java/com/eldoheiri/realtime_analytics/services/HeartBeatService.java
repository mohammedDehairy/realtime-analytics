package com.eldoheiri.realtime_analytics.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.eldoheiri.datastore.DataStore;
import com.eldoheiri.databaseaccess.DataSource;
import com.eldoheiri.realtime_analytics.dataobjects.events.ApplicationEventDTO;
import com.eldoheiri.realtime_analytics.dataobjects.events.HeartBeatDTO;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.heartbeat.HeartBeatException;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.messagequeue.MessageQueueException;
import com.eldoheiri.realtime_analytics.kafka.producer.MessageQueue;
import com.eldoheiri.messaging.messages.HeartBeatMessage;
import com.eldoheiri.messaging.dataobjects.ApplicationEvent;

import jakarta.validation.Valid;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HeartBeatService {

    @Autowired
    private MessageQueue<HeartBeatMessage> heartBeatMessageQueue;

    public void send(HeartBeatMessage message) throws HeartBeatException {
        try {
            heartBeatMessageQueue.send(message);
        } catch (MessageQueueException e) {
            e.printStackTrace();
            throw new HeartBeatException(e);
        }
    }

    public void heartBeatRecieved(HeartBeatDTO sessionHeartBeat, String sessionId, String applicationId) throws HeartBeatException {
        List<ApplicationEvent> applicationEvents = new ArrayList<>();
        for (ApplicationEventDTO eventDTO : sessionHeartBeat.getEvents()) {
            ApplicationEvent applicationEvent = new ApplicationEvent();
            applicationEvent.setTimestamp(eventDTO.getTimestamp().getTime());
            applicationEvent.setType(eventDTO.getType());
            applicationEvent.setAttributes(eventDTO.getAttributes());
            applicationEvents.add(applicationEvent);
        }
        HeartBeatMessage heartBeatMessage = new HeartBeatMessage();
        heartBeatMessage.setSessionId(sessionId.toString());
        heartBeatMessage.setApplicationId(applicationId.toString());
        heartBeatMessage.setDeviceId(sessionHeartBeat.getDeviceId());
        heartBeatMessage.setEvents(applicationEvents);
        send(heartBeatMessage);
    }

    public HeartBeatDTO insertHeartBeat(@Valid @RequestBody HeartBeatDTO sessionHeartBeat, @PathVariable Integer sessionId) {
        try (Connection dbConnection = DataSource.getConnection()) {
            HeartBeatDTO result = insert(sessionHeartBeat, sessionId, dbConnection);
            dbConnection.commit();
            return result;
        } catch (IllegalArgumentException | SQLException e) {
            e.printStackTrace();
            throw new HeartBeatException(e);
        } catch (HeartBeatException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private HeartBeatDTO insert(HeartBeatDTO sessionHeartBeat, Integer sessionId, Connection dbConnection) throws SQLException {
        DataStore dataStore = new DataStore();
        com.eldoheiri.databaseaccess.dataobjects.ApplicationEvent heartBeat = DataFactory.createHeartBeatEvent(sessionHeartBeat, sessionId);
        List<com.eldoheiri.databaseaccess.dataobjects.ApplicationEvent> applicationEvents = DataFactory.createApplicationEvents(sessionHeartBeat, sessionId);
        dataStore.insert(heartBeat, dbConnection);
        dataStore.insert(applicationEvents, dbConnection);
        sessionHeartBeat.setId(heartBeat.getId().toString());
        if (sessionHeartBeat.getEvents() == null || sessionHeartBeat.getEvents().isEmpty()) {
            return sessionHeartBeat;
        }
        for (int i = 0; i < sessionHeartBeat.getEvents().size(); i++) {
            sessionHeartBeat.getEvents().get(i).setId(applicationEvents.get(i).getId().toString());
        }
        return sessionHeartBeat;
    }
}
