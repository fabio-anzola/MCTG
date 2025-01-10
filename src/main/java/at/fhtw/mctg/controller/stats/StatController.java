package at.fhtw.mctg.controller.stats;

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

/**
 * App controller for Stat Routes
 */
public class StatController extends Controller {

    /**
     * Get user stats by user id
     *
     * @param request users request
     * @return the response with the stats
     */
    public Response getStatsByUid(Request request) {
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

            // get stats
            Stats stats = new UserRepository(unitOfWork).getUserStatsByName(requestingUser);

            unitOfWork.commitTransaction();

            ObjectMapper objectMapper = new ObjectMapper();
            String sbJson = objectMapper.writeValueAsString(stats);
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
