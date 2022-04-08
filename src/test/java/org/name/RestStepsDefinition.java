package org.name;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class RestStepsDefinition {

    @Given("the users endpoint exists")
    public void preReq() {
        RestAssured.baseURI = "https://gorest.co.in/public-api/users";
    }

    @When("I send GET request")
    public void sendGet() {
        Response response = given()
                .auth()
                .none()
                .header("Accept", ContentType.JSON.getAcceptHeader())
                .contentType(ContentType.JSON)
                .get()
                .then().assertThat().statusCode(200).extract().response();
    }
}
