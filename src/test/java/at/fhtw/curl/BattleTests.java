package at.fhtw.curl;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BattleTests {

    private static final String BASE_URL = "http://localhost:10001";
    private static final String AD_AUTH_TOKEN = "Bearer admin-mtcgToken";
    private static final String KB_AUTH_TOKEN = "Bearer kienboec-mtcgToken";
    private static final String AH_AUTH_TOKEN = "Bearer altenhof-mtcgToken";
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    @Order(1)
    void testBattle() throws Exception {
        Thread kienboecThread = new Thread(() -> {
            try {
                // kienboec battle request
                HttpRequest kienboecBattleRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/battles"))
                        .header("Authorization", KB_AUTH_TOKEN)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> kienboecResponse = client.send(kienboecBattleRequest, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, kienboecResponse.statusCode(), "Expected HTTP 200 for kienboec battle initiation");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread altenhofThread = new Thread(() -> {
            try {
                // altenhof battle request
                HttpRequest altenhofBattleRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/battles"))
                        .header("Authorization", AH_AUTH_TOKEN)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> altenhofResponse = client.send(altenhofBattleRequest, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, altenhofResponse.statusCode(), "Expected HTTP 200 for altenhof battle initiation");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Start both threads
        kienboecThread.start();
        Thread.sleep(2000);
        altenhofThread.start();

        // Wait for both threads to finish
        kienboecThread.join();
        altenhofThread.join();
    }
}