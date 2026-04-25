package com.baeldung.lsso;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * Needs the following to be running: 
 * - Authorization Server
 */
public class AuthorizationServerLiveTest {

    private static final Pattern HTML_FORM_ACTION_PATTERN = Pattern.compile("action=\\\"([^\\\"]+)\\\"");

    private static final String USERNAME = "john@test.com";
    private static final String PASSWORD = "123";

    private static final String CLIENT_ID = "lssoClient";
    private static final String CLIENT_SECRET = "lssoSecret";

    private static final String LEGACY_AUTH_SERVER_BASE_URL = "http://localhost:8083/auth/realms/baeldung";
    private static final String MODERN_AUTH_SERVER_BASE_URL = "http://localhost:8083/realms/baeldung";
    private static final String CLIENT_BASE_URL = "http://localhost:8082";

    private static final String REDIRECT_URL = CLIENT_BASE_URL + "/lsso-client/login/oauth2/code/custom";

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
        .followRedirects(HttpClient.Redirect.NEVER)
        .build();

    @Test
    public void givenAuthorizationCodeGrant_whenObtainAccessToken_thenSuccess() {
        String accessToken = obtainAccessToken();

        assertThat(accessToken).isNotBlank();
    }

    @Test
    public void whenServiceStartsAndLoadsRealmConfigurations_thenOidcDiscoveryEndpointIsAvailable() {
        final String authServerBaseUrl = resolveAuthServerBaseUrl();
        final String oidcDiscoveryUrl = authServerBaseUrl + "/.well-known/openid_configuration";

        HttpResponse<String> response = sendGet(oidcDiscoveryUrl);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body()).contains("\"issuer\"", "\"authorization_endpoint\"", "\"token_endpoint\"", "\"userinfo_endpoint\"");
    }

    private String obtainAccessToken() {
        final String authServerBaseUrl = resolveAuthServerBaseUrl();
        final String authorizeUrl = authServerBaseUrl + "/protocol/openid-connect/auth?response_type=code&client_id=" + CLIENT_ID + "&scope=read&redirect_uri=" + REDIRECT_URL;
        final String tokenUrl = authServerBaseUrl + "/protocol/openid-connect/token";

        // obtain authentication url with custom codes
        HttpResponse<String> response = sendGet(authorizeUrl);
        assertThat(response.statusCode())
            .as("Expected auth login page to be served by '%s', body: %s", authorizeUrl, response.body())
            .isEqualTo(HttpStatus.OK.value());

        String kcPostAuthenticationUrl = extractFormAction(response.body());

        // obtain authentication code and state
        response = sendPostForm(kcPostAuthenticationUrl, Map.of("username", USERNAME, "password", PASSWORD, "credentialId", ""));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FOUND.value());

        // extract authorization code
        String location = response.headers().firstValue(HttpHeaders.LOCATION).orElseThrow();
        String code = location.split("code=")[1].split("&")[0];

        // get access token
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", CLIENT_ID);
        params.put("redirect_uri", REDIRECT_URL);
        params.put("client_secret", CLIENT_SECRET);
        response = sendPostForm(tokenUrl, params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return extractJsonStringValue(response.body(), "access_token");
    }

    private static String resolveAuthServerBaseUrl() {
        HttpResponse<String> modernResponse = sendGet(MODERN_AUTH_SERVER_BASE_URL + "/.well-known/openid_configuration");
        if (modernResponse.statusCode() == HttpStatus.OK.value()) {
            return MODERN_AUTH_SERVER_BASE_URL;
        }

        HttpResponse<String> legacyResponse = sendGet(LEGACY_AUTH_SERVER_BASE_URL + "/.well-known/openid_configuration");
        assertThat(legacyResponse.statusCode())
            .as("OIDC discovery endpoint is unavailable for both '%s' and '%s'", MODERN_AUTH_SERVER_BASE_URL, LEGACY_AUTH_SERVER_BASE_URL)
            .isEqualTo(HttpStatus.OK.value());
        return LEGACY_AUTH_SERVER_BASE_URL;
    }

    private static String extractFormAction(String html) {
        Matcher matcher = HTML_FORM_ACTION_PATTERN.matcher(html);
        assertThat(matcher.find())
            .as("Unable to find login form action in response body: %s", html)
            .isTrue();
        return matcher.group(1).replace("&amp;", "&");
    }

    private static HttpResponse<String> sendGet(String url) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
            .GET()
            .build();
        try {
            return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpResponse<String> sendPostForm(String url, Map<String, String> formParams) {
        String requestBody = formParams.entrySet()
            .stream()
            .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
            .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        try {
            return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String extractJsonStringValue(String json, String key) {
        String token = "\"" + key + "\":";
        int start = json.indexOf(token);
        assertThat(start).isGreaterThanOrEqualTo(0);
        int valueStart = json.indexOf('"', start + token.length()) + 1;
        int valueEnd = json.indexOf('"', valueStart);
        assertThat(valueEnd).isGreaterThan(valueStart);
        return json.substring(valueStart, valueEnd);
    }

}
