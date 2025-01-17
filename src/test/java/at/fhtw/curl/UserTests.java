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
public class UserTests {

    private static final String BASE_URL = "http://localhost:10001";
    private static final String AD_AUTH_TOKEN = "Bearer admin-mtcgToken";
    private static final String KB_AUTH_TOKEN = "Bearer kienboec-mtcgToken";
    private static final String AH_AUTH_TOKEN = "Bearer altenhof-mtcgToken";
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    @Order(1)
    void testEditUserData() throws Exception {
        // GET current user data for kienboec
        HttpRequest getRequestKienboec = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/kienboec"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getResponseKienboec = client.send(getRequestKienboec, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponseKienboec.statusCode(), "Expected HTTP 200 for fetching kienboec's current user data");

        // GET current user data for altenhof
        HttpRequest getRequestAltenhof = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/altenhof"))
                .header("Authorization", AH_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getResponseAltenhof = client.send(getRequestAltenhof, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponseAltenhof.statusCode(), "Expected HTTP 200 for fetching altenhof's current user data");

        // PUT updated user data for kienboec
        String updatedDataKienboec = "{\"Name\": \"Kienboeck\", \"Bio\": \"me playin...\", \"Image\": \":-)\"}";
        HttpRequest putRequestKienboec = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/kienboec"))
                .header("Content-Type", "application/json")
                .header("Authorization", KB_AUTH_TOKEN)
                .PUT(HttpRequest.BodyPublishers.ofString(updatedDataKienboec))
                .build();

        HttpResponse<String> putResponseKienboec = client.send(putRequestKienboec, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, putResponseKienboec.statusCode(), "Expected HTTP 200 for updating kienboec's user data");

        // PUT updated user data for altenhof
        String updatedDataAltenhof = "{\"Name\": \"Altenhofer\", \"Bio\": \"me codin...\", \"Image\": \":-D\"}";
        HttpRequest putRequestAltenhof = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/altenhof"))
                .header("Content-Type", "application/json")
                .header("Authorization", AH_AUTH_TOKEN)
                .PUT(HttpRequest.BodyPublishers.ofString(updatedDataAltenhof))
                .build();

        HttpResponse<String> putResponseAltenhof = client.send(putRequestAltenhof, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, putResponseAltenhof.statusCode(), "Expected HTTP 200 for updating altenhof's user data");

        // GET updated user data for kienboec
        HttpRequest getUpdatedRequestKienboec = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/kienboec"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getUpdatedResponseKienboec = client.send(getUpdatedRequestKienboec, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getUpdatedResponseKienboec.statusCode(), "Expected HTTP 200 for fetching kienboec's updated user data");

        // GET updated user data for altenhof
        HttpRequest getUpdatedRequestAltenhof = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/altenhof"))
                .header("Authorization", AH_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getUpdatedResponseAltenhof = client.send(getUpdatedRequestAltenhof, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getUpdatedResponseAltenhof.statusCode(), "Expected HTTP 200 for fetching altenhof's updated user data");
    }

    @Test
    @Order(2)
    void testUnauthorizedAccess() throws Exception {
        // Test 1: GET user "altenhof" with "kienboec" token (should fail)
        HttpRequest getAltenhofRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/altenhof"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getAltenhofResponse = client.send(getAltenhofRequest, HttpResponse.BodyHandlers.ofString());
        assertTrue(getAltenhofResponse.statusCode() >= 400 && getAltenhofResponse.statusCode() < 500,
                "Expected HTTP 4xx for unauthorized access to 'altenhof' with 'kienboec' token");

        // Test 2: GET user "kienboec" with "altenhof" token (should fail)
        HttpRequest getKienboecRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/kienboec"))
                .header("Authorization", AH_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getKienboecResponse = client.send(getKienboecRequest, HttpResponse.BodyHandlers.ofString());
        assertTrue(getKienboecResponse.statusCode() >= 400 && getKienboecResponse.statusCode() < 500,
                "Expected HTTP 4xx for unauthorized access to 'kienboec' with 'altenhof' token");

        // Test 3: PUT user "kienboec" data with "altenhof" token (should fail)
        String updateData = "{\"Name\": \"Hoax\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}";
        HttpRequest putKienboecRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/kienboec"))
                .header("Content-Type", "application/json")
                .header("Authorization", AH_AUTH_TOKEN)
                .PUT(HttpRequest.BodyPublishers.ofString(updateData))
                .build();

        HttpResponse<String> putKienboecResponse = client.send(putKienboecRequest, HttpResponse.BodyHandlers.ofString());
        assertTrue(putKienboecResponse.statusCode() >= 400 && putKienboecResponse.statusCode() < 500,
                "Expected HTTP 4xx for unauthorized update of 'kienboec' with 'altenhof' token");

        // Test 4: PUT user "altenhof" data with "kienboec" token (should fail)
        String updateDataAlt = "{\"Name\": \"Hoax\", \"Bio\": \"me codin...\",  \"Image\": \":-D\"}";
        HttpRequest putAltenhofRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/altenhof"))
                .header("Content-Type", "application/json")
                .header("Authorization", KB_AUTH_TOKEN)
                .PUT(HttpRequest.BodyPublishers.ofString(updateDataAlt))
                .build();

        HttpResponse<String> putAltenhofResponse = client.send(putAltenhofRequest, HttpResponse.BodyHandlers.ofString());
        assertTrue(putAltenhofResponse.statusCode() >= 400 && putAltenhofResponse.statusCode() < 500,
                "Expected HTTP 4xx for unauthorized update of 'altenhof' with 'kienboec' token");

        // Test 5: GET non-existent user "someGuy" with "kienboec" token (should fail)
        HttpRequest getSomeGuyRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/someGuy"))
                .header("Authorization", KB_AUTH_TOKEN)
                .GET()
                .build();

        HttpResponse<String> getSomeGuyResponse = client.send(getSomeGuyRequest, HttpResponse.BodyHandlers.ofString());
        assertTrue(getSomeGuyResponse.statusCode() >= 400 && getSomeGuyResponse.statusCode() < 500,
                "Expected HTTP 4xx for fetching non-existent user 'someGuy'");
    }
}