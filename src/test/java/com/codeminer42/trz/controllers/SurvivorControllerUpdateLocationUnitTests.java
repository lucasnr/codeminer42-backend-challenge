package com.codeminer42.trz.controllers;

import com.codeminer42.trz.dto.LocationDTO;
import com.codeminer42.trz.models.InventoryEntry;
import com.codeminer42.trz.models.Item;
import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.services.SurvivorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {SurvivorController.class})
@TestInstance(Lifecycle.PER_CLASS)
public class SurvivorControllerUpdateLocationUnitTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SurvivorService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Survivor survivor;
    private LocationDTO location;

    @BeforeAll
    void setUp() {
        Long survivorId = 1L;
        InventoryEntry entry = InventoryEntry.builder()
                .amount(4)
                .item(Item.builder().id(1L).name("Fiji Water").points(10).build())
                .build();
        entry.setSurvivorId(survivorId);

        Survivor survivor = Survivor.builder()
                .id(survivorId)
                .name("Ulfric Stormcloack")
                .age(32)
                .gender(Survivor.Gender.MALE)
                .latitude(-3.4324)
                .longitude(-5.4956)
                .inventory(Collections.singleton(entry))
                .build();
        survivor.setReports(Collections.emptySet());
        this.survivor = survivor;

        this.location = LocationDTO.builder()
                .latitude(-2.2134)
                .longitude(5.1234)
                .build();
    }

    @BeforeEach
    void onStart() {
        when(service.findByIdOrThrowNotFoundException(survivor.getId())).thenReturn(this.survivor);
    }

    private MockHttpServletRequestBuilder buildRequest() {
        return put("/survivors/" + survivor.getId())
                .contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void whenAcceptIsDefinedButIsNotApplicationSlashJson_thenStatusCodeIsNotAcceptable() throws Exception {
        mvc.perform(buildRequest()
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void whenContentTypeIsNotApplicationSlashJson_thenStatusCodeIsUnsupportedMediaType() throws Exception {
        mvc.perform(buildRequest()
                .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void whenContentIsNotDefined_thenStatusCodeIsBadRequest() throws Exception {
        mvc.perform(buildRequest())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenContentIsNotValidJson_thenStatusCodeIsBadRequest() throws Exception {
        mvc.perform(buildRequest()
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenContentIsMissingFields_thenStatusCodeIsBadRequest() throws Exception {
        mvc.perform(buildRequest()
                .content("{}"))
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString(), containsString("latitude")))
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString(), containsString("longitude")));
    }

    @Test
    void whenUpdate_thenStatusCodeIsOk() throws Exception {
        String locationJson = objectMapper.writeValueAsString(this.location);
        mvc.perform(buildRequest()
                .content(locationJson))
                .andExpect(status().isOk());
    }

    @Test
    void whenUpdate_thenContentTypeIsApplicationSlashJson() throws Exception {
        String locationJson = objectMapper.writeValueAsString(this.location);
        mvc.perform(buildRequest()
                .content(locationJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void whenUpdate_thenResponseBodyContainsUpdatedLocation() throws Exception {
        String locationJson = objectMapper.writeValueAsString(this.location);
        mvc.perform(buildRequest()
                .content(locationJson))
                .andExpect(jsonPath("$.location").exists())
                .andExpect(jsonPath("$.location.latitude").value(this.location.getLatitude()))
                .andExpect(jsonPath("$.location.longitude").value(this.location.getLongitude()));
    }
}

