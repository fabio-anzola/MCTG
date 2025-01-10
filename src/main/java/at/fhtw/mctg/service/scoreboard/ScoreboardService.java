package at.fhtw.mctg.service.scoreboard;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.scoreboard.ScoreboardController;

/**
 * The ScoreboardService class is responsible for handling HTTP requests related to the scoreboard.
 * It implements the Service interface and delegates the processing of scoreboard-related requests
 * to the ScoreboardController.
 */
public class ScoreboardService implements Service {
    private final ScoreboardController scoreboardController;

    /**
     * Constructs a new ScoreboardService instance.
     *
     * This constructor initializes a ScoreboardController, which is used to process
     * scoreboard-related requests. The ScoreboardService acts as the intermediary
     * for handling HTTP requests pertaining to the scoreboard and delegates the
     * corresponding logic to its controller.
     */
    public ScoreboardService() {
        this.scoreboardController = new ScoreboardController();
    }

    /**
     * Handles HTTP requests related to the scoreboard.
     *
     * @param request the HTTP request containing method, headers, and other details
     * @return the HTTP response based on the request method and headers; returns an
     *         UNAUTHORIZED response if the Authorization header is missing, a BAD_REQUEST response
     *         for unsupported methods, or the result of the scoreboardController for valid GET requests
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
            return scoreboardController.getScoreboard(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
