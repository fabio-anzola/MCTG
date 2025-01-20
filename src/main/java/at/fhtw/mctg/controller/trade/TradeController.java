package at.fhtw.mctg.controller.trade;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.Controller;
import at.fhtw.mctg.controller.session.SessionController;
import at.fhtw.mctg.dal.Repository.CardRepository;
import at.fhtw.mctg.dal.Repository.TradeRepository;
import at.fhtw.mctg.dal.Repository.UserRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * App controller for Trade Routes
 */
public class TradeController extends Controller {

    /**
     * Method to get trade in pending state
     *
     * @param request the request by the user
     * @return response with trades
     */
    public Response getAvailableTrades(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            String requestingUser = new SessionController().getUserByToken(request);

            // get the user
            ArrayList<User> users = ((ArrayList<User>) new UserRepository(unitOfWork).getUserByName(requestingUser));
            if (users.isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Token Not Accepted\" }"
                );
            }
            User user = users.get(0);

            // get open trades
            ArrayList<Trade> trades = (ArrayList<Trade>) new TradeRepository(unitOfWork).getPendingTrades();

            unitOfWork.commitTransaction();

            if (trades.isEmpty()) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "[]"
                );
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String sbJson = objectMapper.writeValueAsString(trades);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    sbJson
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
     * Method to create a new trade
     *
     * @param request request containing the trade info
     * @return the created trade as response
     */
    public Response createNewTrade(Request request) {
        // check if user is valid
        // check if card belongs to user
        // check if card is active (in deck)
        // check if there is already a trade with the card

        UnitOfWork unitOfWork = new UnitOfWork();
        try (unitOfWork) {
            String requestingUser = new SessionController().getUserByToken(request);

            // Get user
            ArrayList<User> users = ((ArrayList<User>) new UserRepository(unitOfWork).getUserByName(requestingUser));
            if (users.isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Token Not Accepted\" }"
                );
            }
            User user = users.get(0);

            // get trade info as object
            Trade reqTrade = this.getObjectMapper().readValue(request.getBody(), Trade.class);

            // Check if card exists
            ArrayList<Card> tradeCards = (ArrayList<Card>) new CardRepository(unitOfWork).getCardById(reqTrade.getSenderCardId());
            if (tradeCards.size() != 1) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Requested card is invalid\" }"
                );
            }

            // Check if user is owner
            Card card = tradeCards.get(0);
            if (card.getUserId() != user.getUserId()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"The deal contains a card that is not owned by the user or locked in the deck.\" }"
                );
            }

            // Check if Card is in deck
            if (card.isActive()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"The deal contains a card that is not owned by the user or locked in the deck.\" }"
                );
            }

            // Check if there is already a trade with this card pending
            if (!new TradeRepository(unitOfWork).getPendingTradeByCard(card.getCardId()).isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"The deal contains a card that is already in another trade.\" }"
                );
            }

            // Check if there is already a trade with this id
            if (!new TradeRepository(unitOfWork).getTradeById(reqTrade.getTradeId()).isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"A deal with this deal ID already exists.\" }"
                );
            }

            // Create trade
            reqTrade.setStatus(TradeStatus.PENDING);
            reqTrade.setInitiatorId(user.getUserId());
            Trade trade = new TradeRepository(unitOfWork).createTrade(reqTrade);

            unitOfWork.commitTransaction();

            ObjectMapper objectMapper = new ObjectMapper();
            String sbJson = objectMapper.writeValueAsString(trade);
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    sbJson
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
     * Method to delete a trade
     *
     * @param request request by user
     * @return the message
     */
    public Response deleteTrade(Request request) {
        String tradeId = request.getPathParts().get(1); //the trade we want to delete

        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            String requestingUser = new SessionController().getUserByToken(request);

            // get the user
            ArrayList<User> users = ((ArrayList<User>) new UserRepository(unitOfWork).getUserByName(requestingUser));
            if (users.isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Token Not Accepted\" }"
                );
            }
            User user = users.get(0);

            // get the trade by id
            ArrayList<Trade> trades = (ArrayList<Trade>) new TradeRepository(unitOfWork).getTradeById(tradeId);

            // check if trade exists
            if (trades.isEmpty()) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"The provided deal ID was not found.\" }"
                );
            }

            // get trade
            Trade trade = trades.get(0);

            // check if is own trade
            if (trade.getInitiatorId() != user.getUserId()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Can only delete own trades.\" }"
                );
            }

            // delete trade
            new TradeRepository(unitOfWork).deleteTradeById(trade.getTradeId());

            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\" : \"Trading deal successfully deleted.\" }"
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
     * Method to join trade
     *
     * @param request request by user
     * @return a status message
     */
    public Response joinTrade(Request request) {
        // check if user is valid
        // check if trade exists
        // check if tradepartner is not self
        // check if card is owned
        // check if card is active
        // check if card meets requirements (type & damage)

        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            String requestingUser = new SessionController().getUserByToken(request);

            // get user
            ArrayList<User> users = ((ArrayList<User>) new UserRepository(unitOfWork).getUserByName(requestingUser));
            if (users.isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Token Not Accepted\" }"
                );
            }
            User user = users.get(0);

            // Check if card exists
            ArrayList<Card> cardList = (ArrayList<Card>) new CardRepository(unitOfWork).getCardById(request.getBody());
            if (cardList.size() != 1) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Requested card is invalid\" }"
                );
            }

            // Card data
            Card card = cardList.get(0);

            // Requested trade id
            String tradeId = request.getPathParts().get(1);

            // Check if trade exists
            ArrayList<Trade> tradeList = (ArrayList<Trade>) new TradeRepository(unitOfWork).getTradeById(tradeId);
            if (tradeList.isEmpty()) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"The provided deal ID was not found.\" }"
                );
            }

            // Trade Data
            Trade trade = tradeList.get(0);

            // Is deal pending?
            if (trade.getStatus() != TradeStatus.PENDING) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Trade is not pending anymore.\" }"
                );
            }

            // Cannot deal with oneself
            if (trade.getInitiatorId() == user.getUserId()) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"The user tries to trade with self.\" }"
                );
            }

            // Check if user is owner of card
            if (card.getUserId() != user.getUserId()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"The offered card is not owned by the user.\" }"
                );
            }

            // Check if Card is in deck
            if (card.isActive()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"The offered card is locked in the deck.\" }"
                );
            }

            // Check card requirements
            if (card.getDamage() < trade.getRequestedDamage() || trade.getRequestedType() != card.getType()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"The requirements are not met (Type, MinimumDamage).\" }"
                );
            }

            // Check if there is already a trade with this card pending
            if (!new TradeRepository(unitOfWork).getPendingTradeByCard(card.getCardId()).isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"The deal contains a card that is already in another trade.\" }"
                );
            }

            // Create trade
            trade.setStatus(TradeStatus.ACCEPTED);
            trade.setPartnerId(user.getUserId());
            trade.setReceiverCardId(card.getCardId());
            trade.setTimeCompleted(new Timestamp(System.currentTimeMillis()));
            new TradeRepository(unitOfWork).updateTrade(trade);

            // remove cards vice versa
            new CardRepository(unitOfWork).updateOwner(trade.getSenderCardId(), trade.getPartnerId());
            new CardRepository(unitOfWork).updateOwner(trade.getReceiverCardId(), trade.getInitiatorId());

            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\" : \"Trading deal successfully executed.\" }"
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
