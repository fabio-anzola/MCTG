package at.fhtw.mctg.controller.deck;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.Controller;
import at.fhtw.mctg.controller.session.SessionController;
import at.fhtw.mctg.dal.Repository.CardRepository;
import at.fhtw.mctg.dal.Repository.UserRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.Card;
import at.fhtw.mctg.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

/**
 * App controller for Deck Routes
 */
public class DeckController extends Controller {

    /**
     * Method to get active cards by user id
     *
     * @param request the request by the user
     * @return response containing the active cards
     */
    public Response getActiveCards(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            String requestingUser = new SessionController().getUserByToken(request);

            // check if requesting user exists
            ArrayList<User> users = ((ArrayList<User>) new UserRepository(unitOfWork).getUserByName(requestingUser));
            if (users.isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Token Not Accepted\" }"
                );
            }

            // get user object
            User user = users.get(0);

            // get active cards
            ArrayList<Card> cards = (ArrayList<Card>) new CardRepository(unitOfWork).getActiveCardsByUserId(user.getUserId());

            // check if there are more that 0 cards
            if (cards.isEmpty()) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "[]"
                );
            }

            unitOfWork.commitTransaction();

            ObjectMapper objectMapper = new ObjectMapper();
            String cardsJson = objectMapper.writeValueAsString(cards);

            if (request.getParams() == null) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        cardsJson
                );
            } else if (request.getParams().contains("format=plain")) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.PLAIN_TEXT,
                        cardsJson
                );
            } else {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\" : \"Unsupported format type\" }"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();

            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    /**
     * Method to set an array of cards as active for a user
     *
     * @param request the users Request containing an array of cards
     * @return Confirmation message
     */
    public Response setActiveCards(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            String requestingUser = new SessionController().getUserByToken(request);

            // Get User
            ArrayList<User> users = ((ArrayList<User>) new UserRepository(unitOfWork).getUserByName(requestingUser));
            if (users.isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Token Not Accepted\" }"
                );
            }
            User user = users.get(0);

            // Get Ids from request body
            String[] cardIdArray = new ObjectMapper().readValue(request.getBody(), String[].class);

            // Check if request has 4 cards
            if (cardIdArray.length != 4) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\" : \"The provided deck did not include the required amount of cards\" }"
                );
            }

            // Check if all requested cards belong to the user
            for (String s : cardIdArray) {
                ArrayList<Card> tempCardCheck = (ArrayList<Card>) (new CardRepository(unitOfWork).getCardById(s));

                // Check if card exists
                if (tempCardCheck.isEmpty()) {
                    return new Response(
                            HttpStatus.FORBIDDEN,
                            ContentType.JSON,
                            "{ \"message\" : \"At least one of the provided cards does not belong to the user or is not available. (LEER)\" }"
                    );
                }

                // Check if card belongs to the user
                if (tempCardCheck.get(0).getUserId() != user.getUserId()) {
                    return new Response(
                            HttpStatus.FORBIDDEN,
                            ContentType.JSON,
                            "{ \"message\" : \"At least one of the provided cards does not belong to the user or is not available.\" }"
                    );
                }
            }

            // Check if user has no other cards already active
            ArrayList<Card> currActiveCards = (ArrayList<Card>) new CardRepository(unitOfWork).getActiveCardsByUserId(user.getUserId());
            if (!currActiveCards.isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Deck already chosen\" }"
                );
            }

            // Set requested cards as active
            for (String s : cardIdArray) {
                new CardRepository(unitOfWork).setCardAsActive(s);
            }

            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\" : \"The deck has been successfully configured\"}"
            );

        } catch (Exception e) {
            e.printStackTrace();

            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }
}
