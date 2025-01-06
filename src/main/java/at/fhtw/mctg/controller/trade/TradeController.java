package at.fhtw.mctg.controller.trade;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.Controller;
import at.fhtw.mctg.controller.session.SessionController;
import at.fhtw.mctg.dal.Repository.TradeRepository;
import at.fhtw.mctg.dal.Repository.UserRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.Stats;
import at.fhtw.mctg.model.Trade;
import at.fhtw.mctg.model.User;
import at.fhtw.mctg.service.trade.TradeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class TradeController extends Controller {
    public Response getAvailableTrades(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            String requestingUser = new SessionController().getUserByToken(request);

            ArrayList<User> users = ((ArrayList<User>)new UserRepository(unitOfWork).getUserByName(requestingUser));
            if (users.isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Token Not Accepted\" }"
                );
            }

            // User Data of requesting user
            User user = users.get(0);

            ArrayList<Trade> trades = (ArrayList<Trade>) new TradeRepository(unitOfWork).getPendingTrades();

            unitOfWork.commitTransaction();

            if (trades.isEmpty()) {
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.JSON,
                        "{ \"message\" : \"The request was fine, but there are no trading deals available\" }"
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
}
