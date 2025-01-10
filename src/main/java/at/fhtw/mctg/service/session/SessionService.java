package at.fhtw.mctg.service.session;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.session.SessionController;

/**
 * This class provides session-related services and implements the Service interface.
 * It handles HTTP requests for session operations such as creating a new session.
 */
public class SessionService implements Service {
    private final SessionController sessionController;

    /**
     * Constructs an instance of the SessionService class.
     * This constructor initializes a new SessionController instance to handle session-related operations.
     */
    public SessionService() {
        this.sessionController = new SessionController();
    }

    /**
     * Handles an incoming HTTP request and provides an appropriate response.
     * This method processes requests to create a new session if the HTTP method is POST.
     * For unsupported HTTP methods, it returns a Bad Request response.
     *
     * @param request the HTTP request to be processed, including method, headers, body, and additional data
     * @return a {@link Response} object containing the status, content type, and body
     */
    @Override
    public Response handleRequest(Request request) {

        // POST -> Create new Session (login)
        if (request.getMethod() == Method.POST) {
            return this.sessionController.createSession(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );

    }
}
