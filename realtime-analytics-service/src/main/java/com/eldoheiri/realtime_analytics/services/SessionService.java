package com.eldoheiri.realtime_analytics.services;

import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.eldoheiri.datastore.DataStore;
import com.eldoheiri.datastore.sqlpredicatebuilder.basetypes.concretebuilders.SQLPredicateBuilder;
import com.eldoheiri.databaseaccess.DataSource;
import com.eldoheiri.databaseaccess.dataobjects.Session;
import com.eldoheiri.realtime_analytics.dataobjects.SessionDTO;
import com.eldoheiri.realtime_analytics.dataobjects.events.ApplicationEventDTO;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.heartbeat.HeartBeatException;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.session.SessionException;
import com.eldoheiri.messaging.messages.HeartBeatMessage;
import com.eldoheiri.messaging.dataobjects.ApplicationEvent;
import com.eldoheiri.realtime_analytics.security.authentication.JWTUtil;

public class SessionService {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private HeartBeatService heartBeatService;

    public SessionDTO createNew(SessionDTO sessionRequest, String applicationId) {
        try (Connection dbConnection = DataSource.getConnection()) {

            DataStore dataStore = new DataStore();
            Session session = new Session();
            session.setApplicationId(Integer.parseInt(applicationId));
            session.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            session.setDeviceId(Integer.parseInt(sessionRequest.getDeviceId()));

            // List<ApplicationEvent> applicationEvents =
            // DataFactory.createApplicationEvents(sessionRequest.getHeartBeat(), null);
            // ApplicationEvent heartBeat =
            // DataFactory.createHeartBeatEvent(sessionRequest.getHeartBeat(), null);
            // if (heartBeat != null) {
            // applicationEvents.add(heartBeat);
            // }
            // session.setEvents(applicationEvents);
            dataStore.insert(session, dbConnection);

            dbConnection.commit();

            SessionDTO response = new SessionDTO();
            response.setId(session.getId().toString());
            response.setApplicationId(applicationId.toString());
            Date tokenExpiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 3);
            response.setToken(jwtUtil.generateTokenString(session.getId().toString(), tokenExpiration));
            response.setDeviceId(sessionRequest.getDeviceId());
            response.setCreateAt(session.getCreatedAt());

            if (sessionRequest.getHeartBeat() != null) {
                heartBeatService.heartBeatRecieved(sessionRequest.getHeartBeat(), session.getId().toString(), applicationId);
            }

            // if (sessionRequest.getHeartBeat() != null && heartBeat != null) {
            // response.setHeartBeat(sessionRequest.getHeartBeat());
            // response.getHeartBeat().setId(heartBeat.getId());
            // }
            // if (sessionRequest.getHeartBeat() != null &&
            // sessionRequest.getHeartBeat().getEvents() != null) {
            // for (int i = 0; i < sessionRequest.getHeartBeat().getEvents().size(); i++) {
            // response.getHeartBeat().getEvents().get(i).setId(applicationEvents.get(i).getId());
            // }
            // }
            return response;
        } catch (IllegalArgumentException | SQLException | HeartBeatException e) {
            e.printStackTrace();
            throw new SessionException(e);
        } catch (SessionException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public int cleanup() {
        try (Connection dbConnection = DataSource.getConnection()) {
            DataStore dataStore = new DataStore();
            int deletedRows = dataStore.delete(SQLPredicateBuilder.newPredicate().attribute("created_at").lessThan()
                    .expression("NOW() - INTERVAL '1 week'"), Session.class, dbConnection);
            dbConnection.commit();
            return deletedRows;
        } catch (IllegalArgumentException | SQLException e) {
            e.printStackTrace();
            throw new SessionException(e);
        } catch (SessionException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
