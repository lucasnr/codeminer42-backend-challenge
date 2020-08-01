package com.codeminer42.trz.controllers;

import com.codeminer42.trz.dto.SurvivorResponseDTO;
import com.codeminer42.trz.exceptions.NotFoundException;
import com.codeminer42.trz.models.InventoryEntry;
import com.codeminer42.trz.models.Item;
import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.repositories.SurvivorRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {SurvivorController.class})
@TestInstance(Lifecycle.PER_CLASS)
public class SurvivorControllerFindByIdUnitTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SurvivorService service;

    @MockBean
    private SurvivorRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long survivorId;
    private Survivor survivor;

    @BeforeAll
    void setUp() {
        this.survivorId = 1L;
        InventoryEntry entry = InventoryEntry.builder()
                .amount(4)
                .item(Item.builder().id(1L).name("Fiji Water").points(10).build())
                .build();
        entry.setSurvivorId(survivorId);

        Survivor survivor = Survivor.builder()
                .id(survivorId)
                .name("Maria Isabel")
                .age(19)
                .gender(Survivor.Gender.FEMALE)
                .latitude(-3.4324)
                .longitude(-5.4956)
                .inventory(Collections.singleton(entry))
                .build();
        survivor.setReports(Collections.emptySet());
        this.survivor = survivor;

        doCallRealMethod().when(service).setRepository(any());
        service.setRepository(repository);
    }

    @BeforeEach
    void onStart() {
        when(service.findByIdOrThrowNotFoundException(this.survivorId)).thenReturn(this.survivor);
    }

    @Test
    void whenAcceptIsDefinedButIsNotApplicationSlashJson_thenStatusCodeIsNotAcceptable() throws Exception {
        mvc.perform(get("/survivors/" + this.survivorId)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void whenIdIsNotValidIntegerValue_thenStatusCodeIsBadRequest() throws Exception {
        mvc.perform(get("/survivors/invalidInteger"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenIdExists_thenStatusCodeIsOk() throws Exception {
        mvc.perform(get("/survivors/" + this.survivorId))
                .andExpect(status().isOk());
    }

    @Test
    void whenIdExists_thenContentTypeIsApplicationSlashJson() throws Exception {
        mvc.perform(get("/survivors/" + this.survivorId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void whenIdExists_thenResponseBodyIsFoundSurvivor() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(new SurvivorResponseDTO(this.survivor));
        mvc.perform(get("/survivors/" + this.survivorId))
                .andExpect(content().json(expectedJson));
    }

    @Test
    void whenIdDoesNotExists_thenStatusCodeIsNotFound() throws Exception {
        Long nonexistentId = this.survivorId + 1;

        when(repository.findById(nonexistentId)).thenReturn(Optional.empty());
        when(service.findByIdOrThrowNotFoundException(nonexistentId)).thenCallRealMethod();

        mvc.perform(get("/survivors/" + nonexistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenIdDoesNotExists_thenThrowsNotFoundException() throws Exception {
        Long nonexistentId = this.survivorId + 1;

        when(repository.findById(nonexistentId)).thenReturn(Optional.empty());
        when(service.findByIdOrThrowNotFoundException(nonexistentId)).thenCallRealMethod();

        mvc.perform(get("/survivors/" + nonexistentId))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }
}

