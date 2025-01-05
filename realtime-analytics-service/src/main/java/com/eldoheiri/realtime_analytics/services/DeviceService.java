package com.eldoheiri.realtime_analytics.services;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.eldoheiri.datastore.DataStore;
import com.eldoheiri.databaseaccess.DataSource;
import com.eldoheiri.databaseaccess.dataobjects.Device;
import com.eldoheiri.realtime_analytics.dataobjects.DeviceDTO;
import com.eldoheiri.realtime_analytics.exceptionhandling.Exceptions.DeviceException;

import jakarta.validation.Valid;

public class DeviceService {

    public DeviceDTO newDevice(@Valid @RequestBody DeviceDTO deviceDTO, @PathVariable Integer applicationId) {
        try (Connection dbConnection = DataSource.getConnection()) {
            DataStore dataStore = new DataStore();
            Device device = new Device();
            device.setModel(deviceDTO.getModel());
            device.setApplicationId(applicationId);
            dataStore.insert(device, dbConnection);
            dbConnection.commit();

            deviceDTO.setId(device.getId().toString());
            deviceDTO.setApplicationId(applicationId.toString());
            return deviceDTO;
        } catch (IllegalArgumentException | SQLException e) {
            e.printStackTrace();
            throw new DeviceException(e);
        } catch (DeviceException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
