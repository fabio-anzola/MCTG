package at.fhtw.mctg;

import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.mctg.service.echo.EchoService;
import at.fhtw.mctg.service.session.SessionService;
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

    private static Router configureRouter()
    {
        Router router = new Router();
        router.addService("/users", new UserService());
        router.addService("/sessions", new SessionService());
        router.addService("/echo", new EchoService());

        return router;
    }

}
