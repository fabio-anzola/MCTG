package at.fhtw.mctg.utils;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashTest {

    @Test
    void testGenerateHashedPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "mySecret123";
        String stored = PasswordHash.generateHashedPassword(password);

        // The returned string should have the format "salt:hashed"
        assertNotNull(stored, "Generated password should not be null");
        assertTrue(stored.contains(":"), "Generated password should contain ':' separator");

        String[] parts = stored.split(":");
        assertEquals(2, parts.length, "Should have exactly 2 parts: salt and hash");

        // Just ensure salt and hash are non-empty
        assertFalse(parts[0].isEmpty(), "Salt part should not be empty");
        assertFalse(parts[1].isEmpty(), "Hash part should not be empty");
    }

    @Test
    void testVerifyPassword_CorrectPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String originalPassword = "mySecret123";
        String stored = PasswordHash.generateHashedPassword(originalPassword);

        boolean verified = PasswordHash.verifyPassword(originalPassword, stored);
        assertTrue(verified, "Verification should succeed for the correct password");
    }

    @Test
    void testVerifyPassword_IncorrectPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String originalPassword = "mySecret123";
        String wrongPassword = "myWrongSecret456";

        String stored = PasswordHash.generateHashedPassword(originalPassword);

        boolean verified = PasswordHash.verifyPassword(wrongPassword, stored);
        assertFalse(verified, "Verification should fail for an incorrect password");
    }
}