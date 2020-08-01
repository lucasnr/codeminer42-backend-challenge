package com.codeminer42.trz.controllers;

import com.codeminer42.trz.dto.InventoryEntryDTO;
import com.codeminer42.trz.dto.LocationDTO;
import com.codeminer42.trz.dto.SurvivorRequestDTO;
import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.models.Survivor.Gender;
import com.codeminer42.trz.services.SurvivorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {SurvivorController.class})
@TestInstance(Lifecycle.PER_CLASS)
public class SurvivorControllerOnSaveUnitTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SurvivorService service;

    @Autowired
    private ObjectMapper objectMapper;

    private SurvivorRequestDTO survivor;

    @BeforeAll
    void setUp() {
        SurvivorRequestDTO survivor = new SurvivorRequestDTO();

        survivor.setName("Lucas Nascimento");
        survivor.setAge(20);
        survivor.setGender(Gender.MALE);
        survivor.setLocation(LocationDTO.builder()
                .latitude(-4.30494)
                .longitude(2.12412)
                .build());

        survivor.setInventory(Collections.singleton(InventoryEntryDTO.builder()
                .itemId(1l)
                .amount(4)
                .build()));

        this.survivor = survivor;
    }

    private MockHttpServletRequestBuilder buildRequest() {
        return post("/survivors");
    }

    @Test
    void whenContentTypeIsNotDefined_thenStatusCodeIsUnsupportedMediaType() throws Exception {
        mvc.perform(buildRequest())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void whenContentTypeIsNotApplicationSlashJson_thenStatusCodeIsUnsupportedMediaType() throws Exception {
        mvc.perform(buildRequest()
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void whenContentIsNotDefined_thenStatusCodeIsBadRequest() throws Exception {
        mvc.perform(buildRequest()
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenContentIsNotValidJson_thenStatusCodeIsBadRequest() throws Exception {
        mvc.perform(buildRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenAcceptIsDefinedButIsNotApplicationSlashJson_thenStatusCodeIsNotAcceptable() throws Exception {
        mvc.perform(buildRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .content(""))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void whenContentIsNotValidSurvivor_thenStatusCodeIsBadRequestAndReturnMissingFields() throws Exception {
        MvcResult result = mvc.perform(buildRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andReturn();

        String[] missingFields = {"name", "age", "gender", "location", "inventory"};
        for (String missingField : missingFields)
            assertTrue(result.getResponse().getContentAsString().contains(missingField));
    }

    @Test
    void whenSave_thenStatusCodeIsCreated() throws Exception {
        String survivorJson = objectMapper.writeValueAsString(this.survivor);
        mvc.perform(buildRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .content(survivorJson))
                .andExpect(status().isCreated());
    }

    @Test
    void whenSave_thenLocationHeaderIsPresent() throws Exception {
        String survivorJson = objectMapper.writeValueAsString(this.survivor);
        mvc.perform(buildRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .content(survivorJson))
                .andExpect(header().exists("Location"));
    }

    @Test
    void whenSave_thenResponseBodyIsSurvivor() throws Exception {
        final Long generatedId = 1l;

        doAnswer(invocation -> {
            Survivor survivor = invocation.getArgument(0, Survivor.class);
            survivor.setId(generatedId);
            return null;
        }).when(service).save(any(Survivor.class));

        String survivorJson = objectMapper.writeValueAsString(this.survivor);
        mvc.perform(buildRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .content(survivorJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(generatedId))
                .andExpect(jsonPath("$.name").value(this.survivor.getName()))
                .andExpect(jsonPath("$.gender").value(this.survivor.getGender().name()))
                .andExpect(jsonPath("$.age").value(this.survivor.getAge()))
                .andExpect(jsonPath("$.inventory").exists())
                .andExpect(jsonPath("$.inventory").isArray());
    }
}
