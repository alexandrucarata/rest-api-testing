package httpclient;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import static httpclient.RequestType.*;

public class Request {
    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();
    private final HttpRequest request;

    private Request(String uri, RequestType requestType) {
        switch (requestType) {
            case GET -> request = new HttpGet(uri);
            case POST -> request = new HttpPost(uri);
            case PUT -> request = new HttpPut(uri);
            case PATCH -> request = new HttpPatch(uri);
            case DELETE -> request = new HttpDelete(uri);
            default -> throw new RuntimeException();
        }
    }

    public static Request getRequest(String uri) {
        return new Request(uri, GET);
    }

    public static Request postRequest(String uri) {
        return new Request(uri, POST);
    }

    public static Request putRequest(String uri) {
        return new Request(uri, PUT);
    }

    public static Request patchRequest(String uri) {
        return new Request(uri, PATCH);
    }

    public static Request deleteRequest(String uri) {
        return new Request(uri, DELETE);
    }

    public Request setBasicAuthentication(String username, String password) {
        String basicAuth = Base64.encodeBase64String((username + ":" + password).getBytes());
        setHeader(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth);
        return this;
    }

    public Request setBearerAuthentication(String token) {
        setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return this;
    }

    public Request setHeader(String key, String value) {
        request.addHeader(key, value);
        return this;
    }

    public Request setBody(String body) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(body);
        ((HttpPost) request).setEntity(entity);
        return this;
    }

    public Request setParameter(String key, String value) throws URISyntaxException {
        URI uri = new URIBuilder(request.getRequestLine().getUri()).addParameter(key, value).build();
        ((HttpRequestBase) request).setURI(uri);
        return this;
    }

    public HttpResponse executeRequest() throws IOException {
        return HTTP_CLIENT.execute((HttpUriRequest) request);
    }
}