package com.example.demo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleRuntimeException() {
        // Given: A RuntimeException is thrown
        RuntimeException exception = new RuntimeException("Test RuntimeException");

        // When: The exception handler processes it
        ResponseEntity<Error> response = exceptionHandler.handleRuntimeException(exception);

        // Then: Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RuntimeException", response.getBody().getName());  // ✅ Check `name`
        assertNotNull(response.getBody().getTimeStamp());  // ✅ Check `timeStamp`
        assertEquals("Test RuntimeException", response.getBody().getMessage());  // ✅ Check `message`
    }
}
