package at.fhtw.mctg.service.transaction;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.transaction.TransactionController;

/**
 * The TransactionService class provides functionality to handle transaction-related HTTP requests.
 * It implements the Service interface and processes client requests specifically for transaction operations.
 * This service delegates the main transaction logic to the TransactionController.
 */
public class TransactionService implements Service {
    private final TransactionController transactionController;

    /**
     * Initializes a new instance of the TransactionService class.
     *
     * This constructor sets up the TransactionService by instantiating a
     * TransactionController object to handle the processing of transaction-related
     * requests. The TransactionController is designed to execute the core business
     * logic associated with transactions, such as acquiring packages.
     */
    public TransactionService() {
        this.transactionController = new TransactionController();
    }

    /**
     * Handles an HTTP request and processes it based on the request method and headers.
     * Specifically designed to manage transaction-related requests by verifying authorization and delegating
     * further processing to the TransactionController.
     *
     * @param request the HTTP request object containing method, headers, and other details.
     * @return a Response object representing the result of the request processing.
     *         If the method is POST and the Authorization header is present, it forwards the request to the TransactionController.
     *         If the Authorization header is missing or invalid for a POST request, it returns a 401 Unauthorized response.
     *         For non-POST methods, it returns a 400 Bad Request response.
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

            return this.transactionController.acquirePack(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}