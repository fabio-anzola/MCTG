package at.fhtw.mctg.service.transaction;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.transaction.TransactionController;

public class TransactionService implements Service {
    private final TransactionController transactionController;

    public TransactionService() {
        this.transactionController = new TransactionController();
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

            return this.transactionController.acquirePack(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}