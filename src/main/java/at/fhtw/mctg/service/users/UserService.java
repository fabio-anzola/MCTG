package at.fhtw.mctg.service.users;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.users.UserController;

public class UserService implements Service {
    private final UserController userController;

    public UserService() {
        this.userController = new UserController();
    }

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
