package at.fhtw.mctg.service.trade;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TradeServiceTest {

    private TradeService tradeService;

    @BeforeEach
    void setUp() {
        tradeService = new TradeService();
    }

    @Test
    void testHandleRequest_getNoAuth() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("trade")); // e.g., "/trade"
        // No Authorization header

        // Act
        Response response = tradeService.handleRequest(request);

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
        request.setPathParts(List.of("trade"));
        request.getHeaderMap().ingest("Authorization:Bearer some-token");

        // Act
        Response response = tradeService.handleRequest(request);

        // Assert
        // The actual response depends on TradeController#getAvailableTrades(...).
        // We only verify the service didn't return BAD_REQUEST or UNAUTHORIZED.
        assertNotNull(response);
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertNotEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
    }

    @Test
    void testHandleRequest_postJoinTradeNoAuth() {
        // POST with pathParts > 1 => joinTrade
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setPathParts(List.of("trade", "someTradeId")); // e.g., "/trade/someTradeId"
        // Missing Authorization

        Response response = tradeService.handleRequest(request);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
    }

    @Test
    void testHandleRequest_postJoinTradeWithAuth() {
        // POST with 2 path parts => joinTrade
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setPathParts(List.of("trade", "someTradeId"));
        request.getHeaderMap().ingest("Authorization:Bearer token");

        Response response = tradeService.handleRequest(request);

        // The real outcome depends on TradeController#joinTrade(...)
        // We just check it didn't get blocked by the service.
        assertNotNull(response);
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertNotEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
    }

    @Test
    void testHandleRequest_postCreateTradeNoAuth() {
        // POST with exactly 1 path part => createNewTrade
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setPathParts(List.of("trade")); // e.g. "/trade"
        // Missing Authorization

        Response response = tradeService.handleRequest(request);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
    }

    @Test
    void testHandleRequest_postCreateTradeWithAuth() {
        // POST with single path part => createNewTrade
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setPathParts(List.of("trade"));
        request.getHeaderMap().ingest("Authorization:Bearer token");

        Response response = tradeService.handleRequest(request);

        // Actual logic depends on createNewTrade
        assertNotNull(response);
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertNotEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
    }

    @Test
    void testHandleRequest_deleteNoAuth() {
        // DELETE /trade/someTradeId => pathParts = 2
        Request request = new Request();
        request.setMethod(Method.DELETE);
        request.setPathParts(List.of("trade", "someTradeId"));
        // Missing Authorization

        Response response = tradeService.handleRequest(request);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
    }

    @Test
    void testHandleRequest_deleteWithAuth() {
        // DELETE /trade/someTradeId => calls tradeController.deleteTrade(...)
        Request request = new Request();
        request.setMethod(Method.DELETE);
        request.setPathParts(List.of("trade", "someTradeId"));
        request.getHeaderMap().ingest("Authorization:Bearer token");

        Response response = tradeService.handleRequest(request);

        // The actual status depends on the controller's logic,
        // we just check it's not blocked as BAD_REQUEST or UNAUTHORIZED:
        assertNotNull(response);
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertNotEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
    }

    @Test
    void testHandleRequest_unknownMethod() {
        // For example, a PUT method isn't handled => BAD_REQUEST
        Request request = new Request();
        request.setMethod(Method.PUT);
        request.setPathParts(List.of("trade"));

        Response response = tradeService.handleRequest(request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertEquals("[]", response.getContent());
    }
}