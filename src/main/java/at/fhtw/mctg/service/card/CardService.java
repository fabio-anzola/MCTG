package at.fhtw.mctg.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.card.CardController;

public class CardService implements Service {
    private final CardController cardController;

    public CardService() {
        this.cardController = new CardController();
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
            return this.cardController.getCardsByUid(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
