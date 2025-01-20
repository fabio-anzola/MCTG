package at.fhtw.mctg.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardServiceTest {

    private CardService cardService;

    @BeforeEach
    void setUp() {
        cardService = new CardService();
    }

    @Test
    void testHandleRequest_getNoAuth() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("cards")); // e.g. "/cards"

        // Act
        Response response = cardService.handleRequest(request);

        // Assert
        // Expect UNAUTHORIZED (401)
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertTrue(response.getContent().contains("Access token is missing or invalid"));
    }

    @Test
    void testHandleRequest_getWithAuth() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("cards"));
        request.getHeaderMap().ingest("Authorization:Bearer some-token");

        // Act
        Response response = cardService.handleRequest(request);

        // Assert
        // The actual status depends on cardController.getCardsByUid(request).
        // We just confirm the service didn't reject it with BAD_REQUEST or UNAUTHORIZED.
        assertNotNull(response);
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertNotEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
    }

    @Test
    void testHandleRequest_nonGetMethod() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.POST); // or PUT, DELETE, etc.
        request.setPathParts(List.of("cards"));
        // Optional: set an Authorization header
        request.getHeaderMap().ingest("Authorization:Bearer token");

        // Act
        Response response = cardService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertEquals("[]", response.getContent());
    }
}