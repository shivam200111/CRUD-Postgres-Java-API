package com.example.demo.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorTest {

    @Test
    void testErrorConstructorAndGetters() {
        // Given: Error object created with constructor
        String name = "TestError";
        String message = "This is a test error";
        Error error = new Error(name, message);

        // Then: Check if values are correctly set
        assertEquals(name, error.getName());
        assertEquals(message, error.getMessage());
        assertNotNull(error.getTimeStamp()); // Ensure timestamp is set
    }

    @Test
    void testSetters() {
        // Given: An empty Error object
        Error error = new Error();

        // When: Setting values using setters
        error.setName("NewError");
        error.setMessage("Updated error message");
        error.setTimeStamp(LocalDateTime.now().toString());

        // Then: Verify values were set correctly
        assertEquals("NewError", error.getName());
        assertEquals("Updated error message", error.getMessage());
        assertNotNull(error.getTimeStamp());
    }
}
