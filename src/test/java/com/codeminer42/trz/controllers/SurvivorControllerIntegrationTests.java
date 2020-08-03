package com.codeminer42.trz.controllers;

import com.codeminer42.trz.TheResidentZombieApplication;
import com.codeminer42.trz.dto.InventoryEntryDTO;
import com.codeminer42.trz.dto.LocationDTO;
import com.codeminer42.trz.dto.SurvivorRequestDTO;
import com.codeminer42.trz.models.Survivor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {TheResidentZombieApplication.class})
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class SurvivorControllerIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long survivorId = 1L;

    @Test
    @Order(1)
    void whenNotExists_thenStatusIsNotFound() throws Exception {
        mvc.perform(get("/survivors/" + survivorId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    void whenSave_thenStatusCodeIsCreatedAndResponseBodyIsSavedSurvivor() throws Exception {
        SurvivorRequestDTO survivor = new SurvivorRequestDTO();
        survivor.setName("Lucas Nascimento");
        survivor.setAge(20);
        survivor.setLocation(LocationDTO.builder().longitude(-21.2123).latitude(2.3231).build());
        survivor.setGender(Survivor.Gender.MALE);
        InventoryEntryDTO entry = InventoryEntryDTO.builder().itemId(1L).amount(4).build();
        survivor.setInventory(Collections.singleton(entry));
        String survivorJson = objectMapper.writeValueAsString(survivor);

        String responseJson = mvc.perform(post("/survivors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(survivorJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.inventory").exists())
                .andExpect(jsonPath("$.name").value(survivor.getName()))
                .andExpect(jsonPath("$.age").value(survivor.getAge()))
                .andReturn().getResponse().getContentAsString();
        Map<String, Object> map = objectMapper.readValue(responseJson, HashMap.class);
        this.survivorId = Long.parseLong(map.get("id").toString());
    }

    @Test
    @Order(3)
    void onceSaved_thenCanBeFoundThusStatusCodeIsOk() throws Exception {
        mvc.perform(get("/survivors/" + survivorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(survivorId));
    }
}
