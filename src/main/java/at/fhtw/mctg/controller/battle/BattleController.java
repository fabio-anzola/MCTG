package at.fhtw.mctg.controller.battle;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mctg.controller.Controller;
import at.fhtw.mctg.controller.session.SessionController;
import at.fhtw.mctg.dal.Repository.BattleRepository;
import at.fhtw.mctg.dal.UnitOfWork;
import at.fhtw.mctg.model.Battle;
import at.fhtw.mctg.model.BattleLog;
import at.fhtw.mctg.threads.BattleRunner;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * App controller for Battle Routes
 */
public class BattleController extends Controller {

    /**
     * Method to initialize a battle on request
     *
     * @param request req by user
     * @return the battle
     */
    public Battle initBattle(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            String requestingUser = new SessionController().getUserByToken(request);

            ArrayList<Battle> availableBattles = (ArrayList<Battle>) new BattleRepository(unitOfWork).getPendingBattles();

            Battle battle;

            boolean startThread = false;

            if (availableBattles.isEmpty()) {
                // create new battle
                battle = new BattleRepository(unitOfWork).createNewBattle(requestingUser);
            } else {
                battle = availableBattles.get(0);

                // join battle
                new BattleRepository(unitOfWork).joinBattle(battle.getBattleId(), requestingUser);

                startThread = true;
            }

            unitOfWork.commitTransaction();

            if (startThread) {
                // Create new Battle Thread
                Runnable task = new BattleRunner(battle);
                Thread battleThread = new Thread(task);

                // Start the Battle Thread
                battleThread.start();
            }

            return battle;
        } catch (Exception e) {
            e.printStackTrace();

            unitOfWork.rollbackTransaction();
            return null;
        }
    }


    /**
     * Method to wait until battle completes
     *
     * @param battle the battle to check
     */
    public void waitForBattle(Battle battle) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            while (!new BattleRepository(unitOfWork).checkBattleComplete(battle.getBattleId())) {
                sleep(1000);
            }

            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();

            unitOfWork.rollbackTransaction();
        }
    }

    /**
     * Method to get the log of a Battle
     * @param battle the battle to get the log from
     * @return the Response for the user
     */
    public Response getLog(Battle battle) {
        UnitOfWork unitOfWork = new UnitOfWork();

        try (unitOfWork) {
            ArrayList<BattleLog> bl = (ArrayList<BattleLog>) new BattleRepository(unitOfWork).getBattleLog(battle.getBattleId());

            unitOfWork.commitTransaction();

            StringBuilder log = new StringBuilder();

            for (BattleLog battleLog : bl) {
                log
                        .append(battleLog.getRowNr())
                        .append(": ")
                        .append(battleLog.getLogRow())
                        .append("\n");
            }

            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    log.toString()
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
