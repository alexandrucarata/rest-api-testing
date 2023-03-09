package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.User;
import data.UserPair;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static data.Constants.USERS_RESOURCE;
import static data.Constants.USERS_UPLOAD_RESOURCE;
import static io.restassured.RestAssured.given;

public class UserClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Step("Get Users List")
    public Response getUsersList() {
        return given()
                .header("Authorization", "Bearer " + Authentication.getToken(Scope.READ))
                .when()
                .get(USERS_RESOURCE);
    }

    @Step("Get Filtered Users List")
    public Response getUsersList(String key, String value) {
        return given()
                .header("Authorization", "Bearer " + Authentication.getToken(Scope.READ))
                .param(key, value)
                .when()
                .get(USERS_RESOURCE);
    }

    @Step("Add User to Users List")
    public Response addToUsersList(User user) {
        return given()
                .header("Authorization", "Bearer " + Authentication.getToken(Scope.WRITE))
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(USERS_RESOURCE);
    }

    @Step("Update User")
    public Response updateUser(UserPair userPair) {
        return given()
                .header("Authorization", "Bearer " + Authentication.getToken(Scope.WRITE))
                .contentType(ContentType.JSON)
                .body(userPair)
                .when()
                .patch(USERS_RESOURCE);
    }

    @Step("Delete User")
    public Response deleteUser(User user) {
        return given()
                .header("Authorization", "Bearer " + Authentication.getToken(Scope.WRITE))
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .delete(USERS_RESOURCE);
    }

    @Step("Upload User")
    public Response uploadUser(File file) {
        return given()
                .header("Authorization", "Bearer " + Authentication.getToken(Scope.WRITE))
                .contentType(ContentType.MULTIPART)
                .multiPart(file)
                .when()
                .post(USERS_UPLOAD_RESOURCE);
    }

    @Step("Create User File")
    public void createUsersFile(File file, List<User> userList) throws IOException {
        MAPPER.writeValue(file, userList);
    }

    public List<User> getResponseAsList(Response response) {
        return Arrays.stream(response.getBody().as(User[].class)).collect(Collectors.toList());
    }
}