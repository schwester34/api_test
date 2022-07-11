package tests;

import io.restassured.response.Response;
import models.DataModel;
import models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static specs.Specs.*;

public class ReqresTest extends TestBase {
    @Test
    @DisplayName("Search for user information")
    void singleUserTest() {
        DataModel response =
                given()
                        .spec(request)
                        .when()
                        .get("/users/2")
                        .then()
                        .spec(response200)
                        .log().body()
                        .extract().as(DataModel.class);

        Integer id = 2;
        String email = "janet.weaver@reqres.in";
        String firstName = "Janet";
        String lastName = "Weaver";

        assertEquals(id, response.getUser().getId());
        assertEquals(email, response.getUser().getEmail());
        assertEquals(firstName, response.getUser().getFirstName());
        assertEquals(lastName, response.getUser().getLastName());
    }

    @Test
    @DisplayName("Getting a list of users")
    void listUserTest() {
        Response response =
                given()
                        .spec(request)
                        .when()
                        .get("/users?page=2")
                        .then()
                        .spec(response200)
                        .log().body()
                        .extract().response();

        Integer id = 12;
        String email = "rachel.howell@reqres.in";
        String firstName = "Rachel";
        String lastName = "Howell";

        assertEquals(id, response.path("data[5].id"));
        assertEquals(email, response.path("data[5].email"));
        assertEquals(firstName, response.path("data[5].first_name"));
        assertEquals(lastName, response.path("data[5].last_name"));
    }

    @Test
    @DisplayName("Creating a user")
    void createUserTest() {
        User user = new User();
        user.setName("morpheus");
        user.setJob("leader");

        User responseUser =
                given()
                        .spec(request)
                        .body(user)
                        .when()
                        .post("/users")
                        .then()
                        .spec(response201)
                        .log().body()
                        .extract().as(User.class);

        assertNotEquals(responseUser.getId(), null);
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getJob(), responseUser.getJob());
    }

    @Test
    @DisplayName("Successful registration")
    void registerSuccessfulTest() {
        User user = new User();
        user.setEmail("eve.holt@reqres.in");
        user.setPassword("pistol");

        User responseUser =
                given()
                        .spec(request)
                        .body(user)
                        .when()
                        .post("/register")
                        .then()
                        .spec(response200)
                        .log().body()
                        .extract().as(User.class);

        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";

        assertEquals(id, responseUser.getId());
        assertEquals(token, responseUser.getToken());
    }

    @Test
    @DisplayName("Unsuccessful registration")
    void registerUnsuccessfulTest() {
        User user = new User();
        user.setEmail("sydney@fife");

        Response response =
                given()
                        .spec(request)
                        .body(user)
                        .when()
                        .post("/register")
                        .then()
                        .spec(response400)
                        .log().body()
                        .extract().response();

        String message = "Missing password";
        assertEquals(message, response.path("error"));
    }
}
