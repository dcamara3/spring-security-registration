package org.baeldung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ustn.userprofile.UserAccount;
import com.ustn.userprofile.manager.UserManager;
import org.baeldung.Application;
import org.baeldung.spring.TestDbConfig;
import org.baeldung.spring.TestIntegrationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class, TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class GetLoggedUsersIntegrationTest {

    @Autowired
    private UserManager userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${local.server.port}")
    int port;

    private FormAuthConfig formConfig;
    private String LOGGED_USERS_URL, SESSION_REGISTRY_LOGGED_USERS_URL;

    //

    @Before
    public void init() {
        UserAccount user = userRepository.findByEmail("test@test.com");
        if (user == null) {
            user = new UserAccount();
            user.setName("Test");
            user.setLogin("Test");
            user.setPassword(passwordEncoder.encode("test"));
            user.setEmail("test@test.com");
            user.setActive(true);
            userRepository.insertUserAccount(user, new ArrayList<>());
        } else {
            user.setPassword(passwordEncoder.encode("test"));
            userRepository.insertUserAccount(user, new ArrayList<>());
        }

        RestAssured.port = port;

        String URL_PREFIX = "http://localhost:" + String.valueOf(port);
        LOGGED_USERS_URL = URL_PREFIX + "/loggedUsers";
        SESSION_REGISTRY_LOGGED_USERS_URL = URL_PREFIX + "/loggedUsersFromSessionRegistry";
        formConfig = new FormAuthConfig(URL_PREFIX + "/login", "username", "password");
    }

    @Test
    public void givenLoggedInUser_whenGettingLoggedUsersFromActiveUserStore_thenResponseContainsUser() {
        final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

        final Map<String, String> params = new HashMap<String, String>();
        params.put("password", "test");

        final Response response = request.with().params(params).get(LOGGED_USERS_URL);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().asString().contains("test@test.com"));
    }

    @Test
    public void givenLoggedInUser_whenGettingLoggedUsersFromSessionRegistry_thenResponseContainsUser() {
        final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

        final Map<String, String> params = new HashMap<String, String>();
        params.put("password", "test");

        final Response response = request.with().params(params).get(SESSION_REGISTRY_LOGGED_USERS_URL);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().asString().contains("test@test.com"));
    }

}
