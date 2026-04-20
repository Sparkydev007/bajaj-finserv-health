package com.example.bajaj;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BajajApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BajajApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        // STEP 1: Generate Webhook
        String url1 = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> request = new HashMap<>();
        request.put("name", "John Doe");
        request.put("regNo", "ADT23SOCB0784");
        request.put("email", "prathmeshbunde@gmail.com");

        Map response = restTemplate.postForObject(url1, request, Map.class);

        String webhookUrl = (String) response.get("webhook");
        String accessToken = (String) response.get("accessToken");

        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        // STEP 2: FINAL SQL QUERY
        String sql =
                "SELECT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME, " +
                "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
                "FROM EMPLOYEE e " +
                "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                "LEFT JOIN EMPLOYEE e2 ON e.DEPARTMENT = e2.DEPARTMENT AND e2.DOB > e.DOB " +
                "GROUP BY e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME " +
                "ORDER BY e.EMP_ID DESC";

        // STEP 3: SEND ANSWER TO WEBHOOK
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);

        String body = "{ \"finalQuery\": \"" + sql + "\" }";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        String result = restTemplate.postForObject(webhookUrl, entity, String.class);

        System.out.println("FINAL RESPONSE: " + result);
    }
}