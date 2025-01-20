package at.fhtw.mctg.service.scoreboard;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardServiceTest {

    private ScoreboardService scoreboardService;

    @BeforeEach
    void setUp() {
        scoreboardService = new ScoreboardService();
    }

    @Test
    void testHandleRequest_getNoAuth() {
        // Create a GET request *without* Authorization
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("scoreboard"));

        // Act
        Response response = scoreboardService.handleRequest(request);

        // Assert -> should be UNAUTHORIZED
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertTrue(response.getContent().contains("Access token is missing or invalid"));
    }

    @Test
    void testHandleRequest_getWithAuth() {
        // Create a GET request *with* Authorization
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("scoreboard"));
        request.getHeaderMap().ingest("Authorization:Bearer some-token");

        // Act
        Response response = scoreboardService.handleRequest(request);

        // We don't know exactly what scoreboardController.getScoreboard(request) will return,
        // but we do know that the code won't return BAD_REQUEST or UNAUTHORIZED on the service level.
        assertNotNull(response);
        assertNotEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus(),
                "Should not be UNAUTHORIZED if an Authorization header is present");
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus(),
                "A valid GET request should not return BAD_REQUEST");
    }

    @Test
    void testHandleRequest_otherMethod() {
        // Any other method => BAD_REQUEST
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setPathParts(List.of("scoreboard"));

        Response response = scoreboardService.handleRequest(request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertEquals("[]", response.getContent());
    }
}