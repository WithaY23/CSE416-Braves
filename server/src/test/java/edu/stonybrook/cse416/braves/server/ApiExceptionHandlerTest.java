package edu.stonybrook.cse416.braves.server;

import edu.stonybrook.cse416.braves.server.config.ApiExceptionHandler;
import edu.stonybrook.cse416.braves.server.dto.SkeletonResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiExceptionHandlerTest {

    @Test
    void unsupportedOperationMapsToSkeletonResponse() {
        ApiExceptionHandler handler = new ApiExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/states/OR/summary");

        ResponseEntity<SkeletonResponse> response = handler.handleNotImplemented(
                new UnsupportedOperationException("Planned for next phase"),
                request
        );

        assertEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
        assertEquals("v1", response.getBody().schemaVersion());
        assertEquals("skeleton", response.getBody().status());
        assertEquals("/api/states/OR/summary", response.getBody().route());
    }
}
