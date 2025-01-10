package at.fhtw.mctg.controller.users;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.Controller;
import at.fhtw.mctg.controller.session.SessionController;
import at.fhtw.mctg.dal.Repository.UserRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.User;
import at.fhtw.mctg.utils.PasswordHash;

import java.util.ArrayList;
import java.util.Collection;

/**
 * App controller for Transaction Routes
 */
public class UserController extends Controller {

    /**
     * Method to create a user
     *
     * @param request request containing user info
     * @return response with status message
     */
    public Response createUser(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            // User info
            User requestedUser = this.getObjectMapper().readValue(request.getBody(), User.class);

            // check if user exists by username
            if (!new UserRepository(unitOfWork).getUserByName(requestedUser.getUsername()).isEmpty()) {
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.JSON,
                        "{ \"message\" : \"User exists already\" }"
                );
            }

            // set encrypted password
            requestedUser.setPassword(PasswordHash.generateHashedPassword(requestedUser.getPassword()));

            // store user
            new UserRepository(unitOfWork).createUser(requestedUser);

            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"message\" : \"User created successfully\" }"
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
     * Method to get user by username
     *
     * @param request request by user
     * @return the user data
     */
    public Response getUserByName(Request request) {
        String username = request.getPathParts().get(1); //the user we want to see

        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {

            // get requesting user data
            String requestingUser = new SessionController().getUserByToken(request);
            ArrayList<User> tokenUsers = ((ArrayList<User>) new UserRepository(unitOfWork).getUserByName(requestingUser));
            if (tokenUsers.isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Token Not Accepted\" }"
                );
            }

            // get requested user
            Collection<User> dbUsers = new UserRepository(unitOfWork).getUserByName(requestingUser);
            if (dbUsers.isEmpty()) {
                unitOfWork.rollbackTransaction();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"User not found\" }"
                );
            }

            User user = ((ArrayList<User>) dbUsers).get(0);

            // only allow to get own user
            if (!user.getUsername().equals(username)) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Cannot get other user\" }"
                );
            }

            User reqUser = ((ArrayList<User>) new UserRepository(unitOfWork).getUserByName(username)).get(0);

            String UserDataJSON = this.getObjectMapper().writeValueAsString(reqUser);
            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    UserDataJSON
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
     * Method to update user by username for bio, name, image, password
     *
     * @param request requests by user containing user data
     * @return the status message
     */
    public Response updateUserByName(Request request) {
        String username = request.getPathParts().get(1);

        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            // get token user data
            String requestingUser = new SessionController().getUserByToken(request);
            ArrayList<User> tokenUsers = ((ArrayList<User>) new UserRepository(unitOfWork).getUserByName(requestingUser));
            if (tokenUsers.isEmpty()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Token Not Accepted\" }"
                );
            }

            // get user
            Collection<User> dbUsers = new UserRepository(unitOfWork).getUserByName(requestingUser);
            if (dbUsers.isEmpty()) {
                unitOfWork.rollbackTransaction();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"User not found\" }"
                );
            }

            User user = ((ArrayList<User>) dbUsers).get(0);

            // only allow own user
            if (!user.getUsername().equals(username)) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Cannot update other user\" }"
                );
            }

            User extractedUser = this.getObjectMapper().readValue(request.getBody(), User.class);

            // update values possible for bio, name, image, password
            if (extractedUser.getBio() != null) {
                user.setBio(extractedUser.getBio());
            }
            if (extractedUser.getName() != null) {
                user.setName(extractedUser.getName());
            }
            if (extractedUser.getImage() != null) {
                user.setImage(extractedUser.getImage());
            }
            if (extractedUser.getPassword() != null) {
                user.setPassword(PasswordHash.generateHashedPassword(extractedUser.getPassword()));
            }

            new UserRepository(unitOfWork).updateUserByName(username, user);

            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\" : \"User successfully updated\" }"
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
