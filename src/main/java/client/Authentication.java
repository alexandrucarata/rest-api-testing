package client;

import data.Auth;
import io.restassured.response.Response;

import java.io.IOException;
import java.net.URISyntaxException;

import static data.Constants.*;
import static io.restassured.RestAssured.given;

public class Authentication {
    private static final String WRITE_TOKEN;
    private static final String READ_TOKEN;

    static {
        try {
            WRITE_TOKEN = getBearerToken(Scope.WRITE);
            READ_TOKEN = getBearerToken(Scope.READ);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getToken(Scope scope) {
        if (scope == Scope.WRITE) {
            return WRITE_TOKEN;
        }
        return READ_TOKEN;
    }

    private static String getBearerToken(Scope scope) throws URISyntaxException, IOException {
        return getTokenRequest(scope).as(Auth.class).getAccessToken();
    }

    private static Response getTokenRequest(Scope scope) {
        return given()
                .param("grant_type", "client_credentials")
                .param("scope", scope.name().toLowerCase())
                .auth()
                .basic(USERNAME, PASSWORD)
                .when()
                .post(URL + TOKEN_RESOURCE);
    }
}