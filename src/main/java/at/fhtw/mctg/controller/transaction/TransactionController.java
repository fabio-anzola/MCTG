package at.fhtw.mctg.controller.transaction;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.Controller;
import at.fhtw.mctg.controller.session.SessionController;
import at.fhtw.mctg.dal.Repository.CardRepository;
import at.fhtw.mctg.dal.Repository.PackageRepository;
import at.fhtw.mctg.dal.Repository.UserRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.CardPack;
import at.fhtw.mctg.model.User;

import java.util.ArrayList;

/**
 * App controller for Transaction Routes
 */
public class TransactionController extends Controller {

    /**
     * Method to acquire a pack
     *
     * @param request request by the user
     * @return the json status message
     */
    public Response acquirePack(Request request) {
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

            // get pack obj to acquire
            CardPack pack = new PackageRepository(unitOfWork).getFreePack();

            if (pack == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"No card package available for buying\" }"
                );
            }

            if (user.getWallet() < pack.getPrice()) {
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Not enough money for buying a card package\" }"
                );
            }

            new CardRepository(unitOfWork).acquireMultipleCards(pack.getPackageId(), user.getUserId());
            new UserRepository(unitOfWork).updateWalletByName(requestingUser, -1 * pack.getPrice());

            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"message\" : \"A package has been successfully bought\" }"
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

