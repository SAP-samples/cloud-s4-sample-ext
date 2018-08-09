package com.sap.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.model.ExportWorkforceContent;
import com.sap.services.ExportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ExportControllerTest {

    private static final String EXPORT = "/export";
    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    ExportService exportService;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    Environment environment;


    @Test
    @DisplayName("EmptyListOfIds")
    void emptyIdListResultBadRequest() throws Exception {
        mockMvc.perform(post(EXPORT)
                .content(mapper.writeValueAsString(ExportWorkforceContent.create(Collections.emptyList(),
                        "", "pdf")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("UnsupportedFormat")
    void unsupportedFormatResultBadRequest() throws Exception {
        mockMvc.perform(post(EXPORT)
                .content(mapper.writeValueAsString(ExportWorkforceContent.create(Collections.singletonList("123"),
                        "", "txt")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("NullSignature")
    void nullSignatureResultsBadRequest() throws Exception {
        mockMvc.perform(post(EXPORT)
                .content(mapper.writeValueAsString(ExportWorkforceContent
                        .create(Collections.singletonList("123"), null, "txt")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}