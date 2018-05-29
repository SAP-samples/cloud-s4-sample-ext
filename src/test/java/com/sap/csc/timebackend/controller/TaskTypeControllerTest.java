package com.sap.csc.timebackend.controller;

import com.sap.csc.timebackend.model.TaskType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
@ActiveProfiles("test")

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskTypeControllerTest {

    private static final String HTTP_LOCALHOST = "http://localhost:";
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    @LocalServerPort
    private int port;

    @Test
    public void taskTypeTest() {
        Map[] b = restTemplate.getForObject(HTTP_LOCALHOST + port + "/tasktype", Map[].class);
        List<TaskType> tasks = new ArrayList<>(2);
        for (Map map : b) {
            tasks.add(TaskType.valueOf((String) map.get("abbr")));
        }

        assertTrue(Arrays.asList(TaskType.values()).containsAll(tasks));
    }

}

