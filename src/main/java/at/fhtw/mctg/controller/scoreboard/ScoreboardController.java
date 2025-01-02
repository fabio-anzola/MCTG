package at.fhtw.mctg.controller.scoreboard;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.Controller;
import at.fhtw.mctg.controller.session.SessionController;
import at.fhtw.mctg.dal.Repository.UserRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.Stats;
import at.fhtw.mctg.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class ScoreboardController extends Controller {
    public Response getScoreboard(Request request) {
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

            User user = users.get(0);

            ArrayList<Stats> scoreboard = ((ArrayList<Stats>)new UserRepository(unitOfWork).getOrderedStats());

            unitOfWork.commitTransaction();

            ObjectMapper objectMapper = new ObjectMapper();
            String sbJson = objectMapper.writeValueAsString(scoreboard);
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
