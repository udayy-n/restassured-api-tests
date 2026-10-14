package com.uday.api.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class managing Bearer token authentication for protected API test requests.
 */
public class TokenManager {

    private static String cachedToken;

    public static synchronized String getBearerToken() {
        if (cachedToken == null) {
            String rawPayload = "{\"user\":\"qa_automation_engineer\",\"role\":\"ADMIN\",\"scope\":\"api:read api:write\"}";
            cachedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
                    + Base64.getUrlEncoder().withoutPadding().encodeToString(rawPayload.getBytes(StandardCharsets.UTF_8))
                    + ".qaSignatureToken2026";
        }
        return cachedToken;
    }
}
