package at.fhtw.mctg.service.users;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        // Directly instantiate the service; internally it creates its own UserController.
        userService = new UserService();
    }

    @Test
    void testHandleRequest_postCreateUser() {
        // Simulate a POST request with no specific path parts (e.g., /users).
        Request request = new Request();
        request.setMethod(Method.POST);
        // We'll assume the path is simply "users" => no extra path parts
        request.setPathParts(List.of("users"));
        // Possibly set a body with JSON data for creating a user
        request.setBody("{\"username\": \"Alice\", \"password\": \"secret\"}");

        Response response = userService.handleRequest(request);

        // The actual response depends on UserController logic;
        // we at least check that it's not a BAD_REQUEST because the code routes POST to createUser.
        // If your createUser(...) returns 201, you might check that. For a generic example:
        assertNotNull(response);
        // Example assertion: if we expect userController to respond with 201 on success
        // assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        // Or at least not 400:
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
    }

    @Test
    void testHandleRequest_getUserWithoutName() {
        // Simulate GET /users => pathParts = ["users"] only => no user name
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("users")); // only 1 part

        Response response = userService.handleRequest(request);

        // Because getUserByName requires at least 2 path parts (["users", "<username>"]),
        // we expect the service to return BAD_REQUEST.
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertEquals("[]", response.getContent());
    }

    @Test
    void testHandleRequest_getUserWithName() {
        // GET /users/Alice => pathParts = ["users", "Alice"]
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("users", "Alice"));

        Response response = userService.handleRequest(request);

        // Again, the actual result depends on your UserController's implementation,
        // but we know from the service logic that GET with 2 path parts calls getUserByName(...).
        assertNotNull(response);
        // Possibly the user doesn't exist => maybe 404, or if it does => 200. Just check it's not BAD_REQUEST:
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
    }

    @Test
    void testHandleRequest_putUpdateUser() {
        // PUT /users/Alice => pathParts = ["users", "Alice"]
        Request request = new Request();
        request.setMethod(Method.PUT);
        request.setPathParts(List.of("users", "Alice"));
        // Some JSON body for update
        request.setBody("{\"bio\": \"Updated user bio\"}");

        Response response = userService.handleRequest(request);

        // The service routes PUT (with at least 2 path parts) to userController.updateUserByName(request).
        // Let's just confirm we didn't get BAD_REQUEST:
        assertNotNull(response);
        assertNotEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
    }

    @Test
    void testHandleRequest_invalidMethod() {
        // e.g. DELETE /users/Alice => pathParts = 2, but the code has no branch for DELETE
        Request request = new Request();
        request.setMethod(Method.DELETE);
        request.setPathParts(List.of("users", "Alice"));

        Response response = userService.handleRequest(request);

        // There's no condition for Method.DELETE, so we expect BAD_REQUEST from the default return
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertEquals("[]", response.getContent());
    }

    @Test
    void testHandleRequest_putNoUsername() {
        // PUT /users => pathParts = ["users"] => missing username
        Request request = new Request();
        request.setMethod(Method.PUT);
        request.setPathParts(List.of("users"));

        Response response = userService.handleRequest(request);

        // No second path part => won't match updateUserByName => BAD_REQUEST
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.code, response.getStatus());
        assertEquals(ContentType.JSON.type, response.getContentType());
        assertEquals("[]", response.getContent());
    }
}