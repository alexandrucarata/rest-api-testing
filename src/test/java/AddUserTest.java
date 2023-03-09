import client.UserClient;
import client.ZipCodeClient;
import com.github.javafaker.Faker;
import data.User;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static data.Constants.*;

@Epic("User Management")
@Story("Adding Users")
public class AddUserTest {
    private static UserClient userClient;
    private static ZipCodeClient zipCodeClient;
    private static Faker faker;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = PORT;
        userClient = new UserClient();
        zipCodeClient = new ZipCodeClient();
        faker = new Faker();
    }

    // Scenario #1:
    @Test
    @AllureId("API-5")
    @Feature("Ability to Add New User")
    @Description("Check if new user is successfully added")
    void addUserTest() {
        String zipCode = zipCodeClient.getNewZipCode();
        User user = User.builder()
                .name(faker.name().firstName())
                .age(faker.number().numberBetween(1, 100))
                .sex(faker.demographic().sex().toUpperCase())
                .zipCode(zipCode)
                .build();
        Response addUserResponse = userClient.addToUsersList(user);
        Response usersResponse = userClient.getUsersList();
        Response zipCodesResponse = zipCodeClient.getZipCodesList();

        Assertions.assertAll(
                () -> Assertions.assertEquals(CREATED_STATUS, addUserResponse.getStatusCode()),
                () -> Assertions.assertTrue(userClient.getResponseAsList(usersResponse).contains(user)),
                () -> Assertions.assertFalse(zipCodeClient.getResponseAsList(zipCodesResponse).contains(zipCode))
        );
    }

    // Scenario #2:
    @Test
    @AllureId("API-6")
    @Feature("Ability to Add New User With Required Fields")
    @Description("Check if new user with only required fields is successfully added")
    void addRequiredUserTest() {
        User user = User.builder()
                .name(faker.name().firstName())
                .sex(faker.demographic().sex().toUpperCase())
                .build();
        Response addUserResponse = userClient.addToUsersList(user);
        Response usersResponse = userClient.getUsersList();

        Assertions.assertAll(
                () -> Assertions.assertEquals(CREATED_STATUS, addUserResponse.getStatusCode()),
                () -> Assertions.assertTrue(userClient.getResponseAsList(usersResponse).contains(user))
        );
    }

    // Scenario #3:
    @Test
    @AllureId("API-7")
    @Feature("Inability to Add New User With Unavailable Zip Code")
    @Description("Check if new user with unavailable zip code is not added")
    void addUnavailableUserTest() {
        User user = User.builder()
                .name(faker.name().firstName())
                .age(faker.number().numberBetween(1, 100))
                .sex(faker.demographic().sex().toUpperCase())
                .zipCode(faker.number().digits(5))
                .build();
        Response addUserResponse = userClient.addToUsersList(user);
        Response usersResponse = userClient.getUsersList();

        Assertions.assertAll(
                () -> Assertions.assertEquals(DEPENDENCY_STATUS, addUserResponse.getStatusCode()),
                () -> Assertions.assertFalse(userClient.getResponseAsList(usersResponse).contains(user))
        );
    }

    // Scenario #4:
    @Test
    @AllureId("API-8")
    @Feature("Inability to Add Duplicate User")
    @Description("Check if duplicate user is not added")
    void addDuplicateUserTest() {
        Response usersResponse = userClient.getUsersList();
        List<User> usersList = userClient.getResponseAsList(usersResponse);
        User user = User.builder()
                .name(usersList.get(0).getName())
                .sex(usersList.get(0).getSex())
                .build();
        Response addUserResponse = userClient.addToUsersList(user);

        Assertions.assertAll(
                () -> Assertions.assertEquals(BAD_STATUS, addUserResponse.getStatusCode()),
                () -> Assertions.assertFalse(usersList.contains(user))
        );
    }
}