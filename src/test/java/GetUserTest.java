import client.UserClient;
import com.github.javafaker.Faker;
import data.User;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static data.Constants.OK_STATUS;

@Epic("User Management")
@Story("Getting Users")
public class GetUserTest {
    private static UserClient userClient;

    @BeforeEach
    public void setup() throws IOException {
        userClient = new UserClient();
        Faker faker = new Faker();
        for (int i = 0; i < 3; i++) {
            User user = User.builder()
                    .name(faker.name().firstName())
                    .age(faker.number().numberBetween(1, 100))
                    .sex(faker.demographic().sex().toUpperCase())
                    .build();
            userClient.addToUsersList(user);
        }
    }

    // Scenario #1:
    @Test
    @AllureId("API-9")
    @Feature("Ability to Get User List")
    @Description("Check if user list is successfully returned")
    void getAllUsersTest() throws IOException {
        Response<List<User>> usersResponse = userClient.getUsersList();

        Assertions.assertAll(
                () -> Assertions.assertEquals(OK_STATUS, usersResponse.getStatusCode()),
                () -> Assertions.assertNotNull(usersResponse.getBody())
        );
    }

    // Scenario #2:
    @Test
    @AllureId("API-10")
    @Feature("Ability to Get Older User List")
    @Description("Check if user list with older users is successfully returned")
    void getOlderUsersTest() throws IOException, URISyntaxException {
        int ageLimit = 18;
        Response<List<User>> allUsersResponse = userClient.getUsersList();
        Response<List<User>> filteredUsersResponse = userClient.getUsersList("olderThan", String.valueOf(ageLimit));
        List<User> userList = allUsersResponse.getBody()
                .stream().filter(user -> user.getAge() > ageLimit).collect(Collectors.toList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(OK_STATUS, filteredUsersResponse.getStatusCode()),
                () -> Assertions.assertTrue(filteredUsersResponse.getBody().containsAll(userList))
        );
    }

    // Scenario #3:
    @Test
    @AllureId("API-11")
    @Feature("Ability to Get Younger User List")
    @Description("Check if user list with younger users is successfully returned")
    void getYoungerUsersTest() throws IOException, URISyntaxException {
        int ageLimit = 18;
        Response<List<User>> allUsersResponse = userClient.getUsersList();
        Response<List<User>> filteredUsersResponse = userClient.getUsersList("youngerThan", String.valueOf(ageLimit));
        List<User> userList = allUsersResponse.getBody()
                .stream().filter(user -> user.getAge() < ageLimit).collect(Collectors.toList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(OK_STATUS, filteredUsersResponse.getStatusCode()),
                () -> Assertions.assertTrue(filteredUsersResponse.getBody().containsAll(userList))
        );
    }

    // Scenario #4:
    @Test
    @AllureId("API-12")
    @Feature("Ability to Get certain Sex User List")
    @Description("Check if user list with certain sex is successfully returned")
    void getUsersOfSexTest() throws IOException, URISyntaxException {
        String sex = "FEMALE";
        Response<List<User>> allUsersResponse = userClient.getUsersList();
        Response<List<User>> filteredUsersResponse = userClient.getUsersList("sex", sex);
        List<User> userList = allUsersResponse.getBody()
                .stream().filter(user -> user.getSex().equals(sex)).collect(Collectors.toList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(OK_STATUS, filteredUsersResponse.getStatusCode()),
                () -> Assertions.assertTrue(filteredUsersResponse.getBody().containsAll(userList))
        );
    }
}