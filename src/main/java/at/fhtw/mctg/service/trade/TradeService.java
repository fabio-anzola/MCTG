package at.fhtw.mctg.service.trade;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.trade.TradeController;

public class TradeService implements Service {
    private final TradeController tradeController;

    public TradeService() {
        this.tradeController = new TradeController();
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
            return tradeController.getAvailableTrades(request);
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
            return tradeController.createNewTrade(request);
        }
        // DELETE with Argument
        if (request.getMethod() == Method.DELETE && request.getPathParts().size() > 1) {
            // TODO: Implement this method
            return null;
        }
        // POST with Argument
        if (request.getMethod() == Method.POST && request.getPathParts().size() > 1) {
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
