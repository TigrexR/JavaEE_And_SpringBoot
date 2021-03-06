package com.example.spring.boot.actuator.security;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Author: 王俊超
 * Date: 2018-01-12 07:45
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("cors")
public class CorsSampleActuatorApplicationTests {

    private TestRestTemplate testRestTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        LocalHostUriTemplateHandler handler = new LocalHostUriTemplateHandler(
                this.applicationContext.getEnvironment(), "http");
        restTemplate.setUriTemplateHandler(handler);
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        this.testRestTemplate = new TestRestTemplate(restTemplate);
    }

    @Test
    public void endpointShouldReturnUnauthorized() {
        ResponseEntity<?> entity = this.testRestTemplate.getForEntity("/actuator/env",
                Map.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        printObject(entity);
    }

    @Test
    public void preflightRequestToEndpointShouldReturnOk() throws Exception {
        RequestEntity<?> healthRequest = RequestEntity.options(new URI("/actuator/env"))
                .header("Origin", "http://localhost:8080")
                .header("Access-Control-Request-Method", "GET").build();
        ResponseEntity<?> exchange = this.testRestTemplate.exchange(healthRequest,
                Map.class);

        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);

        printObject(exchange);
    }


    @Test
    public void preflightRequestWhenCorsConfigInvalidShouldReturnForbidden()
            throws Exception {
        RequestEntity<?> entity = RequestEntity.options(new URI("/actuator/env"))
                .header("Origin", "http://localhost:9095")
                .header("Access-Control-Request-Method", "GET").build();
        ResponseEntity<byte[]> exchange = this.testRestTemplate.exchange(entity,
                byte[].class);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        printObject(exchange);
    }


    /////////////////////
    // 工具方法
    /////////////////////
    private void printObject(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String value = null;
        try {
            value = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(value);
    }


}
