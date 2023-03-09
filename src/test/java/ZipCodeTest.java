import client.ZipCodeClient;
import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static data.Constants.*;

@Epic("Zip Code Management")
@Story("Adding Zip Codes")
public class ZipCodeTest {
    private static final List<String> AVAILABLE_ZIP_CODES = Arrays.asList("12345", "23456", "ABCDE");
    private static ZipCodeClient client;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = PORT;
        client = new ZipCodeClient();
    }

    // Scenario #1:
    @Test
    @AllureId("API-1")
    @Feature("Ability to Get Zip Codes List")
    @Description("Check if zip codes list is successfully returned")
    void getZipCodesTest() {
        Response response = client.getZipCodesList();

        Assertions.assertAll(
                () -> Assertions.assertTrue(client.getResponseAsList(response).containsAll(AVAILABLE_ZIP_CODES)),
                () -> Assertions.assertEquals(OK_STATUS, response.getStatusCode())
        );
    }

    // Scenario #2:
    @Test
    @AllureId("API-2")
    @Feature("Ability to Add Zip Codes")
    @Description("Check if new zip codes are successfully added")
    void addZipCodesTest() {
        List<String> newZipCodesList = Arrays.asList(
                new Faker().number().digits(5), new Faker().number().digits(5));
        Response response = client.addToZipCodesList(newZipCodesList);
        Assertions.assertAll(
                () -> Assertions.assertTrue(client.getResponseAsList(response).containsAll(newZipCodesList)),
                () -> Assertions.assertEquals(CREATED_STATUS, response.getStatusCode())
        );
    }

    // Scenario #3:
    @Test
    @AllureId("API-3")
    @Feature("Inability to Add Duplicates")
    @Description("Check if only distinct zip codes can be added")
    void addDuplicatedZipCodesTest() {
        String randomZipCode = new Faker().number().digits(5);
        List<String> duplicatedZipCodesList = Arrays.asList(randomZipCode, randomZipCode);
        Response response = client.addToZipCodesList(duplicatedZipCodesList);
        Assertions.assertAll(
                () -> Assertions.assertTrue(client.getResponseAsList(response).stream().allMatch(new HashSet<>()::add)),
                () -> Assertions.assertEquals(CREATED_STATUS, response.getStatusCode())
        );
    }

    // Scenario #4:
    @Test
    @AllureId("API-4")
    @Feature("Inability to Add Used Zip Codes")
    @Description("Check if used zip codes are not added")
    void addUsedZipCodesTest() {
        List<String> usedZipCodesList = Arrays.asList("12345", "23456");
        Response response = client.addToZipCodesList(usedZipCodesList);
        Assertions.assertAll(
                () -> usedZipCodesList.forEach(zipCode ->
                        Assertions.assertEquals(1, client.getResponseAsList(response).stream()
                                .filter(z -> z.equals(zipCode)).count())),
                () -> Assertions.assertEquals(CREATED_STATUS, response.getStatusCode())
        );
    }
}