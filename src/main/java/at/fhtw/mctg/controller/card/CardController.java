package at.fhtw.mctg.controller.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.Controller;
import at.fhtw.mctg.dal.Repository.CardRepository;
import at.fhtw.mctg.dal.Repository.UserRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.Card;
import at.fhtw.mctg.model.CardType;
import at.fhtw.mctg.model.Elements;
import at.fhtw.mctg.model.User;
import at.fhtw.mctg.utils.PasswordHash;

import java.util.Arrays;

public class CardController extends Controller {
    public Response addMultipleCards(Request request, int packId) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            Card[] requestedCards = this.getObjectMapper().readValue(request.getBody(), Card[].class);

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

            for (Card requestedCard : requestedCards) {
                requestedCard.setPackageId(packId);
                if (requestedCard.getName().contains("Spell")) {
                    requestedCard.setType(CardType.SPELL);

                    if (requestedCard.getName().contains("Water")) {
                        requestedCard.setElement(Elements.WATER);
                    }
                    else if (requestedCard.getName().contains("Fire")) {
                        requestedCard.setElement(Elements.FIRE);
                    }
                    else {
                        requestedCard.setElement(Elements.NORMAL);
                    }
                }
                else {
                    requestedCard.setType(CardType.MONSTER);
                    requestedCard.setElement(Elements.NORMAL);
                }

                // requestedCard.setType();
                // requestedCard.setElement();
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
}