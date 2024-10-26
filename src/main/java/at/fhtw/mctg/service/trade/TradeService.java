package at.fhtw.mctg.service.trade;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class TradeService implements Service {
    private final TradeController tradeController;

    public TradeService() {
        this.tradeController = new TradeController();
    }

    @Override
    public Response handleRequest(Request request) {

        // GET
        if (request.getMethod() == Method.GET) {
            // TODO: Implement this method
            return null;
        }
        // POST
        if (request.getMethod() == Method.POST) {
            // TODO: Implement this method
            return null;
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
