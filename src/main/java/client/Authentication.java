package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.Auth;
import httpclient.Request;
import httpclient.Scope;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

import static data.Constants.*;

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
        ObjectMapper mapper = new ObjectMapper();
        HttpResponse response = getTokenRequest(scope);
        return mapper.readValue(response.getEntity().getContent(), Auth.class).getAccessToken();
    }

    private static HttpResponse getTokenRequest(Scope scope) throws URISyntaxException, IOException {
        return Request
                .postRequest(URL + TOKEN_RESOURCE)
                .setHeader("Content-Type", "application/json")
                .setParameter("grant_type", "client_credentials")
                .setParameter("scope", scope.name().toLowerCase())
                .setBasicAuthentication(USERNAME, PASSWORD)
                .executeRequest();
    }
}