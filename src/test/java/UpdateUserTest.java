import client.UserClient;
import client.ZipCodeClient;
import com.github.javafaker.Faker;
import data.User;
import data.UserPair;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static data.Constants.*;

@Epic("User Management")
@Story("Updating Users")
public class UpdateUserTest {
    private static UserClient userClient;
    private static ZipCodeClient zipCodeClient;
    private static Faker faker;
    private static User userToChange;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = PORT;
        userClient = new UserClient();
        zipCodeClient = new ZipCodeClient();
        faker = new Faker();
        userToChange = User.builder()
                .name(faker.name().firstName())
                .age(faker.number().numberBetween(1, 100))
                .sex(faker.demographic().sex().toUpperCase())
                .zipCode(zipCodeClient.getNewZipCode())
                .build();
        userClient.addToUsersList(userToChange);
    }

    // Scenario #1:
    @Test
    @AllureId("API-13")
    @Feature("Ability to Update User")
    @Description("Check if user is successfully updated")
    void updateUserTest() {
        User newUser = User.builder()
                .name(faker.name().firstName())
                .age(faker.number().numberBetween(1, 100))
                .sex(faker.demographic().sex().toUpperCase())
                .zipCode(userToChange.getZipCode())
                .build();
        Response response = userClient.updateUser(new UserPair(newUser, userToChange));
        List<User> userList = userClient.getResponseAsList(userClient.getUsersList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(OK_STATUS, response.getStatusCode()),
                () -> Assertions.assertFalse(userList.contains(userToChange)),
                () -> Assertions.assertTrue(userList.contains(newUser))
        );
    }

    // Scenario #2:
    @Test
    @AllureId("API-14")
    @Feature("Inability to Update User with Unavailable Zip Code")
    @Description("Check if user with unavailable zip code is not updated")
    void updateUnavailableZipUserTest() {
        User newUser = User.builder()
                .name(faker.name().firstName())
                .age(faker.number().numberBetween(1, 100))
                .sex(faker.demographic().sex().toUpperCase())
                .zipCode(faker.number().digits(5))
                .build();
        Response response = userClient.updateUser(new UserPair(newUser, userToChange));
        List<User> userList = userClient.getResponseAsList(userClient.getUsersList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(DEPENDENCY_STATUS, response.getStatusCode()),
                () -> Assertions.assertFalse(userList.contains(newUser)),
                () -> Assertions.assertTrue(userList.contains(userToChange))
        );
    }

    // Scenario #3:
    @Test
    @AllureId("API-15")
    @Feature("Inability to Update User without Required Fields")
    @Description("Check if user without required fields is not updated")
    void updateIncompleteUserZipTest() {
        User newUser = User.builder()
                .age(faker.number().numberBetween(1, 100))
                .zipCode(userToChange.getZipCode())
                .build();
        Response response = userClient.updateUser(new UserPair(newUser, userToChange));
        List<User> userList = userClient.getResponseAsList(userClient.getUsersList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(CONFLICT_STATUS, response.getStatusCode()),
                () -> Assertions.assertFalse(userList.contains(newUser)),
                () -> Assertions.assertTrue(userList.contains(userToChange))
        );
    }
}