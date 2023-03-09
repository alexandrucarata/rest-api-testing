import client.ZipCodeClient;
import com.github.javafaker.Faker;
import data.Response;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static data.Constants.CREATED_STATUS;
import static data.Constants.OK_STATUS;

@Epic("Zip Code Management")
@Story("Adding Zip Codes")
public class ZipCodeTest {
    private static final List<String> AVAILABLE_ZIP_CODES = Arrays.asList("12345", "23456", "ABCDE");
    private static ZipCodeClient client;

    @BeforeEach
    public void setup() {
        client = new ZipCodeClient();
    }

    // Scenario #1:
    @Test
    @Issue("1")
    @AllureId("API-1")
    @Feature("Ability to Get Zip Codes List")
    @Description("Check if zip codes list is successfully returned")
    void getZipCodesTest() throws IOException {
        Response<List<String>> response = client.getZipCodesList();
        Assertions.assertAll(
                () -> Assertions.assertTrue(response.getBody().containsAll(AVAILABLE_ZIP_CODES)),
                () -> Assertions.assertEquals(OK_STATUS, response.getStatusCode())
        );
    }

    // Scenario #2:
    @Test
    @AllureId("API-2")
    @Feature("Ability to Add Zip Codes")
    @Description("Check if new zip codes are successfully added")
    void addZipCodesTest() throws IOException {
        List<String> newZipCodesList = Arrays.asList(
                new Faker().number().digits(5), new Faker().number().digits(5));
        Response<List<String>> response = client.addToZipCodesList(newZipCodesList);
        Assertions.assertAll(
                () -> Assertions.assertTrue(response.getBody().containsAll(newZipCodesList)),
                () -> Assertions.assertEquals(CREATED_STATUS, response.getStatusCode())
        );
    }

    // Scenario #3:
    @Test
    @Issue("2")
    @AllureId("API-3")
    @Feature("Inability to Add Duplicates")
    @Description("Check if only distinct zip codes can be added")
    void addDuplicatedZipCodesTest() throws IOException {
        String randomZipCode = new Faker().number().digits(5);
        List<String> duplicatedZipCodesList = Arrays.asList(randomZipCode, randomZipCode);
        Response<List<String>> response = client.addToZipCodesList(duplicatedZipCodesList);
        Assertions.assertAll(
                () -> Assertions.assertTrue(response.getBody().stream().allMatch(new HashSet<>()::add)),
                () -> Assertions.assertEquals(CREATED_STATUS, response.getStatusCode())
        );
    }

    // Scenario #4:
    @Test
    @Issue("3")
    @AllureId("API-4")
    @Feature("Inability to Add Used Zip Codes")
    @Description("Check if used zip codes are not added")
    void addUsedZipCodesTest() throws IOException {
        List<String> usedZipCodesList = Arrays.asList("12345", "23456");
        Response<List<String>> response = client.addToZipCodesList(usedZipCodesList);
        Assertions.assertAll(
                () -> usedZipCodesList.forEach(zipCode ->
                        Assertions.assertEquals(1, response.getBody().stream()
                                .filter(z -> z.equals(zipCode)).count())),
                () -> Assertions.assertEquals(CREATED_STATUS, response.getStatusCode())
        );
    }
}