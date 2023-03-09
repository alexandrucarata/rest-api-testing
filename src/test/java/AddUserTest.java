import client.UserClient;
import client.ZipCodeClient;
import com.github.javafaker.Faker;
import data.Response;
import data.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static data.Constants.*;

public class AddUserTest {
    private static UserClient userClient;
    private static ZipCodeClient zipCodeClient;
    private static Faker faker;

    @BeforeEach
    public void setup() {
        userClient = new UserClient();
        zipCodeClient = new ZipCodeClient();
        faker = new Faker();
    }

    // Scenario #1:
    @Test
    void addUserTest() throws IOException {
        String zipCode = zipCodeClient.getNewZipCode();
        User user = User.builder()
                .name(faker.name().firstName())
                .age(faker.number().numberBetween(1, 100))
                .sex(faker.demographic().sex().toUpperCase())
                .zipCode(zipCode)
                .build();
        int responseStatusCode = userClient.addToUsersList(user);
        Response<List<User>> usersResponse = userClient.getUsersList();
        Response<List<String>> zipCodesResponse = zipCodeClient.getZipCodesList();

        Assertions.assertAll(
                () -> Assertions.assertEquals(CREATED_STATUS, responseStatusCode),
                () -> Assertions.assertTrue(usersResponse.getBody().contains(user)),
                () -> Assertions.assertFalse(zipCodesResponse.getBody().contains(zipCode))
        );
    }

    // Scenario #2:
    @Test
    void addRequiredUserTest() throws IOException {
        User user = User.builder()
                .name(faker.name().firstName())
                .sex(faker.demographic().sex().toUpperCase())
                .build();
        int responseStatusCode = userClient.addToUsersList(user);
        Response<List<User>> usersResponse = userClient.getUsersList();

        Assertions.assertAll(
                () -> Assertions.assertEquals(CREATED_STATUS, responseStatusCode),
                () -> Assertions.assertTrue(usersResponse.getBody().contains(user))
        );
    }

    // Scenario #3:
    @Test
    void addUnavailableUserTest() throws IOException {
        User user = User.builder()
                .name(faker.name().firstName())
                .age(faker.number().numberBetween(1, 100))
                .sex(faker.demographic().sex().toUpperCase())
                .zipCode(faker.number().digits(5))
                .build();
        int responseStatusCode = userClient.addToUsersList(user);
        Response<List<User>> usersResponse = userClient.getUsersList();

        Assertions.assertAll(
                () -> Assertions.assertEquals(DEPENDENCY_STATUS, responseStatusCode),
                () -> Assertions.assertFalse(usersResponse.getBody().contains(user))
        );
    }

    // Scenario #4:
    @Test
    void addDuplicateUserTest() throws IOException {
        Response<List<User>> usersResponse = userClient.getUsersList();
        List<User> usersList = usersResponse.getBody();
        User user = User.builder()
                .name(usersList.get(0).getName())
                .sex(usersList.get(0).getSex())
                .build();
        int responseStatusCode = userClient.addToUsersList(user);
        List<User> finalUsersList = userClient.getUsersList().getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals(BAD_STATUS, responseStatusCode),
                () -> Assertions.assertFalse(finalUsersList.contains(user))
        );
    }
}