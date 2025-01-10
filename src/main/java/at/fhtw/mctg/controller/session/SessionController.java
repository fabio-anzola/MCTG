package at.fhtw.mctg.controller.session;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.Controller;
import at.fhtw.mctg.dal.Repository.SessionRepository;
import at.fhtw.mctg.dal.Repository.UserRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.Session;
import at.fhtw.mctg.model.Token;
import at.fhtw.mctg.model.User;
import at.fhtw.mctg.utils.JWTGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import static at.fhtw.mctg.utils.PasswordHash.verifyPassword;

/**
 * App controller for Session Routes
 */
public class SessionController extends Controller {

    /**
     * Method to get username by token
     * Adjusted by lektors specification
     *
     * @param request request containing auth cookie
     * @return the username
     */
    public String getUserByToken(Request request) {

        String token = request.getHeaderMap().getHeader("Authorization").split(" ")[1];

        // TODO: change after submission to use jwt token
        String[] parts = token.split("-");
        return parts[0];
    }

    /**
     * Method to create a new session
     * Adjusted by lektors specification
     *
     * @param request the users request
     * @return the response containing the tokens
     */
    public Response createSession(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            Session input = this.getObjectMapper().readValue(request.getBody(), Session.class);

            // get user info
            ArrayList<User> dbUsers = (ArrayList<User>) new UserRepository(unitOfWork).getUserByName(input.getUsername());
            if (dbUsers.isEmpty()) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"User not found\" }"
                );
            }
            User user = dbUsers.get(0);

            if (!verifyPassword(input.getPassword(), user.getPassword())) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Password doesn't match\" }"
                );
            }

            // TODO: Remove after intermediate submission
            if (1 == 1) { // trick java compiler that code after this is not unreachable
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        user.getUsername() + "-mtcgToken"
                );
            }

            // Check if there is a non expired token
            Collection<Token> tokens = new SessionRepository(unitOfWork).getTokenByUsername(user.getUsername());

            if (!tokens.isEmpty()) {
                // Get latest token
                Token latestToken = ((ArrayList<Token>) tokens).get(tokens.size() - 1);

                // Get expire date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime exp = LocalDateTime.parse(latestToken.getExpires().substring(0, 19), formatter);

                if (exp.isAfter(LocalDateTime.now())) {
                    // Token is still valid
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            this.getObjectMapper().writeValueAsString(latestToken)
                    );
                }
            }

            // No token yet or expired
            // Create token
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payload = "{\"user\":" + user.getUsername() + ",\"role\":" + user.getUserId() + "}";
            String jwtToken = JWTGenerator.createJWT(header, payload);

            // save token to db
            new SessionRepository(unitOfWork).createToken(user.getUserId(), jwtToken);

            // retrieve latest token
            tokens = new SessionRepository(unitOfWork).getTokenByUsername(user.getUsername());
            Token latestToken = ((ArrayList<Token>) tokens).get(tokens.size() - 1);

            // Commit work
            unitOfWork.commitTransaction();

            // return token
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    this.getObjectMapper().writeValueAsString(latestToken)
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
