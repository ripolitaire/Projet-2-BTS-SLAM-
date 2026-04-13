package app.service;

public final class ApiConfig {
    private static final String DEFAULT_BASE_URL = "http://localhost:3000";

    private ApiConfig() {
    }

    public static String getBaseUrl() {
        String env = System.getenv("API_BASE_URL");
        if (env == null || env.isBlank()) {
            return DEFAULT_BASE_URL;
        }
        return trimTrailingSlash(env.trim());
    }

    public static String apiPath(String path) {
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return getBaseUrl() + normalizedPath;
    }

    private static String trimTrailingSlash(String url) {
        int end = url.length();
        while (end > 0 && url.charAt(end - 1) == '/') {
            end--;
        }
        return url.substring(0, end);
    }
}
