package com.example.flag.controllers;

import com.example.flag.services.CSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.BDDMockito.given;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CSVControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private CSVService csvService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testProcessCSV() throws Exception {
        String csvData = "user_id,message\n1,Hello\n2,Hi";
        String expectedResponse = "user_id,total_messages,avg_score\n1,1,0.50\n2,1,0.50\n";

        given(csvService.parseCSV(anyString())).willReturn(expectedResponse);

        mockMvc.perform(post("/csv")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(csvData))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }
}

