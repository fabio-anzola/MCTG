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
public class DeckTests {

    private static final String BASE_URL = "http://localhost:10001";
    private static final String AD_AUTH_TOKEN = "Bearer admin-mtcgToken";
    private static final String KB_AUTH_TOKEN = "Bearer kienboec-mtcgToken";
    private static final String AH_AUTH_TOKEN = "Bearer altenhof-mtcgToken";
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    @Order(1)
    void testShowAllAcquiredCardsWithAuthorizationKienboec() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/cards"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Expected HTTP 200 for authorized access");
    }

    @Test
    @Order(2)
    void testShowAllAcquiredCardsWithoutAuthorization() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/cards"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(401, response.statusCode(), "Expected HTTP 401 for unauthorized access");
    }

    @Test
    @Order(3)
    void testShowAllAcquiredCardsWithAuthorizationAltenhof() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/cards"))
                .header("Authorization", AH_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Expected HTTP 200 for authorized access with altenhof token");
    }

    @Test
    @Order(4)
    void testShowUnconfiguredDeckForKienboec() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Expected HTTP 200 for unconfigured deck with kienboec token");
        assertEquals("[]", response.body(), "Expected an empty list for an unconfigured deck");
    }

    @Test
    @Order(5)
    void testShowUnconfiguredDeckForAltenhof() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck"))
                .header("Authorization", AH_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Expected HTTP 200 for unconfigured deck with altenhof token");
        assertEquals("[]", response.body(), "Expected an empty list for an unconfigured deck");
    }

    @Test
    @Order(6)
    void testConfigureDeckForKienboec() throws Exception {
        // Configure deck
        String deckPayloadKienboec = "[\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\"]";
        HttpRequest configureDeckRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck"))
                .header("Content-Type", "application/json")
                .header("Authorization", KB_AUTH_TOKEN)
                .PUT(HttpRequest.BodyPublishers.ofString(deckPayloadKienboec))
                .build();

        HttpResponse<String> configureResponse = client.send(configureDeckRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, configureResponse.statusCode(), "Expected HTTP 2xx after configuring the deck");

        // Verify deck configuration
        HttpRequest getDeckRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getDeckRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Expected HTTP 200 for retrieving the deck");
        assertTrue(getResponse.body().contains("845f0dc7-37d0-426e-994e-43fc3ac83c08"), "Expected deck configuration to match payload");
        assertTrue(getResponse.body().contains("99f8f8dc-e25e-4a95-aa2c-782823f36e2a"), "Expected deck configuration to match payload");
        assertTrue(getResponse.body().contains("e85e3976-7c86-4d06-9a80-641c2019a79f"), "Expected deck configuration to match payload");
        assertTrue(getResponse.body().contains("171f6076-4eb5-4a7d-b3f2-2d650cc3d237"), "Expected deck configuration to match payload");
    }

    @Test
    @Order(7)
    void testConfigureDeckForAltenhof() throws Exception {
        // Configure deck
        String deckPayloadAltenhof = "[\"aa9999a0-734c-49c6-8f4a-651864b14e62\", \"d6e9c720-9b5a-40c7-a6b2-bc34752e3463\", \"d60e23cf-2238-4d49-844f-c7589ee5342e\", \"02a9c76e-b17d-427f-9240-2dd49b0d3bfd\"]";
        HttpRequest configureDeckRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck"))
                .header("Content-Type", "application/json")
                .header("Authorization", AH_AUTH_TOKEN)
                .PUT(HttpRequest.BodyPublishers.ofString(deckPayloadAltenhof))
                .build();

        HttpResponse<String> configureResponse = client.send(configureDeckRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, configureResponse.statusCode(), "Expected HTTP 2xx after configuring the deck");

        // Verify deck configuration
        HttpRequest getDeckRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck"))
                .header("Authorization", AH_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getDeckRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Expected HTTP 200 for retrieving the deck");
        assertTrue(getResponse.body().contains("aa9999a0-734c-49c6-8f4a-651864b14e62"), "Expected deck configuration to match payload");
        assertTrue(getResponse.body().contains("d6e9c720-9b5a-40c7-a6b2-bc34752e3463"), "Expected deck configuration to match payload");
        assertTrue(getResponse.body().contains("d60e23cf-2238-4d49-844f-c7589ee5342e"), "Expected deck configuration to match payload");
        assertTrue(getResponse.body().contains("02a9c76e-b17d-427f-9240-2dd49b0d3bfd"), "Expected deck configuration to match payload");
    }

    @Test
    @Order(8)
    void testConfigureInvalidDeckForAltenhof() throws Exception {
        // Attempt to configure an invalid deck with cards belonging to another user
        String invalidDeckPayload = "[\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\"]";
        HttpRequest configureInvalidDeckRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck"))
                .header("Content-Type", "application/json")
                .header("Authorization", AH_AUTH_TOKEN)
                .PUT(HttpRequest.BodyPublishers.ofString(invalidDeckPayload))
                .build();

        HttpResponse<String> invalidResponse = client.send(configureInvalidDeckRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, invalidResponse.statusCode(), "Expected HTTP 4xx for invalid deck configuration");

        // Verify that the original deck remains unchanged
        HttpRequest getDeckRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck"))
                .header("Authorization", AH_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getDeckRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Expected HTTP 200 for retrieving the deck");
    }

    @Test
    @Order(9)
    void testConfigureDeckWithTooFewCards() throws Exception {
        // Attempt to configure a deck with only 3 cards
        String tooFewCardsPayload = "[\"aa9999a0-734c-49c6-8f4a-651864b14e62\", \"d6e9c720-9b5a-40c7-a6b2-bc34752e3463\", \"d60e23cf-2238-4d49-844f-c7589ee5342e\"]";
        HttpRequest configureTooFewCardsRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck"))
                .header("Content-Type", "application/json")
                .header("Authorization", AH_AUTH_TOKEN)
                .PUT(HttpRequest.BodyPublishers.ofString(tooFewCardsPayload))
                .build();

        HttpResponse<String> tooFewCardsResponse = client.send(configureTooFewCardsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, tooFewCardsResponse.statusCode(), "Expected HTTP 4xx for configuring a deck with too few cards");
    }

    @Test
    @Order(10)
    void testShowConfiguredDeckJsonFormat() throws Exception {
        // Get the configured deck for kienboec
        HttpRequest requestKienboec = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> responseKienboec = client.send(requestKienboec, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseKienboec.statusCode(), "Expected HTTP 200 for retrieving deck in JSON format for kienboec");

        // Get the configured deck for altenhof
        HttpRequest requestAltenhof = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck"))
                .header("Authorization", AH_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> responseAltenhof = client.send(requestAltenhof, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAltenhof.statusCode(), "Expected HTTP 200 for retrieving deck in JSON format for altenhof");
    }

    @Test
    @Order(11)
    void testShowConfiguredDeckPlainTextFormat() throws Exception {
        // Get the configured deck in plain text format for kienboec
        HttpRequest requestPlainKienboec = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck?format=plain"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> responsePlainKienboec = client.send(requestPlainKienboec, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responsePlainKienboec.statusCode(), "Expected HTTP 200 for retrieving deck in plain text format for kienboec");

        // Get the configured deck in plain text format for altenhof
        HttpRequest requestPlainAltenhof = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/deck?format=plain"))
                .header("Authorization", AH_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> responsePlainAltenhof = client.send(requestPlainAltenhof, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responsePlainAltenhof.statusCode(), "Expected HTTP 200 for retrieving deck in plain text format for altenhof");
    }


}