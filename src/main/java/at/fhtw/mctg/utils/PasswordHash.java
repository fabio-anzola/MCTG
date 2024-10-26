package at.fhtw.mctg.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordHash {
    // Using PBKDF2

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256; // 256-bit hash
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private static String getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 128-bit salt
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private static String hashPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    public static String generateHashedPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String salt = getSalt();
        String hashedPassword = hashPassword(password, salt);
        return salt + ":" + hashedPassword; // Store salt with the hashed password
    }

    public static boolean verifyPassword(String password, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        String salt = parts[0];
        String hash = parts[1];
        String newHash = hashPassword(password, salt);
        return newHash.equals(hash);
    }
}
