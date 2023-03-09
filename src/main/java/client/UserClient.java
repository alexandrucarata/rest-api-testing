package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.Response;
import data.User;
import data.UserPair;
import org.apache.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static data.Constants.USERS_RESOURCE;
import static data.Constants.USERS_UPLOAD_RESOURCE;

public class UserClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Response<List<User>> getUsersList() throws IOException {
        Response<List<User>> response = new Response<>();
        HttpResponse httpResponse = executeGetUsersList();
        response.setBody(getResponseAsList(httpResponse));
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return response;
    }

    public Response<List<User>> getUsersList(String key, String value) throws IOException, URISyntaxException {
        Response<List<User>> response = new Response<>();
        HttpResponse httpResponse = executeGetUsersList(key, value);
        response.setBody(getResponseAsList(httpResponse));
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return response;
    }

    public int addToUsersList(User user) throws IOException {
        HttpResponse response = executePostUser(user);
        return response.getStatusLine().getStatusCode();
    }

    public int updateUser(UserPair userPair) throws IOException {
        HttpResponse response = executePatchUser(userPair);
        return response.getStatusLine().getStatusCode();
    }

    public int deleteUser(User user) throws IOException {
        HttpResponse response = executeDeleteUser(user);
        return response.getStatusLine().getStatusCode();
    }

    public int uploadUser(File file, String fileName) throws IOException {
        HttpResponse response = executePostUser(file, fileName);
        return response.getStatusLine().getStatusCode();
    }

    public void createUsersFile(File file, List<User> userList) throws IOException {
        MAPPER.writeValue(file, userList);
    }

    private static HttpResponse executeGetUsersList() throws IOException {
        return Client.executeGet(USERS_RESOURCE);
    }

    private static HttpResponse executeGetUsersList(String key, String value) throws IOException, URISyntaxException {
        return Client.executeGet(USERS_RESOURCE, key, value);
    }

    private static HttpResponse executePostUser(User user) throws IOException {
        String body = MAPPER.writeValueAsString(user);
        return Client.executePost(USERS_RESOURCE, body);
    }

    private static HttpResponse executePostUser(File file, String fileName) throws IOException {
        return Client.executePost(USERS_UPLOAD_RESOURCE, file, fileName);
    }

    private static HttpResponse executePatchUser(UserPair userPair) throws IOException {
        String body = MAPPER.writeValueAsString(userPair);
        return Client.executePatch(USERS_RESOURCE, body);
    }

    private static HttpResponse executeDeleteUser(User user) throws IOException {
        String body = MAPPER.writeValueAsString(user);
        return Client.executeDelete(USERS_RESOURCE, body);
    }

    private static List<User> getResponseAsList(HttpResponse response) throws IOException {
        return Arrays.stream(MAPPER.readValue(response.getEntity().getContent(), User[].class))
                .collect(Collectors.toList());
    }
}