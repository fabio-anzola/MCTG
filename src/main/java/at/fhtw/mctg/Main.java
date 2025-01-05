package at.fhtw.mctg;

import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.mctg.service.battle.BattleService;
import at.fhtw.mctg.service.card.CardService;
import at.fhtw.mctg.service.deck.DeckService;
import at.fhtw.mctg.service.pack.PackService;
import at.fhtw.mctg.service.scoreboard.ScoreboardService;
import at.fhtw.mctg.service.session.SessionService;
import at.fhtw.mctg.service.stats.StatService;
import at.fhtw.mctg.service.trade.TradeService;
import at.fhtw.mctg.service.transaction.TransactionService;
import at.fhtw.mctg.service.users.UserService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter() {
        Router router = new Router();
        router.addService("/users", new UserService());
        router.addService("/sessions", new SessionService());
        router.addService("/packages", new PackService());
        router.addService("/transactions", new TransactionService()); // /transactions/packages
        router.addService("/cards", new CardService());
        router.addService("/deck", new DeckService());
        router.addService("/stats", new StatService());
        router.addService("/scoreboard", new ScoreboardService());
        // Not yet fully implemented
        router.addService("/battles", new BattleService());
        router.addService("/tradings", new TradeService());


        return router;
    }

}
