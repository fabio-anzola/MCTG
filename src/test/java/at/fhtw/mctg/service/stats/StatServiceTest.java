package at.fhtw.mctg.service.stats;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatServiceTest {

    private StatService statService;

    @BeforeEach
    void setUp() {
        statService = new StatService();
    }

    @Test
    void testHandleRequest_getNoAuth() {
        // Prepare a GET request without "Authorization"
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("stats"));

        // Execute
        Response response = statService.handleRequest(request);

        // Verify
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus(), "Should return 401 for missing auth");
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertTrue(response.getContent().contains("Access token is missing or invalid"));
    }

    @Test
    void testHandleRequest_getWithAuth() {
        // Prepare a GET request with "Authorization"
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("stats"));
        request.getHeaderMap().ingest("Authorization:Bearer valid-token");

        // Execute
        Response response = statService.handleRequest(request);

        // The actual status depends on StatController#getStatsByUid(...)
        // We only check that the service does NOT return BAD_REQUEST or UNAUTHORIZED
        assertNotNull(response);
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertNotEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
    }

    @Test
    void testHandleRequest_otherMethod() {
        // Any method that's not GET => BAD_REQUEST
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setPathParts(List.of("stats"));

        Response response = statService.handleRequest(request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertEquals("[]", response.getContent());
    }
}