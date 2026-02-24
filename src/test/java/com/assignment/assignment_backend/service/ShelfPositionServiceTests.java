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
class ShelfPositionServiceTests {

    @Mock Driver driver;
    @Mock Session session;

    private ShelfPositionService shelfPositionService;

    @BeforeEach
    void setUp() {
        shelfPositionService = new ShelfPositionService(driver, "neo4j");
    }

    @Test
    void testListPositionsByDevice_returnsList() {
        when(driver.session(any(SessionConfig.class))).thenReturn(session);

        // Simple empty list, bypassing execute()
        when(session.executeRead(any())).thenReturn(Collections.emptyList());

        assertNotNull(shelfPositionService.listPositionsByDevice("D1"));
    }

    @Test
    void testGetShelfPosition_throwsWhenNull() {
        assertThrows(NullPointerException.class,
            () -> shelfPositionService.getShelfPosition(null));
    }

    @Test
    void testSoftDelete_noErrors() {
        when(driver.session(any(SessionConfig.class))).thenReturn(session);
        when(session.executeWrite(any())).thenReturn(null);
        assertDoesNotThrow(() -> shelfPositionService.softDeleteShelfPosition("SP1"));
    }

    @Test
    void testCreateShelfPosition_invalidArgs() {
        assertThrows(NullPointerException.class,
            () -> shelfPositionService.createShelfPosition(null, 1));

        assertThrows(IllegalArgumentException.class,
            () -> shelfPositionService.createShelfPosition("D1", 0));
    }
}