package at.fhtw.mctg.service.session;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        // Directly create the SessionService; it internally creates its own SessionController
        sessionService = new SessionService();
    }

    @Test
    void testHandleRequest_postCreateSession() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.POST);
        // Optional: set request body or headers as needed
        request.setBody("{\"username\": \"Alice\", \"password\": \"secret\"}");

        // Act
        Response response = sessionService.handleRequest(request);

        // Assert
        // The actual status depends on sessionController.createSession(...).
        // We know that it should NOT be BAD_REQUEST if the logic is implemented.
        assertNotNull(response);
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus(),
                "POST requests should not return BAD_REQUEST for creating a session");
    }

    @Test
    void testHandleRequest_nonPostMethod() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.GET);

        // Act
        Response response = sessionService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus(),
                "All methods except POST should return BAD_REQUEST");
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertEquals("[]", response.getContent());
    }
}