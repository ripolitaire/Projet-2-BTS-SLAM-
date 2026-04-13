package app.service;

public final class AuthSession {
    private static String token;
    private static String role;
    private static String email;
    private static final String BEARER_PREFIX = "Bearer ";

    private AuthSession() {
    }

    public static void setSession(String tokenValue, String roleValue, String emailValue) {
        token = normalizeToken(tokenValue);
        role = roleValue == null ? null : roleValue.trim();
        email = emailValue == null ? null : emailValue.trim();
    }

    public static String getToken() {
        return token;
    }

    public static boolean hasValidToken() {
        return token != null && !token.isBlank();
    }

    public static String getAuthorizationHeaderValue() {
        if (!hasValidToken()) {
            return null;
        }
        return BEARER_PREFIX + token;
    }

    public static String getRole() {
        return role;
    }

    public static String getEmail() {
        return email;
    }

    public static boolean isAdmin() {
        return role != null && role.equalsIgnoreCase("admin");
    }

    public static void clear() {
        token = null;
        role = null;
        email = null;
    }

    private static String normalizeToken(String rawToken) {
        if (rawToken == null) {
            return null;
        }

        String clean = rawToken.trim();
        if (clean.isEmpty()) {
            return null;
        }

        if (clean.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            clean = clean.substring(BEARER_PREFIX.length()).trim();
        }

        return clean.isEmpty() ? null : clean;
    }
}
