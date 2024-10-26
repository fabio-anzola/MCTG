package at.fhtw.mctg.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Basic implementation for JWT Tokens
 */
public class JWTGenerator {

    private static final String SECRET_KEY = "s3cretpassword";

    public static String createJWT(String header, String payload) {
        // Base64 Encode Header and Payload
        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        // Cat header and payload
        String unsignedToken = encodedHeader + "." + encodedPayload;

        // create and add signature
        String signature = createHMACSHA256Signature(unsignedToken);
        return unsignedToken + "." + signature;
    }

    private static String createHMACSHA256Signature(String data) {
        // Access Sha256 hmac algo
        Mac sha256HMAC = null;
        try {
            sha256HMAC = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not access Sha256 hash algo", e);
        }

        //Initialize hmac algo with secret key
        SecretKeySpec secretKeySpec = new SecretKeySpec(JWTGenerator.SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        try {
            sha256HMAC.init(secretKeySpec);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Could not initialize Sha256 hash algo", e);
        }

        // Sign data with hash algo
        byte[] signedBytes = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signedBytes);
    }

    public static boolean validateJWT(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) return false;

        String header = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        String signature = parts[2];

        // Token ohne Signatur
        String unsignedToken = parts[0] + "." + parts[1];

        // Erzeuge eine neue Signatur
        String expectedSignature = createHMACSHA256Signature(unsignedToken);

        // Überprüfe die Signatur
        return expectedSignature.equals(signature);
    }

    public static String decodePayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;

            // Decodiere Payload
            return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}