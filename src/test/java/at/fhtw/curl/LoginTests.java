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
public class LoginTests {

    private static final String BASE_URL = "http://localhost:10001";
    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> loginUser(String username, String password) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/sessions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"Username\":\"" + username + "\", \"Password\":\"" + password + "\"}"))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @Order(1)
    void testLoginUsers() throws Exception {
        HttpResponse<String> response1 = loginUser("kienboec", "daniel");
        assertEquals(200, response1.statusCode(), "Login for 'kienboec' failed");

        HttpResponse<String> response2 = loginUser("altenhof", "markus");
        assertEquals(200, response2.statusCode(), "Login for 'altenhof' failed");

        HttpResponse<String> response3 = loginUser("admin", "istrator");
        assertEquals(200, response3.statusCode(), "Login for 'admin' failed");
    }

    @Test
    @Order(2)
    void testLoginWithIncorrectPassword() throws Exception {
        HttpResponse<String> response = loginUser("kienboec", "different");
        assertEquals(401, response.statusCode(), "Login with incorrect password for 'kienboec' should fail");
    }
}