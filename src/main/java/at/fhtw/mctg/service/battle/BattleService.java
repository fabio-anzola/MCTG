package at.fhtw.mctg.service.battle;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mctg.controller.battle.BattleController;
import at.fhtw.mctg.model.Battle;

/**
 * Service class for handling HTTP requests related to battles.
 * Implements the Service interface and provides logic for processing
 * POST requests to manage battle initialization and execution.
 *
 * This class interacts with the BattleController to perform
 * the necessary operations such as initializing a battle,
 * waiting for its completion, and retrieving battle logs.
 *
 * Request processing includes:
 * - Verifying the presence of an Authorization header.
 * - Initializing or joining a battle with the help of BattleController.
 * - Waiting for the battle to complete in a blocking manner.
 * - Returning the battle logs on successful completion.
 *
 * Responses:
 * - Returns UNAUTHORIZED if the Authorization header is missing or invalid.
 * - Returns BAD_REQUEST for invalid requests or issues during battle initialization.
 * - Returns the battle logs if the battle completes successfully.
 * - Returns an empty response with BAD_REQUEST for unsupported methods.
 */
public class BattleService implements Service {
    private final BattleController battleController;

    /**
     * Constructs a new instance of the BattleService class.
     *
     * This constructor initializes a new instance of the BattleController,
     * which is used to handle the core operations associated with battles,
     * including initialization, execution, and log retrieval.
     */
    public BattleService() {
        this.battleController = new BattleController();
    }

    /**
     * Handles incoming HTTP requests, specifically focused on processing POST requests
     * for initiating and managing battles. The method validates the request, interacts
     * with the BattleController to initialize and manage battles, and returns appropriate
     * responses based on the request and processing outcomes.
     *
     * @param request the HTTP request object containing method type, headers, and body
     *                required for processing.
     * @return a Response object representing the corresponding HTTP response.
     *         - Returns `UNAUTHORIZED` if the "Authorization" header is missing or invalid.
     *         - Returns `BAD_REQUEST` if the request is invalid or battle initialization fails.
     *         - Returns the battle logs in case of a successfully completed battle.
     *         - Returns an empty response with `BAD_REQUEST` for unsupported methods.
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

            Battle battle = battleController.initBattle(request);

            if (battle == null) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "[]"
                );
            }

            battleController.waitForBattle(battle); // blocking!

            return battleController.getLog(battle);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}