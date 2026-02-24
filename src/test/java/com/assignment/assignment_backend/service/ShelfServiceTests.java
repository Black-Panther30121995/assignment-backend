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
class ShelfServiceTests {

    @Mock Driver driver;
    @Mock Session session;

    private ShelfService shelfService;

    @BeforeEach
    void setUp() {
        shelfService = new ShelfService(driver, "neo4j");
    }

    @Test
    void testGetAllShelves_returnsList() {
        when(driver.session(any(SessionConfig.class))).thenReturn(session);
        when(session.executeRead(any())).thenReturn(Collections.emptyList());
        assertNotNull(shelfService.getAllShelves());
    }

    @Test
    void testGetShelf_throwsWhenIdNull() {
        assertThrows(NullPointerException.class,
            () -> shelfService.getShelf(null));
    }

    @Test
    void testAttachShelfToPosition_invalidArgs() {
        assertThrows(NullPointerException.class,
            () -> shelfService.attachShelfToPosition(null, "S1"));

        assertThrows(NullPointerException.class,
            () -> shelfService.attachShelfToPosition("SP1", null));
    }

    @Test
    void testDetachShelf_noErrors() {
        when(driver.session(any(SessionConfig.class))).thenReturn(session);
        when(session.executeWrite(any())).thenReturn(null);

        assertDoesNotThrow(() -> shelfService.detachShelfFromPosition("SP1"));
    }
}
