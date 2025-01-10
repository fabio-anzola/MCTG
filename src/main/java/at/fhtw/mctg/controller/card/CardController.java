package at.fhtw.mctg.controller.card;

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
import at.fhtw.mctg.model.CardType;
import at.fhtw.mctg.model.Elements;
import at.fhtw.mctg.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

/**
 * App controller for Card Routes
 */
public class CardController extends Controller {

    /**
     * Method to add multiple cards to a pack
     *
     * @param request
     * @param packId
     * @return
     */
    public Response addMultipleCards(Request request, int packId) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            Card[] requestedCards = this.getObjectMapper().readValue(request.getBody(), Card[].class);

            // Check if ids are unique
            for (Card requestedCard : requestedCards) {
                if (!new CardRepository(unitOfWork).getCardById(requestedCard.getCardId()).isEmpty()) {
                    unitOfWork.rollbackTransaction();
                    return new Response(
                            HttpStatus.CONFLICT,
                            ContentType.JSON,
                            "{ \"message\" : \"At least one card in the packages already exists\" }"
                    );
                }
            }

            // Generate objects
            for (Card requestedCard : requestedCards) {
                // set pack id
                requestedCard.setPackageId(packId);

                // check if card is spell or monster
                if (requestedCard.getName().contains("Spell")) {
                    requestedCard.setType(CardType.SPELL);
                } else {
                    requestedCard.setType(CardType.MONSTER);
                }

                // check element of card
                if (requestedCard.getName().contains("Water")) {
                    requestedCard.setElement(Elements.WATER);
                } else if (requestedCard.getName().contains("Fire")) {
                    requestedCard.setElement(Elements.FIRE);
                } else {
                    requestedCard.setElement(Elements.NORMAL);
                }

                // save object to db
                new CardRepository(unitOfWork).createCard(requestedCard);
            }

            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"message\" : \"Package and cards successfully created\"}"
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

    /**
     * Method to get cards based on the associated user id
     *
     * @param request request by user
     * @return the Response with the cards
     */
    public Response getCardsByUid(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            String requestingUser = new SessionController().getUserByToken(request);

            // Check if requesting user exists
            ArrayList<User> users = ((ArrayList<User>) new UserRepository(unitOfWork).getUserByName(requestingUser));
            if (users.isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Token Not Accepted\" }"
                );
            }

            // Get object of requesting user
            User user = users.get(0);

            // Get cards by user id
            ArrayList<Card> cards = (ArrayList<Card>) new CardRepository(unitOfWork).getCardsByUserId(user.getUserId());

            // Check if there are more then 0 cards
            if (cards.isEmpty()) {
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.JSON,
                        "{ \"message\" : \"The request was fine, but the user doesn't have any cards\" }"
                );
            }

            unitOfWork.commitTransaction();

            ObjectMapper objectMapper = new ObjectMapper();
            String cardsJson = objectMapper.writeValueAsString(cards);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    cardsJson
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
