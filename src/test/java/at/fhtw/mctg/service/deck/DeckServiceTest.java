package at.fhtw.mctg.service.deck;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckServiceTest {

    private DeckService deckService;

    @BeforeEach
    void setUp() {
        deckService = new DeckService();
    }

    @Test
    void testHandleRequest_getNoAuth() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("deck")); // e.g. "/deck"

        // Act
        Response response = deckService.handleRequest(request);

        // Assert
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
        request.setPathParts(List.of("deck"));
        // Provide some Authorization header
        request.getHeaderMap().ingest("Authorization:Bearer some-token");

        // Act
        Response response = deckService.handleRequest(request);

        // Assert
        // The actual status depends on deckController.getActiveCards(...).
        // We only confirm the service didn't reject it with UNAUTHORIZED or BAD_REQUEST.
        assertNotNull(response);
        assertNotEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
    }

    @Test
    void testHandleRequest_putNoAuth() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.PUT);
        request.setPathParts(List.of("deck"));
        // No Authorization

        // Act
        Response response = deckService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertTrue(response.getContent().contains("Access token is missing or invalid"));
    }

    @Test
    void testHandleRequest_putWithAuth() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.PUT);
        request.setPathParts(List.of("deck"));
        request.getHeaderMap().ingest("Authorization:Bearer some-token");
        // Possibly add some JSON body representing cards to set active

        // Act
        Response response = deckService.handleRequest(request);

        // Assert
        // We only check that the service doesn't return 401 or 400.
        // Actual outcome depends on deckController.setActiveCards(...)
        assertNotNull(response);
        assertNotEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
    }

    @Test
    void testHandleRequest_invalidMethod() {
        // e.g. POST => not handled => 400 Bad Request
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setPathParts(List.of("deck"));
        request.getHeaderMap().ingest("Authorization:Bearer some-token");

        Response response = deckService.handleRequest(request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertEquals("[]", response.getContent());
    }
}