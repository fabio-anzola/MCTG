package at.fhtw.mctg.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JWTGeneratorTest {

    @Test
    void testCreateJWT_andValidate() {
        // Arrange
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"user\":\"Alice\",\"role\":\"admin\"}";

        // Act
        String token = JWTGenerator.createJWT(header, payload);

        // Assert
        assertNotNull(token, "Token should not be null");
        // We expect 3 parts: header, payload, signature
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have exactly 3 parts (header, payload, signature)");

        // Validate the newly created token
        boolean isValid = JWTGenerator.validateJWT(token);
        assertTrue(isValid, "Token should be valid right after creation");
    }

    @Test
    void testValidateJWT_tamperedPayload() {
        // Arrange
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"user\":\"Alice\",\"role\":\"admin\"}";
        String token = JWTGenerator.createJWT(header, payload);

        // Act: Tamper with the middle part (payload)
        String[] parts = token.split("\\.");
        // Overwrite the payload with something else
        parts[1] = "eyJ1c2VyIjoiQm9iIiwicm9sZSI6ImVkaXRvciJ9"; // base64Url for {"user":"Bob","role":"editor"}
        String tamperedToken = String.join(".", parts);

        // Assert
        assertFalse(JWTGenerator.validateJWT(tamperedToken), "Token with tampered payload should be invalid");
    }

    @Test
    void testDecodePayload_validToken() {
        // Arrange
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"user\":\"Charlie\",\"role\":\"tester\"}";
        String token = JWTGenerator.createJWT(header, payload);

        // Act
        String decodedPayload = JWTGenerator.decodePayload(token);

        // Assert
        assertNotNull(decodedPayload);
        assertTrue(decodedPayload.contains("\"user\":\"Charlie\""));
        assertTrue(decodedPayload.contains("\"role\":\"tester\""));
    }

    @Test
    void testDecodePayload_invalidToken() {
        // Arrange
        String invalidToken = "abc.def"; // only 2 parts, missing signature

        // Act
        String result = JWTGenerator.decodePayload(invalidToken);

        // Assert
        assertNull(result, "Decoding invalid token (missing part) should return null");
    }

    @Test
    void testValidateJWT_invalidSignature() {
        // Arrange
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"user\":\"Alice\"}";
        String token = JWTGenerator.createJWT(header, payload);

        // Tamper with the signature only
        String[] parts = token.split("\\.");
        parts[2] = "invalidsignature";
        String invalidToken = String.join(".", parts);

        // Act & Assert
        assertFalse(JWTGenerator.validateJWT(invalidToken), "Token with invalid signature should not validate");
    }
}
