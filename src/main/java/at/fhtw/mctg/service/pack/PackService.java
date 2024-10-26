package at.fhtw.mctg.service.pack;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.pack.PackController;

public class PackService implements Service {
    private final PackController packController;

    public PackService() {
        this.packController = new PackController();
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
