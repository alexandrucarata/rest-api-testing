package client;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static data.Constants.ZIPCODES_EXPAND_RESOURCE;
import static data.Constants.ZIPCODES_RESOURCE;
import static io.restassured.RestAssured.given;

public class ZipCodeClient {
    @Step("Get Zip Codes List")
    public Response getZipCodesList() {
        return given()
                .header("Authorization", "Bearer " + Authentication.getToken(Scope.READ))
                .when()
                .get(ZIPCODES_RESOURCE);
    }

    @Step("Add Zip Codes to Zip Codes List")
    public Response addToZipCodesList(List<String> zipCodesList) {
        return given()
                .header("Authorization", "Bearer " + Authentication.getToken(Scope.WRITE))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(zipCodesList)
                .when()
                .post(ZIPCODES_EXPAND_RESOURCE);
    }

    @Step("Get Newly Created Zip Code")
    public String getNewZipCode() {
        String newZipCode = new Faker().number().digits(5);
        addToZipCodesList(Collections.singletonList(newZipCode));
        return newZipCode;
    }

    public List<String> getResponseAsList(Response response) {
        return Arrays.stream(response.getBody().as(String[].class)).collect(Collectors.toList());
    }
}