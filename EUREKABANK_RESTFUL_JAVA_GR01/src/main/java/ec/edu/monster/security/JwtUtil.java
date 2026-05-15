package ec.edu.monster.security;

import ec.edu.monster.exception.AuthException;
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class JwtUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final long TOKEN_TTL_SECONDS = 3600;
    private static final String SECRET = System.getenv().getOrDefault("JWT_SECRET", "change-me");

    private JwtUtil() {
    }

    public static String generateToken(String username, String role) {
        if (isBlank(username) || isBlank(role)) {
            throw new IllegalArgumentException("Username and role are required");
        }

        long now = Instant.now().getEpochSecond();
        long exp = now + TOKEN_TTL_SECONDS;

        JsonObject header = Json.createObjectBuilder()
                .add("alg", "HS256")
                .add("typ", "JWT")
                .build();

        JsonObject payload = Json.createObjectBuilder()
                .add("sub", username)
                .add("role", role)
                .add("iat", now)
                .add("exp", exp)
                .build();

        String headerEncoded = base64UrlEncode(header.toString().getBytes(StandardCharsets.UTF_8));
        String payloadEncoded = base64UrlEncode(payload.toString().getBytes(StandardCharsets.UTF_8));
        String signingInput = headerEncoded + "." + payloadEncoded;
        String signature = base64UrlEncode(hmacSha256(signingInput));

        return signingInput + "." + signature;
    }

    public static JwtPayload validateToken(String token) {
        try {
            String normalized = normalizeToken(token);
            if (isBlank(normalized)) {
                throw new AuthException("Invalid token", 400);
            }

            String[] parts = normalized.split("\\.");
            if (parts.length != 3) {
                throw new AuthException("Invalid token", 400);
            }

            String signingInput = parts[0] + "." + parts[1];
            byte[] expectedSignature = hmacSha256(signingInput);
            byte[] actualSignature = base64UrlDecodeToBytes(parts[2]);

            if (!MessageDigest.isEqual(expectedSignature, actualSignature)) {
                throw new AuthException("Invalid token", 400);
            }

            JsonObject payload = parseJson(base64UrlDecode(parts[1]));
            JsonNumber expNumber = payload.getJsonNumber("exp");
            if (expNumber == null) {
                throw new AuthException("Invalid token", 400);
            }

            long exp = expNumber.longValue();
            long now = Instant.now().getEpochSecond();
            if (now >= exp) {
                throw new AuthException("Token expired", 400);
            }

            String username = payload.getString("sub", null);
            String role = payload.getString("role", null);
            if (username == null || role == null) {
                throw new AuthException("Invalid token", 400);
            }

            return new JwtPayload(username, role, exp);
        } catch (AuthException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AuthException("Invalid token", 400);
        }
    }

    private static byte[] hmacSha256(String signingInput) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign token", ex);
        }
    }

    private static String base64UrlEncode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private static String base64UrlDecode(String value) {
        byte[] decoded = base64UrlDecodeToBytes(value);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    private static byte[] base64UrlDecodeToBytes(String value) {
        return Base64.getUrlDecoder().decode(value);
    }

    private static JsonObject parseJson(String json) {
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            return reader.readObject();
        }
    }

    private static String normalizeToken(String token) {
        if (token == null) {
            return null;
        }
        String trimmed = token.trim();
        if (trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
