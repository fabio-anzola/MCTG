package at.fhtw.mctg.service.scoreboard;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.scoreboard.ScoreboardController;

public class ScoreboardService implements Service {
    private final ScoreboardController scoreboardController;

    public ScoreboardService() {
        this.scoreboardController = new ScoreboardController();
    }

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
