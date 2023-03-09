import client.UserClient;
import client.ZipCodeClient;
import com.github.javafaker.Faker;
import data.User;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static data.Constants.CONFLICT_STATUS;
import static data.Constants.NO_CONTENT_STATUS;

@Epic("User Management")
@Story("Deleting Users")
public class DeleteUserTest {
    private static UserClient userClient;
    private static ZipCodeClient zipCodeClient;
    private static User user;

    @BeforeEach
    public void setup() throws IOException {
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
    void deleteCompleteUserTest() throws IOException {
        int response = userClient.deleteUser(user);
        List<User> userList = userClient.getUsersList().getBody();
        String zipCode = user.getZipCode();
        List<String> zipCodeList = zipCodeClient.getZipCodesList().getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals(NO_CONTENT_STATUS, response),
                () -> Assertions.assertFalse(userList.contains(user)),
                () -> Assertions.assertTrue(zipCodeList.contains(zipCode))
        );
    }

    // Scenario #2:
    @Test
    @Issue("7")
    @AllureId("API-17")
    @Feature("Ability to Delete User with Required Fields")
    @Description("Check if user is successfully removed by only required fields")
    void deleteRequiredUserTest() throws IOException {
        User userToDelete = User.builder()
                .name(user.getName())
                .sex(user.getSex())
                .build();
        int response = userClient.deleteUser(userToDelete);
        List<User> userList = userClient.getUsersList().getBody();
        String zipCode = user.getZipCode();
        List<String> zipCodeList = zipCodeClient.getZipCodesList().getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals(NO_CONTENT_STATUS, response),
                () -> Assertions.assertFalse(userList.contains(user)),
                () -> Assertions.assertTrue(zipCodeList.contains(zipCode))
        );
    }

    // Scenario #3:
    @Test
    @AllureId("API-18")
    @Feature("Inability to Delete User without Required Fields")
    @Description("Check if user without required fields is not removed")
    void deleteIncompleteUserTest() throws IOException {
        User userToDelete = User.builder()
                .name(user.getName())
                .age(user.getAge())
                .zipCode(user.getZipCode())
                .build();
        int response = userClient.deleteUser(userToDelete);
        List<User> userList = userClient.getUsersList().getBody();
        String zipCode = user.getZipCode();
        List<String> zipCodeList = zipCodeClient.getZipCodesList().getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals(CONFLICT_STATUS, response),
                () -> Assertions.assertTrue(userList.contains(user)),
                () -> Assertions.assertFalse(zipCodeList.contains(zipCode))
        );
    }
}