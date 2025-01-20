package at.fhtw.mctg.service.pack;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.card.CardController;
import at.fhtw.mctg.controller.pack.PackController;
import at.fhtw.mctg.controller.session.SessionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PackServiceTest {

    private PackController packControllerMock;
    private CardController cardControllerMock;
    private SessionController sessionControllerMock;
    private PackService packService;

    @BeforeEach
    void setUp() {
        // Create mocks
        packControllerMock = Mockito.mock(PackController.class);
        cardControllerMock = Mockito.mock(CardController.class);
        sessionControllerMock = Mockito.mock(SessionController.class);

        // Inject them into PackService
        packService = new PackService(packControllerMock, cardControllerMock, sessionControllerMock);
    }

    @Test
    void testHandleRequest_nonPostMethod() {
        // Arrange
        packService = new PackService();
        Request request = new Request();
        request.setMethod(Method.GET);

        // Act
        Response response = packService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertEquals("[]", response.getContent());
        // Verify no interaction with controllers
        verifyNoInteractions(packControllerMock, cardControllerMock, sessionControllerMock);
    }

    @Test
    void testHandleRequest_postNoAuth() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.POST);
        // No "Authorization" header

        // Act
        Response response = packService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
        assertTrue(response.getContent().contains("Access token is missing or invalid"));
        // No controller calls
        verifyNoInteractions(packControllerMock, cardControllerMock, sessionControllerMock);
    }

    @Test
    void testHandleRequest_postNotAdmin() {
        // Arrange
        Request request = new Request();
        request.setMethod(Method.POST);
        request.getHeaderMap().ingest("Authorization:Bearer token");

        // Stub sessionController to return a user != "admin"
        when(sessionControllerMock.getUserByToken(eq(request))).thenReturn("Bob");

        // Act
        Response response = packService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN.code, response.getStatus());
        assertTrue(response.getContent().contains("not admin"));
        // No calls to packController or cardController
        verify(sessionControllerMock).getUserByToken(request);
        verifyNoInteractions(packControllerMock, cardControllerMock);
    }

    @Test
    void testHandleRequest_postAdminCreatePackNegative() {
        // user = admin, but createPack returns -1 => INTERNAL_SERVER_ERROR
        Request request = new Request();
        request.setMethod(Method.POST);
        request.getHeaderMap().ingest("Authorization:Bearer token");

        when(sessionControllerMock.getUserByToken(eq(request))).thenReturn("admin");
        when(packControllerMock.createPack(eq(request))).thenReturn(-1);

        // Act
        Response response = packService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertEquals("{}", response.getContent());

        verify(sessionControllerMock).getUserByToken(request);
        verify(packControllerMock).createPack(request);
        // No call to cardController.addMultipleCards
        verifyNoInteractions(cardControllerMock);
    }

    @Test
    void testHandleRequest_postAdminCreatePackSuccess() {
        // user = admin, createPack returns a valid ID => call cardController.addMultipleCards
        Request request = new Request();
        request.setMethod(Method.POST);
        request.getHeaderMap().ingest("Authorization:Bearer token");

        when(sessionControllerMock.getUserByToken(eq(request))).thenReturn("admin");
        when(packControllerMock.createPack(eq(request))).thenReturn(123);

        // Suppose addMultipleCards returns a success Response
        Response successResp = new Response(HttpStatus.CREATED, ContentType.JSON, "{\"cards\":\"added\"}");
        when(cardControllerMock.addMultipleCards(eq(request), eq(123))).thenReturn(successResp);

        // Act
        Response response = packService.handleRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED.code, response.getStatus());
        assertEquals("{\"cards\":\"added\"}", response.getContent());

        verify(sessionControllerMock).getUserByToken(request);
        verify(packControllerMock).createPack(request);
        verify(cardControllerMock).addMultipleCards(request, 123);
    }
}