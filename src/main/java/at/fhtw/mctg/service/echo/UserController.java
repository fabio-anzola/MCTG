package at.fhtw.mctg.service.echo;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.Controller;
import at.fhtw.mctg.dal.Repository.UserRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.User;

import java.util.ArrayList;
import java.util.Collection;

public class UserController extends Controller {

    public Response createUser(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            User requestedUser = this.getObjectMapper().readValue(request.getBody(), User.class);

            if (!new UserRepository(unitOfWork).getUserByName(requestedUser.getUsername()).isEmpty()) {
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.JSON,
                        "{ \"message\" : \"User exists already\" }"
                );
            }

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

    public Response getUserByName(Request request) {
        String username = request.getPathParts().get(1);

        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {

            User reqUser = ((ArrayList<User>)new UserRepository(unitOfWork).getUserByName(username)).get(0);
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

    public Response updateUserByName(Request request) {
        String username = request.getPathParts().get(1);

        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {

            User extractedUser = this.getObjectMapper().readValue(request.getBody(), User.class);

            Collection<User> dbUsers = new UserRepository(unitOfWork).getUserByName(username);

            if (dbUsers.isEmpty()) {
                unitOfWork.rollbackTransaction();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"User not found\" }"
                );
            }

            User user = ((ArrayList<User>)dbUsers).get(0);

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
                user.setPassword(extractedUser.getPassword());
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
