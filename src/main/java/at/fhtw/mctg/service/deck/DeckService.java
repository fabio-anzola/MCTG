package at.fhtw.mctg.service.deck;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.deck.DeckController;

/**
 * Service class responsible for handling deck-related requests.
 * Implements the Service interface and delegates the specific
 * functionalities to the DeckController.
 */
public class DeckService implements Service {
    private final DeckController deckController;

    /**
     * Constructs a DeckService object.
     * Initializes and assigns a new instance of DeckController to handle
     * the core deck-related operations and logic.
     */
    public DeckService() {
        this.deckController = new DeckController();

    }

    /**
     * Handles incoming requests and routes them to the appropriate actions based on the
     * HTTP method and request headers. Supports GET and PUT operations for managing
     * active cards through the DeckController.
     *
     * @param request the incoming request containing method, headers, and other details
     * @return a Response object representing the outcome of the request. Possible responses include:
     *         - Unauthorized (401): if the "Authorization" header is missing or invalid.
     *         - Bad Request (400): for unsupported methods or malformed requests.
     *         - Delegates to DeckController for GET and PUT operations with specific responses based on business logic.
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
            return this.deckController.getActiveCards(request);
        }
        // PUT
        if (request.getMethod() == Method.PUT) {
            if (request.getHeaderMap().getHeader("Authorization") == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Access token is missing or invalid\" }"
                );
            }
            return this.deckController.setActiveCards(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
