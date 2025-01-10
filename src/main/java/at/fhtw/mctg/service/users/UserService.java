package at.fhtw.mctg.service.users;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.users.UserController;

/**
 * The UserService class implements the Service interface and serves as the entry point for handling user-related HTTP requests.
 * It delegates the processing of requests to the UserController class. The service provides functionality for creating new users,
 * retrieving user information by username, and updating user details.
 */
public class UserService implements Service {
    private final UserController userController;

    /**
     * Constructs a new instance of the UserService class.
     * This service is responsible for handling user-related HTTP requests by delegating
     * operations to the UserController. It provides functionality to create, retrieve,
     * and update user information.
     */
    public UserService() {
        this.userController = new UserController();
    }

    /**
     * Handles an incoming HTTP request and delegates processing to the appropriate
     * user-related controller methods based on the HTTP method and URL structure.
     *
     * @param request the incoming HTTP request containing method, path, and relevant data
     * @return a Response object that encapsulates the status, content type, and response body
     */
    @Override
    public Response handleRequest(Request request) {

        // POST -> Create new User
        if (request.getMethod() == Method.POST) {
            return this.userController.createUser(request);
        }

        // GET + username -> Retrieve specific user
        if (request.getMethod() == Method.GET && request.getPathParts().size() > 1) {
            return this.userController.getUserByName(request);
        }

        // PUT + username -> Update specific User
        if (request.getMethod() == Method.PUT && request.getPathParts().size() > 1) {
            return this.userController.updateUserByName(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
