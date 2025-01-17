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
public class PackageTests {

    private static final String BASE_URL = "http://localhost:10001";
    private static final String AD_AUTH_TOKEN = "Bearer admin-mtcgToken";
    private static final String KB_AUTH_TOKEN = "Bearer kienboec-mtcgToken";
    private static final String AH_AUTH_TOKEN = "Bearer altenhof-mtcgToken";
    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> createPackage(String packageJson, String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/packages"))
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .POST(HttpRequest.BodyPublishers.ofString(packageJson))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> acquirePackage(String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/transactions/packages"))
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    @Test
    @Order(1)
    void testCreatePackages() throws Exception {
        String package1 = "[{\"Id\":\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0}, " +
                "{\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"Name\":\"Dragon\", \"Damage\": 50.0}, " +
                "{\"Id\":\"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"Name\":\"WaterSpell\", \"Damage\": 20.0}, " +
                "{\"Id\":\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\", \"Name\":\"Ork\", \"Damage\": 45.0}, " +
                "{\"Id\":\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\", \"Name\":\"FireSpell\", \"Damage\": 25.0}]";

        HttpResponse<String> response1 = createPackage(package1, AD_AUTH_TOKEN);
        assertEquals(201, response1.statusCode(), "Package 1 creation failed");

        String package2 = "[{\"Id\":\"644808c2-f87a-4600-b313-122b02322fd5\", \"Name\":\"WaterGoblin\", \"Damage\": 9.0}, " +
                "{\"Id\":\"4a2757d6-b1c3-47ac-b9a3-91deab093531\", \"Name\":\"Dragon\", \"Damage\": 55.0}, " +
                "{\"Id\":\"91a6471b-1426-43f6-ad65-6fc473e16f9f\", \"Name\":\"WaterSpell\", \"Damage\": 21.0}, " +
                "{\"Id\":\"4ec8b269-0dfa-4f97-809a-2c63fe2a0025\", \"Name\":\"Ork\", \"Damage\": 55.0}, " +
                "{\"Id\":\"f8043c23-1534-4487-b66b-238e0c3c39b5\", \"Name\":\"WaterSpell\", \"Damage\": 23.0}]";

        HttpResponse<String> response2 = createPackage(package2, AD_AUTH_TOKEN);
        assertEquals(201, response2.statusCode(), "Package 2 creation failed");

        String package3 = "[{\"Id\":\"b017ee50-1c14-44e2-bfd6-2c0c5653a37c\", \"Name\":\"WaterGoblin\", \"Damage\": 11.0}, " +
                "{\"Id\":\"d04b736a-e874-4137-b191-638e0ff3b4e7\", \"Name\":\"Dragon\", \"Damage\": 70.0}, " +
                "{\"Id\":\"88221cfe-1f84-41b9-8152-8e36c6a354de\", \"Name\":\"WaterSpell\", \"Damage\": 22.0}, " +
                "{\"Id\":\"1d3f175b-c067-4359-989d-96562bfa382c\", \"Name\":\"Ork\", \"Damage\": 40.0}, " +
                "{\"Id\":\"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\", \"Name\":\"RegularSpell\", \"Damage\": 28.0}]";

        HttpResponse<String> response3 = createPackage(package3, AD_AUTH_TOKEN);
        assertEquals(201, response3.statusCode(), "Package 3 creation failed");

        String package4 = "[{\"Id\":\"ed1dc1bc-f0aa-4a0c-8d43-1402189b33c8\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0}, " +
                "{\"Id\":\"65ff5f23-1e70-4b79-b3bd-f6eb679dd3b5\", \"Name\":\"Dragon\", \"Damage\": 50.0}, " +
                "{\"Id\":\"55ef46c4-016c-4168-bc43-6b9b1e86414f\", \"Name\":\"WaterSpell\", \"Damage\": 20.0}, " +
                "{\"Id\":\"f3fad0f2-a1af-45df-b80d-2e48825773d9\", \"Name\":\"Ork\", \"Damage\": 45.0}, " +
                "{\"Id\":\"8c20639d-6400-4534-bd0f-ae563f11f57a\", \"Name\":\"WaterSpell\", \"Damage\": 25.0}]";

        HttpResponse<String> response4 = createPackage(package4, AD_AUTH_TOKEN);
        assertEquals(201, response4.statusCode(), "Package 4 creation failed");

        String package5 = "[{\"Id\":\"d7d0cb94-2cbf-4f97-8ccf-9933dc5354b8\", \"Name\":\"WaterGoblin\", \"Damage\": 9.0}, " +
                "{\"Id\":\"44c82fbc-ef6d-44ab-8c7a-9fb19a0e7c6e\", \"Name\":\"Dragon\", \"Damage\": 55.0}, " +
                "{\"Id\":\"2c98cd06-518b-464c-b911-8d787216cddd\", \"Name\":\"WaterSpell\", \"Damage\": 21.0}, " +
                "{\"Id\":\"951e886a-0fbf-425d-8df5-af2ee4830d85\", \"Name\":\"Ork\", \"Damage\": 55.0}, " +
                "{\"Id\":\"dcd93250-25a7-4dca-85da-cad2789f7198\", \"Name\":\"FireSpell\", \"Damage\": 23.0}]";

        HttpResponse<String> response5 = createPackage(package5, AD_AUTH_TOKEN);
        assertEquals(201, response5.statusCode(), "Package 5 creation failed");

        String package6 = "[{\"Id\":\"b2237eca-0271-43bd-87f6-b22f70d42ca4\", \"Name\":\"WaterGoblin\", \"Damage\": 11.0}, " +
                "{\"Id\":\"9e8238a4-8a7a-487f-9f7d-a8c97899eb48\", \"Name\":\"Dragon\", \"Damage\": 70.0}, " +
                "{\"Id\":\"d60e23cf-2238-4d49-844f-c7589ee5342e\", \"Name\":\"WaterSpell\", \"Damage\": 22.0}, " +
                "{\"Id\":\"fc305a7a-36f7-4d30-ad27-462ca0445649\", \"Name\":\"Ork\", \"Damage\": 40.0}, " +
                "{\"Id\":\"84d276ee-21ec-4171-a509-c1b88162831c\", \"Name\":\"RegularSpell\", \"Damage\": 28.0}]";

        HttpResponse<String> response6 = createPackage(package6, AD_AUTH_TOKEN);
        assertEquals(201, response6.statusCode(), "Package 6 creation failed");
    }

    @Test
    @Order(2)
    void testAcquirePackages() throws Exception {
        // First package acquisition
        HttpResponse<String> response1 = acquirePackage(KB_AUTH_TOKEN);
        assertEquals(201, response1.statusCode(), "First package acquisition failed");

        // Second package acquisition
        HttpResponse<String> response2 = acquirePackage(KB_AUTH_TOKEN);
        assertEquals(201, response2.statusCode(), "Second package acquisition failed");

        // Third package acquisition
        HttpResponse<String> response3 = acquirePackage(KB_AUTH_TOKEN);
        assertEquals(201, response3.statusCode(), "Third package acquisition failed");

        // Fourth package acquisition
        HttpResponse<String> response4 = acquirePackage(KB_AUTH_TOKEN);
        assertEquals(201, response4.statusCode(), "Fourth package acquisition failed");

        // Attempting to acquire more packages (should fail)
        HttpResponse<String> response5 = acquirePackage(KB_AUTH_TOKEN);
        assertTrue(response5.statusCode() >= 400 && response5.statusCode() < 500,
                "Acquisition beyond budget should fail");
    }

    @Test
    @Order(3)
    void testAcquirePackagesForAltenhof() throws Exception {
        // First package acquisition
        HttpResponse<String> response1 = acquirePackage(AH_AUTH_TOKEN);
        assertEquals(201, response1.statusCode(), "First package acquisition for altenhof failed");

        // Second package acquisition
        HttpResponse<String> response2 = acquirePackage(AH_AUTH_TOKEN);
        assertEquals(201, response2.statusCode(), "Second package acquisition for altenhof failed");

        // Attempting to acquire more packages (should fail)
        HttpResponse<String> response3 = acquirePackage(AH_AUTH_TOKEN);
        assertTrue(response3.statusCode() >= 400 && response3.statusCode() < 500,
                "Acquisition without available packages should fail");
    }

    @Test
    @Order(4)
    void testAddNewPackages() throws Exception {
        // First package addition
        String package1 = "[{\"Id\":\"67f9048f-99b8-4ae4-b866-d8008d00c53d\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0}," +
                "{\"Id\":\"aa9999a0-734c-49c6-8f4a-651864b14e62\", \"Name\":\"RegularSpell\", \"Damage\": 50.0}," +
                "{\"Id\":\"d6e9c720-9b5a-40c7-a6b2-bc34752e3463\", \"Name\":\"Knight\", \"Damage\": 20.0}," +
                "{\"Id\":\"02a9c76e-b17d-427f-9240-2dd49b0d3bfd\", \"Name\":\"RegularSpell\", \"Damage\": 45.0}," +
                "{\"Id\":\"2508bf5c-20d7-43b4-8c77-bc677decadef\", \"Name\":\"FireElf\", \"Damage\": 25.0}]";

        HttpResponse<String> response1 = createPackage(package1, AD_AUTH_TOKEN);
        assertEquals(201, response1.statusCode(), "Adding first package failed");

        // Second package addition
        String package2 = "[{\"Id\":\"70962948-2bf7-44a9-9ded-8c68eeac7793\", \"Name\":\"WaterGoblin\", \"Damage\": 9.0}," +
                "{\"Id\":\"74635fae-8ad3-4295-9139-320ab89c2844\", \"Name\":\"FireSpell\", \"Damage\": 55.0}," +
                "{\"Id\":\"ce6bcaee-47e1-4011-a49e-5a4d7d4245f3\", \"Name\":\"Knight\", \"Damage\": 21.0}," +
                "{\"Id\":\"a6fde738-c65a-4b10-b400-6fef0fdb28ba\", \"Name\":\"FireSpell\", \"Damage\": 55.0}," +
                "{\"Id\":\"a1618f1e-4f4c-4e09-9647-87e16f1edd2d\", \"Name\":\"FireElf\", \"Damage\": 23.0}]";

        HttpResponse<String> response2 = createPackage(package2, AD_AUTH_TOKEN);
        assertEquals(201, response2.statusCode(), "Adding second package failed");

        // Third package addition
        String package3 = "[{\"Id\":\"2272ba48-6662-404d-a9a1-41a9bed316d9\", \"Name\":\"WaterGoblin\", \"Damage\": 11.0}," +
                "{\"Id\":\"3871d45b-b630-4a0d-8bc6-a5fc56b6a043\", \"Name\":\"Dragon\", \"Damage\": 70.0}," +
                "{\"Id\":\"166c1fd5-4dcb-41a8-91cb-f45dcd57cef3\", \"Name\":\"Knight\", \"Damage\": 22.0}," +
                "{\"Id\":\"237dbaef-49e3-4c23-b64b-abf5c087b276\", \"Name\":\"WaterSpell\", \"Damage\": 40.0}," +
                "{\"Id\":\"27051a20-8580-43ff-a473-e986b52f297a\", \"Name\":\"FireElf\", \"Damage\": 28.0}]";

        HttpResponse<String> response3 = createPackage(package3, AD_AUTH_TOKEN);
        assertEquals(201, response3.statusCode(), "Adding third package failed");
    }

    @Test
    @Order(5)
    void testAcquireNewlyCreatedPackages() throws Exception {
        // First package acquisition
        HttpResponse<String> response1 = acquirePackage(AH_AUTH_TOKEN);
        assertEquals(201, response1.statusCode(), "Acquiring first package failed");

        // Second package acquisition
        HttpResponse<String> response2 = acquirePackage(AH_AUTH_TOKEN);
        assertEquals(201, response2.statusCode(), "Acquiring second package failed");

        // Attempt to acquire a package when out of money
        HttpResponse<String> response3 = acquirePackage(AH_AUTH_TOKEN);
        assertEquals(403, response3.statusCode(), "Expected failure due to insufficient funds");
    }
}