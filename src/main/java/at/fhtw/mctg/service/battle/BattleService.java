package at.fhtw.mctg.service.battle;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.battle.BattleController;

public class BattleService implements Service {
    private final BattleController battleController;

    public BattleService() {
        this.battleController = new BattleController();
    }

    @Override
    public Response handleRequest(Request request) {

        // POST
        if (request.getMethod() == Method.POST) {
            // TODO: Implement this method
            return null;
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}