package at.fhtw.httpserver.utils;

import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RequestBuilderTest {

    @Test
    void testBuildRequestSuccessWithParams() throws IOException {
        // Arrange
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        when(bufferedReader.readLine()).thenReturn("GET /test?param=value", "Header: Value", "");
        when(bufferedReader.read(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(0);

        // Act
        RequestBuilder builder = new RequestBuilder();
        Request request = builder.buildRequest(bufferedReader);

        // Assert
        assertNotNull(request);
        assertEquals(Method.GET, request.getMethod());
        assertEquals("/test", request.getPathname());
        assertEquals("param=value", request.getParams());
        assertNotNull(request.getHeaderMap());
        assertEquals("Value", request.getHeaderMap().getHeader("Header"));
    }

    @Test
    void testBuildRequestSuccessWithoutParams() throws IOException {
        // Arrange
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        when(bufferedReader.readLine()).thenReturn("POST /test", "Header: Value", "");
        when(bufferedReader.read(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(0);

        // Act
        RequestBuilder builder = new RequestBuilder();
        Request request = builder.buildRequest(bufferedReader);

        // Assert
        assertNotNull(request);
        assertEquals(Method.POST, request.getMethod());
        assertEquals("/test", request.getPathname());
        assertNull(request.getParams());
        assertNotNull(request.getHeaderMap());
        assertEquals("Value", request.getHeaderMap().getHeader("Header"));
    }

    @Test
    void testBuildRequestFailMethodUnsupported() throws IOException {
        // Arrange
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        when(bufferedReader.readLine()).thenReturn("INVALID /test", "Header: Value", "");
        when(bufferedReader.read(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(0);

        // Act
        RequestBuilder builder = new RequestBuilder();
        assertThrows(IllegalArgumentException.class, () -> builder.buildRequest(bufferedReader));
    }

    @Test
    void testBuildRequestFromGet() throws Exception {
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        when(reader.readLine()).thenReturn("GET /echo/mehr?p=23 HTTP/1.1",
                "Content-Type: text/plain",
                "Content-Length: 8",
                "Accept: */*",
                "",
                "{'id':1}");

        Request request = new RequestBuilder().buildRequest(reader);
        assertEquals("/echo/mehr", request.getPathname());
        assertEquals("/echo", request.getServiceRoute());
        assertEquals("mehr", request.getPathParts().get(1));
        assertEquals(8, request.getHeaderMap().getContentLength());
    }
}