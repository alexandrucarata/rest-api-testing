import client.UserClient;
import client.ZipCodeClient;
import com.github.javafaker.Faker;
import data.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static data.Constants.*;

public class UploadUserTest {
    private static UserClient userClient;
    private static Faker faker;
    private static List<User> userList;

    @BeforeEach
    public void setup() throws IOException {
        userClient = new UserClient();
        faker = new Faker();
        userList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            User user = User.builder()
                    .name(faker.name().firstName())
                    .age(faker.number().numberBetween(1, 100))
                    .sex(faker.demographic().sex().toUpperCase())
                    .zipCode(new ZipCodeClient().getNewZipCode())
                    .build();
            userList.add(user);
        }
    }

    @Test
    void uploadValidUsersTest() throws IOException {
        File file = new File("src/test/resources/validUsers.json");
        userClient.createUsersFile(file, userList);
        int statusCode = userClient.uploadUser(file, "validUsers.json");
        List<User> finalUserList = userClient.getUsersList().getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals(CREATED_STATUS, statusCode),
                () -> Assertions.assertEquals(userList, finalUserList)
        );
    }

    @Test
    void uploadInvalidZipUsersTest() throws IOException {
        userList.get(0).setZipCode(faker.number().digits(5));
        File file = new File("src/test/resources/invalidZipUsers.json");
        userClient.createUsersFile(file, userList);
        int statusCode = userClient.uploadUser(file, "invalidZipUsers.json");
        List<User> finalUserList = userClient.getUsersList().getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals(DEPENDENCY_STATUS, statusCode),
                () -> Assertions.assertTrue(Collections.disjoint(finalUserList, userList))
        );
    }

    @Test
    void uploadIncompleteUsersTest() throws IOException {
        userList.get(0).setName(null);
        File file = new File("src/test/resources/incompleteUsers.json");
        userClient.createUsersFile(file, userList);
        int statusCode = userClient.uploadUser(file, "incompleteUsers.json");
        List<User> finalUserList = userClient.getUsersList().getBody();

        Assertions.assertAll(
                () -> Assertions.assertEquals(CONFLICT_STATUS, statusCode),
                () -> Assertions.assertTrue(Collections.disjoint(finalUserList, userList))
        );
    }
}