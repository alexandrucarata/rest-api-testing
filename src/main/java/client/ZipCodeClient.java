package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import data.Response;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static data.Constants.ZIPCODES_EXPAND_RESOURCE;
import static data.Constants.ZIPCODES_RESOURCE;

public class ZipCodeClient {
    public Response<List<String>> getZipCodesList() throws IOException {
        Response<List<String>> response = new Response<>();
        HttpResponse httpResponse = executeGetZipCodes();
        response.setBody(getResponseAsList(httpResponse));
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return response;
    }

    public Response<List<String>> addToZipCodesList(List<String> zipCodesList) throws IOException {
        Response<List<String>> response = new Response<>();
        HttpResponse httpResponse = executePostZipCodes(zipCodesList.toString());
        response.setBody(getResponseAsList(httpResponse));
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return response;
    }

    public String getNewZipCode() throws IOException {
        String newZipCode = new Faker().number().digits(5);
        addToZipCodesList(Collections.singletonList(newZipCode));
        return newZipCode;
    }

    private static HttpResponse executeGetZipCodes() throws IOException {
        return Client.executeGet(ZIPCODES_RESOURCE);
    }

    private static HttpResponse executePostZipCodes(String requestBody) throws IOException {
        return Client.executePost(ZIPCODES_EXPAND_RESOURCE, requestBody);
    }

    private static List<String> getResponseAsList(HttpResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.stream(mapper.readValue(response.getEntity().getContent(), String[].class))
                .collect(Collectors.toList());
    }
}