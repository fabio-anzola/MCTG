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
public class TradeTests {

    private static final String BASE_URL = "http://localhost:10001";
    private static final String AD_AUTH_TOKEN = "Bearer admin-mtcgToken";
    private static final String KB_AUTH_TOKEN = "Bearer kienboec-mtcgToken";
    private static final String AH_AUTH_TOKEN = "Bearer altenhof-mtcgToken";
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    @Order(1)
    void testCheckTradingDealsAndCreateTrade() throws Exception {
        // Check existing trading deals
        HttpRequest checkDealsRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tradings"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> checkDealsResponse = client.send(checkDealsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, checkDealsResponse.statusCode(), "Expected HTTP 200 when checking trading deals");

        // Create a trading deal
        String tradingDealPayload = """
            {
                "Id": "6cd85277-4590-49d4-b0cf-ba0a921faad0",
                "CardToTrade": "1cb6ab86-bdb2-47e5-b6e4-68c5ab389334",
                "Type": "MONSTER",
                "MinimumDamage": 15
            }
            """;

        HttpRequest createDealRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tradings"))
                .header("Authorization", KB_AUTH_TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(tradingDealPayload))
                .build();

        HttpResponse<String> createDealResponse = client.send(createDealRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createDealResponse.statusCode(), "Expected HTTP 201 when creating a trading deal");
    }

    @Test
    @Order(2)
    void testCheckTradingDealsAsKienboec() throws Exception {
        // Check trading deals for kienboec
        HttpRequest checkDealsRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tradings"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> checkDealsResponse = client.send(checkDealsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, checkDealsResponse.statusCode(), "Expected HTTP 200 when checking trading deals as kienboec");
    }

    @Test
    @Order(3)
    void testCheckTradingDealsAsAltenhof() throws Exception {
        // Check trading deals for altenhof
        HttpRequest checkDealsRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tradings"))
                .header("Authorization", AH_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> checkDealsResponse = client.send(checkDealsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, checkDealsResponse.statusCode(), "Expected HTTP 200 when checking trading deals as altenhof");
    }

    @Test
    @Order(4)
    void testDeleteTradingDealAsKienboec() throws Exception {
        // Define the ID of the trading deal to delete
        String tradingDealId = "6cd85277-4590-49d4-b0cf-ba0a921faad0";

        // Create DELETE request
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tradings/" + tradingDealId))
                .header("Authorization", KB_AUTH_TOKEN)
                .DELETE()
                .build();

        // Send the request
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        // Validate response
        assertEquals(200, deleteResponse.statusCode(), "Expected HTTP 2xx when deleting the trading deal as kienboec");
    }

    @Test
    @Order(5)
    void testCheckTradingDealsAndCreateNewTrade() throws Exception {
        // Check existing trading deals
        HttpRequest checkDealsRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tradings"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> checkDealsResponse = client.send(checkDealsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, checkDealsResponse.statusCode(), "Expected HTTP 200 when checking trading deals");

        // Create a trading deal
        String tradingDealPayload = """
            {
                "Id": "6cd85277-4590-49d4-b0cf-ba0a921faad0",
                "CardToTrade": "1cb6ab86-bdb2-47e5-b6e4-68c5ab389334",
                "Type": "MONSTER",
                "MinimumDamage": 15
            }
            """;

        HttpRequest createDealRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tradings"))
                .header("Authorization", KB_AUTH_TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(tradingDealPayload))
                .build();

        HttpResponse<String> createDealResponse = client.send(createDealRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createDealResponse.statusCode(), "Expected HTTP 201 when creating a trading deal");
    }

    @Test
    @Order(6)
    void testSelfTradeShouldFail() throws Exception {
        // Attempt to trade with self
        String cardIdToTrade = "dfdd758f-649c-40f9-ba3a-8657f4b3439f";
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tradings/6cd85277-4590-49d4-b0cf-ba0a921faad0"))
                .header("Content-Type", "application/json")
                .header("Authorization", KB_AUTH_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(cardIdToTrade))
                .build();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        // Validate the response status is 4xx (failure)
        int statusCode = response.statusCode();
        assertTrue(statusCode >= 400 && statusCode < 500,
                "Expected HTTP 4xx for self-trade attempt, but got: " + statusCode);
    }

    @Test
    @Order(7)
    void testTradeProcess() throws Exception {
        // Step 1: Altenhof executes the trade
        String cardIdToTrade = "951e886a-0fbf-425d-8df5-af2ee4830d85";
        HttpRequest tradeRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tradings/6cd85277-4590-49d4-b0cf-ba0a921faad0"))
                .header("Content-Type", "application/json")
                .header("Authorization", AH_AUTH_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(cardIdToTrade))
                .build();

        HttpResponse<String> tradeResponse = client.send(tradeRequest, HttpResponse.BodyHandlers.ofString());

        // Assert that the trade was successful (HTTP 200)
        assertEquals(200, tradeResponse.statusCode(),
                "Expected HTTP 201 for successful trade, but got: " + tradeResponse.statusCode());

        // Step 2: Verify trading deals as Kienboec
        HttpRequest getDealsRequestKienboec = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tradings"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getDealsResponseKienboec = client.send(getDealsRequestKienboec, HttpResponse.BodyHandlers.ofString());

        // Assert that Kienboec sees the updated trading deals (HTTP 200)
        assertEquals(200, getDealsResponseKienboec.statusCode(),
                "Expected HTTP 200 for fetching trading deals as Kienboec, but got: " + getDealsResponseKienboec.statusCode());

        // Step 3: Verify trading deals as Altenhof
        HttpRequest getDealsRequestAltenhof = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tradings"))
                .header("Authorization", AH_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getDealsResponseAltenhof = client.send(getDealsRequestAltenhof, HttpResponse.BodyHandlers.ofString());

        // Assert that Altenhof sees the updated trading deals (HTTP 200)
        assertEquals(200, getDealsResponseAltenhof.statusCode(),
                "Expected HTTP 200 for fetching trading deals as Altenhof, but got: " + getDealsResponseAltenhof.statusCode());
    }


}