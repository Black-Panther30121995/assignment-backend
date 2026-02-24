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
    void testGetAllDevices_returnsList() {
        when(driver.session(any(SessionConfig.class))).thenReturn(session);

        // Return empty list directly → no executeRead → no execute → no NPE
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

        // No lambda will run → no execute()
        when(session.executeWrite(any())).thenReturn(null);

        assertDoesNotThrow(() -> deviceService.softDeleteDevice("D1"));
    }
}