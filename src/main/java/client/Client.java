package client;

import httpclient.Request;
import httpclient.Scope;
import org.apache.http.HttpResponse;

import java.io.IOException;

import static data.Constants.URL;

public class Client {
    public static HttpResponse executeGet(String resource) throws IOException {
        return Request
                .getRequest(URL + resource)
                .setBearerAuthentication(Authentication.getToken(Scope.READ))
                .executeRequest();
    }

    public static HttpResponse executePost(String resource, String body) throws IOException {
        return Request
                .postRequest(URL + resource)
                .setHeader("Content-Type", "application/json")
                .setBearerAuthentication(Authentication.getToken(Scope.WRITE))
                .setBody(body)
                .executeRequest();
    }
}