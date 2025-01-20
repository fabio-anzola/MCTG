package at.fhtw.mctg.service.transaction;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
    }

    @Test
    void testHandleRequest_postWithAuth() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.POST);
        // Simulate an Authorization header
        request.getHeaderMap().ingest("Authorization:Bearer some-token");
        // Optionally, set the path or body if needed

        // Act
        Response response = transactionService.handleRequest(request);

        // Assert
        // The actual status depends on what your transactionController.acquirePack(...) returns.
        // But we know it should not be 401 or 400 if the logic succeeded.
        assertNotNull(response);
        assertNotEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus(),
                "With an Authorization header, it should not be 401");
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus(),
                "With a POST request and auth, it should not be 400");
    }

    @Test
    void testHandleRequest_postNoAuth() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.POST);
        // No authorization header => expect 401

        // Act
        Response response = transactionService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus(),
                "POST without Authorization header should return 401");
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertTrue(response.getContent().contains("Access token is missing or invalid"));
    }

    @Test
    void testHandleRequest_nonPostMethod() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.GET);  // or Method.PUT, DELETE, etc.

        // Act
        Response response = transactionService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus(),
                "Only POST is handled, so all other methods should return 400");
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertEquals("[]", response.getContent());
    }
}