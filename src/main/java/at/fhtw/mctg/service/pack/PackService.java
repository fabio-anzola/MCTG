package at.fhtw.mctg.service.pack;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.card.CardController;
import at.fhtw.mctg.controller.pack.PackController;
import at.fhtw.mctg.controller.session.SessionController;

public class PackService implements Service {
    private final PackController packController;
    private final CardController cardController;

    public PackService() {
        this.packController = new PackController();
        this.cardController = new CardController();
    }

    @Override
    public Response handleRequest(Request request) {

        // POST
        if (request.getMethod() == Method.POST) {

            if (request.getHeaderMap().getHeader("Authorization") == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Access token is missing or invalid\" }"
                );
            }

            if (!new SessionController().getUserByToken(request).equals("admin")) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Provided user is not admin\" }"
                );
            }

            int id =  this.packController.createPack(request);
            if (id < 0) {
                return new Response(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ContentType.JSON,
                        "{}"
                );
            }

            return this.cardController.addMultipleCards(request, id);


        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
