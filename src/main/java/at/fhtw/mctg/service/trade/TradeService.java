package at.fhtw.mctg.service.trade;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.trade.TradeController;

/**
 * The TradeService class implements the Service interface to handle HTTP requests
 * related to trade operations such as retrieving available trades, creating trades,
 * joining trades, and deleting trades.
 *
 * This class is responsible for delegating the request handling to the TradeController
 * based on the HTTP method and path details provided in the request. It also verifies
 * the presence of an Authorization header in applicable cases, responding with an
 * unauthorized status if the header is missing or invalid.
 */
public class TradeService implements Service {
    private final TradeController tradeController;

    /**
     * Default constructor for the TradeService class.
     *
     * Initializes a new instance of TradeController to handle trade-related
     * operations. The TradeController is used to process requests such as
     * retrieving available trades, creating new trades, joining trades,
     * or deleting existing trades.
     */
    public TradeService() {
        this.tradeController = new TradeController();
    }

    /**
     * Handles incoming HTTP requests and routes them to the appropriate methods
     * based on the HTTP method and request path. Supports GET, POST, and DELETE
     * operations for trade-related functionality, including fetching available trades,
     * creating new trades, joining an existing trade, or deleting a trade.
     *
     * If the request lacks a valid "Authorization" header, an UNAUTHORIZED response will be returned.
     * Unsupported methods or invalid routes result in a BAD_REQUEST response.
     *
     * @param request the incoming HTTP request containing method, headers, path, and body
     * @return a Response object that represents the outcome of processing the request
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
            return tradeController.getAvailableTrades(request);
        }
        // POST with Argument
        if (request.getMethod() == Method.POST && request.getPathParts().size() > 1) {
            if (request.getHeaderMap().getHeader("Authorization") == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Access token is missing or invalid\" }"
                );
            }
            return this.tradeController.joinTrade(request);
        }
        // POST
        if (request.getMethod() == Method.POST) {
            if (request.getHeaderMap().getHeader("Authorization") == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Access token is missing or invalid\" }"
                );
            }
            return this.tradeController.createNewTrade(request);
        }
        // DELETE with Argument
        if (request.getMethod() == Method.DELETE && request.getPathParts().size() > 1) {
            if (request.getHeaderMap().getHeader("Authorization") == null) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Access token is missing or invalid\" }"
                );
            }
            return this.tradeController.deleteTrade(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
