package org.baeldung.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.baeldung.persistence.model.Foo;
import org.junit.Test;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class LiveTest {

    private final FormAuthConfig formAuthConfig = new FormAuthConfig("http://localhost:8082/spring-security-mvc-boot/login", "username", "password");

    @Test
    public void givenUserWithReadPrivilegeAndHasPermission_whenGetFooById_thenOK() {
        final Response response = givenAuth("john", "123").get("http://localhost:8082/spring-security-mvc-boot/foos/1");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("id"));
    }

    @Test
    public void givenUserWithNoWritePrivilegeAndHasPermission_whenPostFoo_thenForbidden() {
        final Response response = givenAuth("john", "123").contentType(MediaType.APPLICATION_JSON_VALUE).body(new Foo("sample")).post("http://localhost:8082/spring-security-mvc-boot/foos");
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void givenUserWithWritePrivilegeAndHasPermission_whenPostFoo_thenOk() {
        final Response response = givenAuth("tom", "111").contentType(MediaType.APPLICATION_JSON_VALUE).body(new Foo("sample")).post("http://localhost:8082/spring-security-mvc-boot/foos");
        assertEquals(201, response.getStatusCode());
        assertTrue(response.asString().contains("id"));
    }

    //

    @Test
    public void givenUserMemberInOrganization_whenGetOrganization_thenOK() {
        final Response response = givenAuth("john", "123").get("http://localhost:8082/spring-security-mvc-boot/organizations/1");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.asString().contains("id"));
    }

    @Test
    public void givenUserMemberNotInOrganization_whenGetOrganization_thenForbidden() {
        final Response response = givenAuth("john", "123").get("http://localhost:8082/spring-security-mvc-boot/organizations/2");
        assertEquals(403, response.getStatusCode());
    }

    //

    @Test
    public void givenDisabledSecurityExpression_whenGetFooByName_thenError() {
        final Response response = givenAuth("john", "123").get("http://localhost:8082/spring-security-mvc-boot/foos?name=sample");
        assertEquals(500, response.getStatusCode());
        assertTrue(response.asString().contains("method hasAuthority() not allowed"));
    }

    //
    private RequestSpecification givenAuth(String username, String password) {
        return RestAssured.given().auth().form(username, password, formAuthConfig);
    }
}