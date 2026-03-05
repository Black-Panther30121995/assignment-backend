package com.assignment.assignment_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import com.assignment.assignment_backend.entity.Device;

import com.assignment.assignment_backend.requests.CreateDeviceRequest;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTests {

    @Mock Driver driver;
    @Mock Session session;

    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
        deviceService = new DeviceService(driver, "neo4j");
    }
    
    @Test
    void createDevice_doesNotThrow_andReturnsDevice() {
    	when(driver.session(any(SessionConfig.class))).thenReturn(session);
        Device stubDevice = new Device(
            "D-1","Core-01","PN-1","B1","Router",3,false
        );
        when(session.executeWrite(any())).thenReturn(stubDevice);

        var req = new CreateDeviceRequest("Core-01","PN-1","B1","Router",3);

        assertDoesNotThrow(() -> {
            var result = deviceService.createDevice(req);
            assertNotNull(result);
            assertEquals("D-1", result.getDeviceId());
        });
    }

    @Test
    void testGetAllDevices_returnsList() {
        when(driver.session(any(SessionConfig.class))).thenReturn(session);


        when(session.executeRead(any())).thenReturn(Collections.emptyList());

        assertNotNull(deviceService.getAllDevices());
    }

    @Test
    void testGetDeviceView_throwsWhenIdNull() {
        assertThrows(NullPointerException.class,
            () -> deviceService.getDeviceView(null));
    }

    @Test
    void testUpdateDevice_throwsWhenRequestNull() {
        assertThrows(NullPointerException.class,
            () -> deviceService.updateDevice("D1", null));
    }

    @Test
    void testSoftDeleteDevice_doesNotThrow() {
        when(driver.session(any(SessionConfig.class))).thenReturn(session);

        when(session.executeWrite(any())).thenReturn(null);

        assertDoesNotThrow(() -> deviceService.softDeleteDevice("D1"));
    }
}



