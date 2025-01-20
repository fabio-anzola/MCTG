package at.fhtw.mctg.service.battle;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.battle.BattleController;
import at.fhtw.mctg.model.Battle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BattleServiceTest {

    private BattleController battleControllerMock;
    private BattleService battleService;

    @BeforeEach
    void setUp() {
        // Create a mock BattleController to pass into BattleService
        battleControllerMock = Mockito.mock(BattleController.class);
        battleService = new BattleService(battleControllerMock);
    }

    @Test
    void testHandleRequest_nonPostMethod() {
        // Arrange
        battleService = new BattleService();
        Request request = new Request();
        request.setMethod(Method.GET); // not POST

        // Act
        Response response = battleService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertEquals("[]", response.getContent());

        // No calls to the controller
        verifyNoInteractions(battleControllerMock);
    }

    @Test
    void testHandleRequest_postNoAuth() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.POST);
        // No "Authorization" header

        // Act
        Response response = battleService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertTrue(response.getContent().contains("Access token is missing or invalid"));

        // No calls to the controller
        verifyNoInteractions(battleControllerMock);
    }

    @Test
    void testHandleRequest_postInitBattleReturnsNull() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.POST);
        request.getHeaderMap().ingest("Authorization:Bearer some-token");

        // Stub battleController.initBattle(...) to return null
        when(battleControllerMock.initBattle(any(Request.class))).thenReturn(null);

        // Act
        Response response = battleService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertEquals("[]", response.getContent());

        // initBattle was called, but waitForBattle and getLog should not be
        verify(battleControllerMock).initBattle(request);
        verify(battleControllerMock, never()).waitForBattle(any(Battle.class));
        verify(battleControllerMock, never()).getLog(any(Battle.class));
    }

    @Test
    void testHandleRequest_postSuccessfulBattle() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.POST);
        request.getHeaderMap().ingest("Authorization:Bearer some-token");

        // Mock a valid Battle object
        Battle mockBattle = new Battle(123, null, null, 0, 999);
        when(battleControllerMock.initBattle(any(Request.class))).thenReturn(mockBattle);

        // waitForBattle(...) does not return anything
        doNothing().when(battleControllerMock).waitForBattle(mockBattle);

        // getLog(...) returns a success response
        Response logResponse = new Response(HttpStatus.OK, ContentType.JSON, "{\"log\":\"some data\"}");
        when(battleControllerMock.getLog(mockBattle)).thenReturn(logResponse);

        // Act
        Response response = battleService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertTrue(response.getContent().contains("\"log\":\"some data\""));

        // Verify calls
        verify(battleControllerMock).initBattle(request);
        verify(battleControllerMock).waitForBattle(mockBattle);
        verify(battleControllerMock).getLog(mockBattle);
    }
}