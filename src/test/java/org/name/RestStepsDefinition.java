package org.name;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.name.data.User;
import org.testng.Assert;
import org.testng.Reporter;

public class RestStepsDefinition {
    private String token = "45234d95eac42cded7623d64819a122472497644beb5ed6977b00caeb8520611";
    private String dataPrefix;
    private int id;
    private ContentType acceptContentType;
    private ContentType contentType;
    private User user;
    private String appendix = "";
    private Response response;

   @Given("we want to verify {string} responses")
    public void preReq(String type) {
        type = type.toLowerCase();
        RestAssured.baseURI = Endpoints.URI;
        switch (type) {
            case "json" -> {
                acceptContentType = ContentType.JSON;
                contentType = ContentType.JSON;
                dataPrefix = "";
            }
            case "xml" -> {
                acceptContentType = ContentType.XML;
                contentType = ContentType.JSON;
                appendix = ".xml";
                dataPrefix = "hash.";
            }
        }
    }

    @When("send GET request")
    public void sendGet() {
        Reporter.log("Thread: "+ Thread.currentThread().getId() + " execute 'send GET request' step. Accept type: " + acceptContentType, true);
        response = RestAssured.given()
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .get(Endpoints.api + Endpoints.user + appendix)
                .then().assertThat().statusCode(200).extract().response();
    }


    @When("send GET with previously received user ID")
    public void sendGetWithPreviousUserId() {
        Reporter.log("Thread: "+ Thread.currentThread().getId() + " execute 'send GET with previously received user ID' step. Accept type: " + acceptContentType, true);
        RequestSpecification requestSpecification = new RequestSpecBuilder().setBaseUri(Endpoints.URI + Endpoints.api + Endpoints.user).build();
        String getPath = "/" + id + appendix;
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .get(getPath)
                .then().assertThat().statusCode(200).extract().response();
    }
    
    

    private void verifyThatStatusCodeIs(int status, Response response) {
        if (acceptContentType.toString().contains("json")) {
            Assert.assertEquals(response.path(dataPrefix + "code"), new Integer(status));
        }
        if (acceptContentType.toString().contains("xml")) {
            Assert.assertEquals(response.path(dataPrefix + "code"), "" + status);
        }
    }

    @When("send POST")
    public void sendPost() {
        Reporter.log("Thread: "+ Thread.currentThread().getId() + " execute 'send POST' step. Accept type: " + acceptContentType, true);
        user = new User("tenalis.ramakrishna@" + Math.random() + "ce.com",
                "Tenali Ramakrishna", "male", "active");
        response = RestAssured.given()
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .post(Endpoints.api + Endpoints.user + appendix)
                .then().assertThat().statusCode(200).extract().response();
        writeDownId(response);
    }

    private void writeDownId(Response response) {
        if (acceptContentType.toString().contains("json")) {
            id = response.jsonPath().getInt(dataPrefix + "data.id");
        }
        if (acceptContentType.toString().contains("xml")) {
            id = response.xmlPath().getInt(dataPrefix + "data.id");
        }
    }

    @When("send PUT")
    public void sendPUT() {
        Reporter.log("Thread: " + Thread.currentThread().getId() + " execute 'send PUT' step. Accept type: " + acceptContentType, true);
        user = new User("tenalis.ramakrishna@" + Math.random() + "ce.com",
                "Tenali Ramakrishna", "male", "active");
        response = RestAssured.given().auth().oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .put(Endpoints.api + Endpoints.user + "/" + id + appendix)
                .then().assertThat().statusCode(200).extract().response();
    }

    @When("send POST with illformated data")
    public void iSendRequestWithIllformatedData() {
        RequestSpecification requestSpecification = new RequestSpecBuilder().setBaseUri(Endpoints.URI + Endpoints.api).build();
        Reporter.log("Thread: " + Thread.currentThread().getId() + " execute 'send POST with illformated data' step. Accept type:" + acceptContentType);
        user = new User("tenalis.ramakrishna@" + Math.random() + "ce.com",
                "Tenali Ramakrishna", "malel", "active");
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .post(Endpoints.user + appendix)
                .then().assertThat().statusCode(200).extract().response();
        verifyThatStatusCodeIs(422, response);
        user = new User("tenalis.ramakrishna@" + Math.random() + "ce.com",
                "Tenali Ramakrishna", "male", "activel");
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .post(Endpoints.user + appendix)
                .then().assertThat().statusCode(200).extract().response();
        verifyThatStatusCodeIs(422, response);
        user = new User("tenalis.ramakrishn@a@" + Math.random() + "ce.com",
                "Tenali Ramakrishna", "male", "active");
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .post(Endpoints.user + appendix)
                .then().assertThat().statusCode(200).extract().response();
        verifyThatStatusCodeIs(422, response);
        user = new User("tenalis.ramakrishna@" + Math.random() + "ce.com",
                "", "male", "active");
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .post(Endpoints.user + appendix)
                .then().assertThat().statusCode(200).extract().response();
        verifyThatStatusCodeIs(422, response);
    }

    @When("send PUT with illformated data")
    public void iSendPutWithIllformatedData() {
        RequestSpecification requestSpecification = new RequestSpecBuilder().setBaseUri(Endpoints.URI + Endpoints.api).build();
        Reporter.log("Thread: " + Thread.currentThread().getId() + " execute 'send PUT with illformated data' step. Accept type: " + acceptContentType, true);
        user = new User("tenalis.ramakrishna@" + Math.random() + "ce.com",
                "Tenali Ramakrishna", "malel", "active");
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .put(Endpoints.user + "/" + id + appendix)
                .then().assertThat().statusCode(200).extract().response();
        verifyThatStatusCodeIs(422, response);
        user = new User("tenalis.ramakrishna@" + Math.random() + "ce.com",
                "Tenali Ramakrishna", "male", "activel");
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .put(Endpoints.user + "/" + id + appendix)
                .then().assertThat().statusCode(200).extract().response();
        verifyThatStatusCodeIs(422, response);
        user = new User("tenalis.ramakrishn@a@" + Math.random() + "ce.com",
                "Tenali Ramakrishna", "male", "active");
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .put(Endpoints.user + "/" + id + appendix)
                .then().assertThat().statusCode(200).extract().response();
        verifyThatStatusCodeIs(422, response);
        user = new User("tenalis.ramakrishna@" + Math.random() + "ce.com",
                "", "male", "active");
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .put(Endpoints.user + "/" + id + appendix)
                .then().assertThat().statusCode(200).extract().response();
        verifyThatStatusCodeIs(422, response);
    }

    @When("send DELETE with previously received user ID")
    public void sendDeleteWithPreviousUserId() {
        Reporter.log("Thread: " + Thread.currentThread().getId() + " execute 'send DELETE with previously received user ID' step. Accept type: " + acceptContentType, true);
        RequestSpecification requestSpecification = new RequestSpecBuilder().setBaseUri(Endpoints.URI + Endpoints.api + Endpoints.user).build();
        String getPath = "/" + id + appendix;
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .delete(getPath)
                .then().assertThat().statusCode(200).extract().response();
    }

    @When("verify that status code is {int}")
    public void verifyThatStatusCodeIs(int code) {
        verifyThatStatusCodeIs(code, response);
    }

    @When("verify that response contains user")
    public void verifyThatResponseContainsUser() {
        Assert.assertEquals(response.path(dataPrefix + "data.name"), user.getName());
        Assert.assertEquals(response.path(dataPrefix + "data.email"), user.getEmail());
        Assert.assertEquals(response.path(dataPrefix + "data.gender"), user.getGender());
        Assert.assertEquals(response.path(dataPrefix + "data.status"), user.getStatus());
    }
}
