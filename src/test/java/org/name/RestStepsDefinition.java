package org.name;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.name.data.JdbcPoolTool;
import org.name.data.User;
import org.testng.Assert;
import org.testng.Reporter;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RestStepsDefinition {
    private String token = "45234d95eac42cded7623d64819a122472497644beb5ed6977b00caeb8520611";
    private String dataPrefix;
    private int id;
    private ContentType acceptContentType;
    private ContentType contentType;
    private Response response;
    private RequestSpecification requestSpecification;
    private User user;
    private String appendix = "";
    private static JdbcPoolTool jdbcPoolTool;
    private String xRateLimitLimit = "10000";

    @BeforeAll
    public static void beforeAll() {
        try {
            jdbcPoolTool = new JdbcPoolTool();
        } catch (SQLException se) {
            Assert.assertTrue(false, se.getMessage());
        }
    }

    @AfterAll
    public static void affterAll() {
        try {
            jdbcPoolTool.close();

        } catch (SQLException se) {
            Reporter.log(se.getMessage(), true);
        }
    }

    @Given("we want to verify {string} responses")
    public void preReq(String type) {
        type = type.toLowerCase();
        RestAssured.baseURI = Endpoints.URI + Endpoints.api;
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
        Reporter.log("Thread: " + Thread.currentThread().getId() + " execute 'send GET request' step. Accept type: " + acceptContentType, true);
        RequestSpecification requestSpecification = getDefaultRequestConfiguration();
        sendGet(Endpoints.user + appendix, requestSpecification);
    }

    @When("send GET request to path {string}")
    public void sendGetToPath(String path) {
        Reporter.log("Thread: " + Thread.currentThread().getId() + " execute 'send GET request to path " + path + "' step. Accept type: " + acceptContentType, true);
        String[] pathA = path.split("/");
        path = pathA[pathA.length - 1];
        pathA = Arrays.copyOfRange(pathA, 0, pathA.length - 1);
        RequestSpecification reqS = new RequestSpecBuilder().setBaseUri(Endpoints.URI + Arrays.stream(pathA).collect(Collectors.joining("/")))
                .addHeader("X-RateLimit-Limit", xRateLimitLimit)
                .addHeader("X-RateLimit-Remaining", xRateLimitLimit)
                .addHeader("X-RateLimit-Reset", "1").build();
        sendGet("/" + path + appendix, reqS);
    }

    public void sendGet(String path, RequestSpecification requestSpecification) {
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .get(path)
                .then().assertThat().statusCode(200).extract().response();
    }

    @When("send GET with previously received user ID")
    public void sendGetWithPreviousUserId() {
        Reporter.log("Thread: " + Thread.currentThread().getId() + " execute 'send GET with previously received user ID' step. Accept type: " + acceptContentType, true);
        RequestSpecification requestSpecification = new RequestSpecBuilder().setBaseUri(Endpoints.URI + Endpoints.api + Endpoints.user)
                .addHeader("X-RateLimit-Limit", xRateLimitLimit)
                .addHeader("X-RateLimit-Remaining", xRateLimitLimit)
                .addHeader("X-RateLimit-Reset", "1").build();
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
        Reporter.log("Thread: " + Thread.currentThread().getId() + " execute 'send POST' step. Accept type: " + acceptContentType, true);
        ifUserNotPreserntSetUserWithRandomEmail();
        RequestSpecification requestSpecification = getDefaultRequestConfiguration();
        response = RestAssured.given()
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .post(Endpoints.user + appendix)
                .then().assertThat().statusCode(200).extract().response();
        writeDownId(response);
    }

    private RequestSpecification getDefaultRequestConfiguration() {
        return new RequestSpecBuilder().setBaseUri(Endpoints.URI + Endpoints.api)
                .addHeader("X-RateLimit-Limit", xRateLimitLimit)
                .addHeader("X-RateLimit-Remaining", xRateLimitLimit)
                .addHeader("X-RateLimit-Reset", "1").build();
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
        RequestSpecification requestSpecification = getDefaultRequestConfiguration();
        ifUserNotPreserntSetUserWithRandomEmail();
        response = RestAssured.given(requestSpecification).auth().oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .put(Endpoints.user + "/" + id + appendix)
                .then().assertThat().statusCode(200).extract().response();
    }

    private void ifUserNotPreserntSetUserWithRandomEmail() {
        if (user == null) {
            user = jdbcPoolTool.getUser("SELECT * FROM USERS WHERE name LIKE 'Tenali Ramakrishna'");
            user.setEmail("tenalis.ramakrishna@" + Math.random() + "ce.com");
        }
    }


    @When("send POST with illformated data")
    public void sendPostWithIllformatedData() {
        RequestSpecification requestSpecification = getDefaultRequestConfiguration();
        sendRequestWithIllformatedDataByMethodToPath("POST", Endpoints.user + appendix, requestSpecification);
    }

    //PUT or PATCH
    @When("send {string} with illformated data")
    public void sendPutWithIllformatedData(String method) {
        RequestSpecification requestSpecification = getDefaultRequestConfiguration();
        sendRequestWithIllformatedDataByMethodToPath(method, Endpoints.user + "/" + id + appendix, requestSpecification);
    }

    private void sendRequestWithIllformatedDataByMethodToPath(String method, String path, RequestSpecification requestSpecification) {
        Reporter.log("Thread: " + Thread.currentThread().getId() + " execute 'send" + method + "with illformated data' step. Accept type:" + acceptContentType);
        //mistyped gender
        user.setGender("malel");
        sendRequestWithTypeAndBody(method, path, requestSpecification);
        verifyThatStatusCodeIs(422, response);

        //mistyped status
        user.setGender("male");
        user.setStatus("activel");
        sendRequestWithTypeAndBody(method, path, requestSpecification);
        verifyThatStatusCodeIs(422, response);

        //mistyped email
        user.setStatus("active");
        user.setEmail("blabla.@a@" + Math.random() + "ce.com");
        sendRequestWithTypeAndBody(method, path, requestSpecification);
        verifyThatStatusCodeIs(422, response);
        user.setEmail("tenalis.ramakrishna@\" + Math.random() + \"ce.com");
        //empty name
        user.setName("");
        sendRequestWithTypeAndBody(method, path, requestSpecification);
        verifyThatStatusCodeIs(422, response);
    }

    private void sendRequestWithTypeAndBody(String method, String path, RequestSpecification requestSpecification) {
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .request(method, path)
                .then().assertThat().statusCode(200).extract().response();
    }

    @When("send DELETE with previously received user ID")
    public void sendDeleteWithPreviousUserId() {
        Reporter.log("Thread: " + Thread.currentThread().getId() + " execute 'send DELETE with previously received user ID' step. Accept type: " + acceptContentType, true);
        RequestSpecification requestSpecification = new RequestSpecBuilder().setBaseUri(Endpoints.URI + Endpoints.api + Endpoints.user)
                .addHeader("X-RateLimit-Limit", xRateLimitLimit)
                .addHeader("X-RateLimit-Remaining", xRateLimitLimit)
                .addHeader("X-RateLimit-Reset", "1")
                .build();
        String getPath = "/" + id + appendix;
        response = RestAssured.given(requestSpecification)
                .auth()
                .oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .delete(getPath)
                .then().assertThat().statusCode(200).extract().response();
    }

    @When("verify that operation response code is {int}")
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

    @Then("send PATCH")
    public void sendPATCH() {
        Reporter.log("Thread: " + Thread.currentThread().getId() + " execute 'send PATCH' step. Accept type: " + acceptContentType, true);
        ifUserNotPreserntSetUserWithRandomEmail();
        response = RestAssured.given().auth().oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body(user)
                .patch(Endpoints.user + "/" + id + appendix)
                .then().assertThat().statusCode(200).extract().response();
    }

    @Then("send {string} with corrupted body")
    public void sendPATCH(String method) {
        Reporter.log("Thread: " + Thread.currentThread().getId() + " execute 'send " + method + "' step. Accept type: " + acceptContentType, true);
        response = RestAssured.given().auth().oauth2(token)
                .accept(acceptContentType)
                .contentType(contentType)
                .body("{\"name\":\"Tenali Ramakrishna\", \"gender\":\"male\", \"email\":\"tenalis.ramakrishna@19ce.com\", \"status\":\"active\"")
                .patch(Endpoints.user + "/" + id + appendix)
                .then().assertThat().statusCode(200).extract().response();
    }

    @When("send GET for user with {string}")
    public void sendGetForUserWithNameTenali(String reqParametr) {
        String param = reqParametr.split("=")[0];
        String value = reqParametr.split("=")[1];
        RequestSpecification requestSpecification = new RequestSpecBuilder().setBaseUri(Endpoints.URI + Endpoints.api)
                .addHeader("X-RateLimit-Limit", xRateLimitLimit)
                .addHeader("X-RateLimit-Remaining", xRateLimitLimit)
                .addHeader("X-RateLimit-Reset", "1")
                .addQueryParam(param, value).build();
        sendGet(Endpoints.user + appendix, requestSpecification);
        String postfix = "";
        if (acceptContentType.toString().contains("xml")) {
            postfix = "datum.";
        }
        Assert.assertTrue(response.path(dataPrefix + "data." + postfix + param).toString().contains(value));
    }
}
