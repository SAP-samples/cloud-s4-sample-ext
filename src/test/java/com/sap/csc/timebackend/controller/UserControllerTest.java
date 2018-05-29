package com.sap.csc.timebackend.controller;

import com.sap.csc.timebackend.security.SAPUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    private static final String HTTP_LOCALHOST = "http://localhost:";
    private static final String TEST_USER = "testUser";
    private static final String USER = "/user";
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    @LocalServerPort
    private int port;

    @Test
    public void getAuthUser() {
        assertEquals(TEST_USER, restTemplate.getForObject(HTTP_LOCALHOST + port + USER, SAPUser.class).getId());
    }
}