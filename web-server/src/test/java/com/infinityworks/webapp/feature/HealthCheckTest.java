package com.infinityworks.webapp.feature;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthCheckTest extends WebApplicationTest {

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .build();
    }

    @Test
    public void returnsTheHealthStatus() throws Exception {
        String healthEndpoint = "/health";

        ResultActions response = mockMvc.perform(get(healthEndpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("status", is("UP")));
    }
}
