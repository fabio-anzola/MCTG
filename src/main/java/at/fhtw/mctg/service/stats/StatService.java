package at.fhtw.mctg.service.stats;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.stats.StatController;

/**
 * StatService is responsible for handling incoming requests related to statistical data.
 * It leverages the StatController to process specific logic for retrieving user statistics
 * and responds with the appropriate HTTP statuses and response data.
 */
public class StatService implements Service {
    private final StatController statController;

    /**
     * Default constructor for the StatService class.
     * Initializes the statController instance to handle logic related to user statistics.
     */
    public StatService() {
        this.statController = new StatController();
    }

    /**
     * Handles incoming HTTP requests and provides the appropriate response based
     * on the request method and headers. Processes GET requests to retrieve
     * statistical data if the Authorization header is provided, otherwise it
     * returns an UNAUTHORIZED response. If the request method is unsupported,
     * it returns a BAD_REQUEST response.
     *
     * @param request the incoming HTTP request containing method, headers, and other details
     * @return a Response object containing the status, content type, and response data
     */
    @Override
    public Response handleRequest(Request request) {

        // GET
        if (request.getMethod() == Method.GET) {
            if (request.getHeaderMap().getHeader("Authorization") == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Access token is missing or invalid\" }"
                );
            }
            return statController.getStatsByUid(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}

