package at.fhtw.mctg.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.card.CardController;

/**
 * The CardService class is responsible for handling card-related HTTP requests
 * and delegating the processing to the CardController.
 *
 * This service primarily supports handling GET requests. It ensures that
 * requests include valid authorization headers before delegating to the
 * CardController for further processing. Responses are crafted based on the
 * HTTP request method, headers, and the operations performed by the controller.
 */
public class CardService implements Service {
    private final CardController cardController;

    /**
     * Initializes a new instance of the CardService class.
     *
     * This constructor sets up the CardController, which is responsible for
     * performing business logic on card-related operations. The CardService acts
     * as a delegate for handling HTTP requests and routing them to the
     * CardController for processing.
     */
    public CardService() {
        this.cardController = new CardController();
    }

    /**
     * Handles incoming HTTP requests and generates an appropriate response.
     * This method currently processes only GET requests and validates the
     * presence of an "Authorization" header. If valid, it delegates the
     * request to the CardController for further processing.
     *
     * @param request the HTTP request to handle, which includes method, headers,
     *                and other request attributes
     * @return a Response instance representing the outcome of the request
     *         - Returns a 401 UNAUTHORIZED response if the "Authorization"
     *           header is missing or invalid
     *         - Returns a 200 OK response with card data if processing is successful
     *         - Returns a 400 BAD_REQUEST response for unsupported HTTP methods
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
            return this.cardController.getCardsByUid(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
