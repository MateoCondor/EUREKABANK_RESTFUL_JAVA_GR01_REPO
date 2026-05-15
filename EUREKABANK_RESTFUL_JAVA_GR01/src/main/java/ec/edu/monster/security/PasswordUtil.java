package ec.edu.monster.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {

    private static final int SALT_LENGTH = 16;

    private PasswordUtil() {
    }

    public static String hashPassword(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        byte[] hash = sha256(rawPassword, salt);
        return encodeBase64(salt) + ":" + encodeBase64(hash);
    }

    public static boolean verifyPassword(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }
        String[] parts = storedHash.split(":", 2);
        if (parts.length != 2) {
            return false;
        }
        byte[] salt = decodeBase64(parts[0]);
        byte[] expectedHash = decodeBase64(parts[1]);
        byte[] actualHash = sha256(rawPassword, salt);
        return MessageDigest.isEqual(expectedHash, actualHash);
    }

    private static byte[] sha256(String rawPassword, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            digest.update(rawPassword.getBytes(StandardCharsets.UTF_8));
            return digest.digest();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    private static String encodeBase64(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    private static byte[] decodeBase64(String value) {
        return Base64.getDecoder().decode(value);
    }
}
