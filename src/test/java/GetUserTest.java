import client.UserClient;
import com.github.javafaker.Faker;
import data.User;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static data.Constants.*;

@Epic("User Management")
@Story("Getting Users")
public class GetUserTest {
    private static UserClient userClient;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = PORT;
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
    void getAllUsersTest() {
        Response usersResponse = userClient.getUsersList();

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
    void getOlderUsersTest() {
        int ageLimit = 18;
        Response allUsersResponse = userClient.getUsersList();
        Response filteredUsersResponse = userClient.getUsersList("olderThan", String.valueOf(ageLimit));
        List<User> userList = userClient.getResponseAsList(allUsersResponse)
                .stream().filter(user -> user.getAge() > ageLimit).collect(Collectors.toList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(OK_STATUS, filteredUsersResponse.getStatusCode()),
                () -> Assertions.assertTrue(userClient.getResponseAsList(filteredUsersResponse).containsAll(userList))
        );
    }

    // Scenario #3:
    @Test
    @AllureId("API-11")
    @Feature("Ability to Get Younger User List")
    @Description("Check if user list with younger users is successfully returned")
    void getYoungerUsersTest() {
        int ageLimit = 18;
        Response allUsersResponse = userClient.getUsersList();
        Response filteredUsersResponse = userClient.getUsersList("youngerThan", String.valueOf(ageLimit));
        List<User> userList = userClient.getResponseAsList(allUsersResponse)
                .stream().filter(user -> user.getAge() < ageLimit).collect(Collectors.toList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(OK_STATUS, filteredUsersResponse.getStatusCode()),
                () -> Assertions.assertTrue(userClient.getResponseAsList(filteredUsersResponse).containsAll(userList))
        );
    }

    // Scenario #4:
    @Test
    @AllureId("API-12")
    @Feature("Ability to Get certain Sex User List")
    @Description("Check if user list with certain sex is successfully returned")
    void getUsersOfSexTest() {
        String sex = "FEMALE";
        Response allUsersResponse = userClient.getUsersList();
        Response filteredUsersResponse = userClient.getUsersList("sex", sex);
        List<User> userList = userClient.getResponseAsList(allUsersResponse)
                .stream().filter(user -> user.getSex().equals(sex)).collect(Collectors.toList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(OK_STATUS, filteredUsersResponse.getStatusCode()),
                () -> Assertions.assertTrue(userClient.getResponseAsList(filteredUsersResponse).containsAll(userList))
        );
    }
}