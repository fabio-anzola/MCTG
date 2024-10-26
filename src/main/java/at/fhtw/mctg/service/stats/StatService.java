package at.fhtw.mctg.service.stats;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class StatService implements Service {
    private final StatController statController;

    public StatService() {
        this.statController = new StatController();
    }

    @Override
    public Response handleRequest(Request request) {

        // GET
        if (request.getMethod() == Method.GET) {
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

