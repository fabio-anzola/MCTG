package at.fhtw.mctg.controller.pack;

import at.fhtw.httpserver.server.Request;
import at.fhtw.mctg.controller.Controller;
import at.fhtw.mctg.dal.Repository.PackageRepository;
import at.fhtw.mctg.dal.UnitOfWork;

/**
 * App controller for Pack Routes
 */
public class PackController extends Controller {

    /**
     * Method to create a pack
     *
     * @param request request from user
     * @return the pack id
     */
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
