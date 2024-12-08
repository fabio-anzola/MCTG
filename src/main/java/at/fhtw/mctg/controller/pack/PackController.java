package at.fhtw.mctg.controller.pack;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.Controller;
import at.fhtw.mctg.dal.Repository.PackageRepository;
import at.fhtw.mctg.dal.Repository.UserRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.CardPack;
import at.fhtw.mctg.model.User;
import at.fhtw.mctg.utils.PasswordHash;

import java.util.Arrays;

public class PackController extends Controller {

    public int createPack(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            int id = new PackageRepository(unitOfWork).createPackage();

            unitOfWork.commitTransaction();

            return id;

        } catch (Exception e) {
            e.printStackTrace();

            unitOfWork.rollbackTransaction();
            return -1;
        }
    }
}
