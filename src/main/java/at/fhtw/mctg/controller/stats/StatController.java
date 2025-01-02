package at.fhtw.mctg.controller.stats;

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

public class StatController extends Controller {
    public Response getStatsByUid(Request request) {
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

            ArrayList<Integer> stats = ((ArrayList<Integer>)new UserRepository(unitOfWork).getUserStatsByName(requestingUser));

            String json = String.format(
                    "{ \"Name\": \"%s\", \"Elo\": %d, \"Wins\": %d, \"Losses\": %d}",
                    user.getName(),
                    stats.get(0), // ELO
                    stats.get(1), // WINS
                    stats.get(2) // LOSS
            );

            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    json
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
