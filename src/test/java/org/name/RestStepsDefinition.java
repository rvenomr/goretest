package org.name;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.concurrent.ConcurrentHashMap;

public class RestStepsDefinition {
    private String token = "45234d95eac42cded7623d64819a122472497644beb5ed6977b00caeb8520611";
    private String data;
    private int id;
    private String acceptHeader;
    private ContentType contentType;

    @Given("we want to verify {string} requests and responses")
    public void preReq(String type) {
        type = type.toLowerCase();
        RestAssured.baseURI = Endpoints.URI + Endpoints.users;
        switch (type) {
            case "json":
                acceptHeader = ContentType.JSON.getAcceptHeader();
                contentType = ContentType.JSON;
                data = "data";
                break;
            case "xml":
                acceptHeader = ContentType.XML.getAcceptHeader();
                contentType = ContentType.XML;
                data = "data.datum";
        }
    }

    @When("I send GET request")
    public void sendGet() {
        System.out.println(Thread.currentThread().getId());
        Response response = RestAssured.given()
                .auth()
                .none()
                .header("Accept", acceptHeader)
                .contentType(contentType)
                .get()
                .then().assertThat().statusCode(200).extract().response();
    }

    @When("I send GET with previously received user ID")
    public void sendGetWithPreviousUserId() {
        System.out.println(Thread.currentThread().getId());
        Response response = RestAssured.given()
                .auth()
                .none()
                .header("Accept", acceptHeader)
                .contentType(contentType)
                .get("/" + id)
                .then().assertThat().statusCode(200).extract().response();
    }

    @When("I send POST request and then verify it")
    public void sendPostAndVeryfyIt() {
        System.out.println(Thread.currentThread().getId());
        Response response = RestAssured.given()
                .auth()
                .oauth2(token)
                .header("Accept", acceptHeader)
                .contentType(contentType)
                .body("{\"name\":\"Tenali Ramakrishna\", \"gender\":\"male\", \"email\":\"tenalis.ramakrishna@" + Math.random() + "ce.com\", \"status\":\"active\"}")
                .post()
                .then().assertThat().statusCode(200).extract().response();
        String id = response.jsonPath().getString(data + "id");

        response = RestAssured.given().auth().oauth2(token)
                .header("Accept", acceptHeader)
                .contentType(contentType)
                .get("/" + id)
                .then().assertThat().statusCode(200).extract().response();
    }

    @When("I send POST")
    public void sendPost() {
        System.out.println(Thread.currentThread().getId());
        Response response = RestAssured.given()
                .auth()
                .oauth2(token)
                .header("Accept", acceptHeader)
                .contentType(contentType)
                .body("{\"name\":\"Tenali Ramakrishna\", \"gender\":\"male\", \"email\":\"tenalis.ramakrishna@" + Math.random() + "ce.com\", \"status\":\"active\"}")
                .post()
                .then().assertThat().statusCode(200).extract().response();
        id = response.jsonPath().getInt(data + ".id");
    }

    @When("I send PUT")
    public void sendPUT() {
        System.out.println(Thread.currentThread().getId());
        Response response = RestAssured.given().auth().oauth2(token)
                .header("Accept", acceptHeader)
                .contentType(contentType)
                .body("{\"name\":\"Tenali Ramakrishna\", \"gender\":\"male\", \"email\":\"tenalis.ramakrishna@19ce.com\", \"status\":\"active\"}")
                .get("/" + id)
                .then().assertThat().statusCode(200).extract().response();
    }
}
