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
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatsTests {

    private static final String BASE_URL = "http://localhost:10001";
    private static final String AD_AUTH_TOKEN = "Bearer admin-mtcgToken";
    private static final String KB_AUTH_TOKEN = "Bearer kienboec-mtcgToken";
    private static final String AH_AUTH_TOKEN = "Bearer altenhof-mtcgToken";
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    @Order(1)
    void testUserStats() throws Exception {
        // Test: Get stats for user "kienboec"
        HttpRequest getStatsKienboecRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/stats"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getStatsKienboecResponse = client.send(getStatsKienboecRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getStatsKienboecResponse.statusCode(), "Expected HTTP 200 for fetching stats of 'kienboec'");

        // Test: Get stats for user "altenhof"
        HttpRequest getStatsAltenhofRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/stats"))
                .header("Authorization", AH_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getStatsAltenhofResponse = client.send(getStatsAltenhofRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getStatsAltenhofResponse.statusCode(), "Expected HTTP 200 for fetching stats of 'altenhof'");
    }

    @Test
    @Order(2)
    void testGetScoreboard() throws Exception {
        // Test: Get the scoreboard with authorization
        HttpRequest getScoreboardRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/scoreboard"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getScoreboardResponse = client.send(getScoreboardRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getScoreboardResponse.statusCode(), "Expected HTTP 200 for fetching the scoreboard");
    }
}