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
public class RegistrationTests {

    private static final String BASE_URL = "http://localhost:10001";
    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> createUser(String username, String password) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"Username\":\"" + username + "\", \"Password\":\"" + password + "\"}"))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @Order(1)
    void testCreateUsers() throws Exception {
        HttpResponse<String> response1 = createUser("kienboec", "daniel");
        assertEquals(201, response1.statusCode(), "User 'kienboec' creation failed");

        HttpResponse<String> response2 = createUser("altenhof", "markus");
        assertEquals(201, response2.statusCode(), "User 'altenhof' creation failed");

        HttpResponse<String> response3 = createUser("admin", "istrator");
        assertEquals(201, response3.statusCode(), "User 'admin' creation failed");
    }

    @Test
    @Order(2)
    void testCreateDuplicateUsers() throws Exception {
        HttpResponse<String> duplicateResponse1 = createUser("kienboec", "daniel");
        assertEquals(409, duplicateResponse1.statusCode(), "Duplicate user 'kienboec' should fail");

        HttpResponse<String> duplicateResponse2 = createUser("kienboec", "different");
        assertEquals(409, duplicateResponse2.statusCode(), "Duplicate user 'kienboec' with different password should fail");
    }
}