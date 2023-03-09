package client;

import httpclient.Request;
import httpclient.Scope;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

import static data.Constants.*;

public class Authentication {
    public static HttpResponse getTokenRequest(Scope scope) throws URISyntaxException, IOException {
        return Request
                .postRequest(URL + TOKEN_RESOURCE)
                .setHeader("Content-Type", "application/json")
                .setParameter("grant_type", "client_credentials")
                .setParameter("scope", scope.name().toLowerCase())
                .setBasicAuthentication(USERNAME, PASSWORD)
                .executeRequest();
    }
}