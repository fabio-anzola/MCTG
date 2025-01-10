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

/**
 * Service implementation for handling requests related to packs and cards.
 * This class processes client requests by delegating specific tasks to
 * the associated PackController and CardController.
 */
public class PackService implements Service {
    private final PackController packController;
    private final CardController cardController;

    /**
     * Constructs a new PackService instance.
     *
     * This constructor initializes the PackService by instantiating the
     * associated PackController and CardController. These controllers are used
     * to handle the creation of packs and managing associated cards during service
     * operations.
     */
    public PackService() {
        this.packController = new PackController();
        this.cardController = new CardController();
    }

    /**
     * Handles an incoming HTTP request and generates an appropriate HTTP response.
     *
     * This method processes the request based on its HTTP method and headers.
     * If the request is a POST method, it validates the presence of the Authorization
     * header and verifies that the user associated with the token is an admin. Upon
     * successful validation, it delegates the task of creating a pack to the pack controller
     * and adds multiple cards to the created pack using the card controller.
     *
     * If the request does not meet the necessary conditions or uses an unsupported method,
     * it returns the respective error response.
     *
     * @param request the incoming HTTP request object containing method, headers,
     *                body, and other metadata.
     * @return a Response object containing the status, content type, and response body,
     *         representing the outcome of the request processing.
     */
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
