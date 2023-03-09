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
@Story("Deleting Users")
public class DeleteUserTest {
    private static UserClient userClient;
    private static ZipCodeClient zipCodeClient;
    private static User user;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = PORT;
        userClient = new UserClient();
        zipCodeClient = new ZipCodeClient();
        Faker faker = new Faker();
        user = User.builder()
                .name(faker.name().firstName())
                .age(faker.number().numberBetween(1, 100))
                .sex(faker.demographic().sex().toUpperCase())
                .zipCode(zipCodeClient.getNewZipCode())
                .build();
        userClient.addToUsersList(user);
    }

    // Scenario #1:
    @Test
    @AllureId("API-16")
    @Feature("Ability to Delete User")
    @Description("Check if user is successfully removed")
    void deleteCompleteUserTest() {
        Response response = userClient.deleteUser(user);
        List<User> userList = userClient.getResponseAsList(userClient.getUsersList());
        String zipCode = user.getZipCode();
        List<String> zipCodeList = zipCodeClient.getResponseAsList(zipCodeClient.getZipCodesList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(NO_CONTENT_STATUS, response.getStatusCode()),
                () -> Assertions.assertFalse(userList.contains(user)),
                () -> Assertions.assertTrue(zipCodeList.contains(zipCode))
        );
    }

    // Scenario #2:
    @Test
    @AllureId("API-17")
    @Feature("Ability to Delete User with Required Fields")
    @Description("Check if user is successfully removed by only required fields")
    void deleteRequiredUserTest() {
        User userToDelete = User.builder()
                .name(user.getName())
                .sex(user.getSex())
                .build();
        Response response = userClient.deleteUser(userToDelete);
        List<User> userList = userClient.getResponseAsList(userClient.getUsersList());
        String zipCode = user.getZipCode();
        List<String> zipCodeList = zipCodeClient.getResponseAsList(zipCodeClient.getZipCodesList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(NO_CONTENT_STATUS, response.getStatusCode()),
                () -> Assertions.assertFalse(userList.contains(user)),
                () -> Assertions.assertTrue(zipCodeList.contains(zipCode))
        );
    }

    // Scenario #3:
    @Test
    @AllureId("API-18")
    @Feature("Inability to Delete User without Required Fields")
    @Description("Check if user without required fields is not removed")
    void deleteIncompleteUserTest() {
        User userToDelete = User.builder()
                .name(user.getName())
                .age(user.getAge())
                .zipCode(user.getZipCode())
                .build();
        Response response = userClient.deleteUser(userToDelete);
        List<User> userList = userClient.getResponseAsList(userClient.getUsersList());
        String zipCode = user.getZipCode();
        List<String> zipCodeList = zipCodeClient.getResponseAsList(zipCodeClient.getZipCodesList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(CONFLICT_STATUS, response.getStatusCode()),
                () -> Assertions.assertTrue(userList.contains(user)),
                () -> Assertions.assertFalse(zipCodeList.contains(zipCode))
        );
    }
}