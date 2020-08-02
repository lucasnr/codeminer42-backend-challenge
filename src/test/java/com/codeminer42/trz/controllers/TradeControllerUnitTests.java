package com.codeminer42.trz.controllers;

import com.codeminer42.trz.dto.ProposalEntryDTO;
import com.codeminer42.trz.dto.TradeDTO;
import com.codeminer42.trz.dto.TradeSideDTO;
import com.codeminer42.trz.exceptions.BadRequestException;
import com.codeminer42.trz.models.InventoryEntry;
import com.codeminer42.trz.models.Item;
import com.codeminer42.trz.models.Report;
import com.codeminer42.trz.models.ReportId;
import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.repositories.ItemRepository;
import com.codeminer42.trz.services.SurvivorService;
import com.codeminer42.trz.services.TradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.ir.FunctionNode;
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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TradeController.class})
@TestInstance(Lifecycle.PER_CLASS)
public class TradeControllerUnitTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TradeService service;

    @MockBean
    private SurvivorService survivorService;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Survivor infectedSurvivor;
    private Survivor leftSurvivor;
    private Survivor rightSurvivor;
    private Item fijiWater;
    private Item firstAid;

    @BeforeAll
    void setUp() {
        doCallRealMethod().when(service).setItemRepository(itemRepository);
        service.setItemRepository(itemRepository);

        Survivor.SurvivorBuilder builder = Survivor.builder()
                .age(20)
                .gender(Survivor.Gender.MALE)
                .longitude(-1.12442)
                .latitude(3.2314);

        this.fijiWater = Item.builder()
                .id(1L)
                .name("Fiji Water")
                .points(14)
                .build();
        this.firstAid = Item.builder()
                .id(2L)
                .name("First Aid Pouch")
                .points(10)
                .build();

        Set<InventoryEntry> leftInventory =
                Collections.singleton(InventoryEntry.builder().amount(5).item(fijiWater).build());
        this.leftSurvivor = builder
                .id(1L)
                .name("Left Survivor")
                .inventory(leftInventory)
                .build();

        Set<InventoryEntry> rightInventory =
                Collections.singleton(InventoryEntry.builder().amount(7).item(firstAid).build());
        this.rightSurvivor = builder
                .id(2L)
                .name("Right Survivor")
                .inventory(rightInventory)
                .build();

        this.infectedSurvivor = builder
                .id(10L)
                .name("Infected Survivor")
                .inventory(leftInventory)
                .build();
        Set<Report> reports = new HashSet<>();
        for (int i = 0; i < 5; i++)
            reports.add(new Report(new ReportId(infectedSurvivor.getId(), i + 1L)));
        infectedSurvivor.setReports(reports);
    }

    @BeforeEach
    void onStart() {
        when(survivorService.findByIdOrThrowNotFoundException(leftSurvivor.getId())).thenReturn(leftSurvivor);
        when(survivorService.findByIdOrThrowNotFoundException(rightSurvivor.getId())).thenReturn(rightSurvivor);
        when(survivorService.findByIdOrThrowNotFoundException(infectedSurvivor.getId())).thenReturn(infectedSurvivor);

        when(itemRepository.findById(fijiWater.getId())).thenReturn(Optional.of(fijiWater));
        when(itemRepository.findById(firstAid.getId())).thenReturn(Optional.of(firstAid));

        doCallRealMethod().when(service).trade(any(), any(), any(), any());
        doCallRealMethod().when(service).assertThatSurvivorsCanTrade(any(), any());
    }

    private MockHttpServletRequestBuilder buildRequest() {
        return post("/trades")
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
    void whenContentIsNotValidTrade_thenStatusCodeIsBadRequestAndReturnMissingFields() throws Exception {
        String responseJson = mvc.perform(buildRequest()
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andReturn().getResponse().getContentAsString();

        String[] missingFields = {"left", "right"};
        for (String missingField : missingFields)
            assertThat(responseJson, containsString(missingField));
    }

    @Test
    void whenContentHasNotValidTradeSide_thenStatusCodeIsBadRequestAndReturnMissingFields() throws Exception {
        String responseJson = mvc.perform(buildRequest()
                .content("{\"left\": {}, \"right\": {}}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andReturn().getResponse().getContentAsString();

        String[] missingFields = {"survivor_id", "items"};
        for (String missingField : missingFields)
            assertThat(responseJson, containsString(missingField));
    }

    @Test
    void whenBothSidesAreOfTheSameSurvivor_thenStatusCodeIsBadRequest() throws Exception {
        ProposalEntryDTO entry = ProposalEntryDTO.builder()
                .amount(5)
                .itemId(fijiWater.getId())
                .build();
        TradeSideDTO tradeSide = TradeSideDTO.builder()
                .survivorId(leftSurvivor.getId())
                .items(Collections.singleton(entry)).build();
        TradeDTO trade = TradeDTO.builder()
                .left(tradeSide)
                .right(tradeSide)
                .build();
        String tradeJson = objectMapper.writeValueAsString(trade);

        mvc.perform(buildRequest()
                .content(tradeJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException));
    }

    @Test
    void whenOneOfTheSurvivorsIsInfected_thenStatusCodeIsBadRequest() throws Exception {
        TradeSideDTO leftSide = TradeSideDTO.builder()
                .survivorId(leftSurvivor.getId())
                .items(Collections.singleton(ProposalEntryDTO.builder()
                        .amount(5)
                        .itemId(fijiWater.getId())
                        .build()))
                .build();

        TradeSideDTO rightSide = TradeSideDTO.builder()
                .survivorId(infectedSurvivor.getId())
                .items(Collections.singleton(ProposalEntryDTO.builder()
                        .amount(5)
                        .itemId(fijiWater.getId())
                        .build()))
                .build();
        TradeDTO trade = TradeDTO.builder()
                .left(leftSide)
                .right(rightSide)
                .build();
        String tradeJson = objectMapper.writeValueAsString(trade);

        mvc.perform(buildRequest()
                .content(tradeJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage(),
                        containsStringIgnoringCase("infected")));
    }

    @Test
    void whenBothSidesAreNotEqualInPoints_thenStatusCodeIsBadRequest() throws Exception {
        Integer leftSideAmount = 5;
        Item leftSideItem = fijiWater;
        TradeSideDTO leftSide = TradeSideDTO.builder()
                .survivorId(leftSurvivor.getId())
                .items(Collections.singleton(ProposalEntryDTO.builder()
                        .amount(leftSideAmount)
                        .itemId(leftSideItem.getId())
                        .build()))
                .build();

        Integer rightSideAmount = 6;
        Item rightSideItem = firstAid;
        TradeSideDTO rightSide = TradeSideDTO.builder()
                .survivorId(rightSurvivor.getId())
                .items(Collections.singleton(ProposalEntryDTO.builder()
                        .amount(rightSideAmount)
                        .itemId(rightSideItem.getId())
                        .build()))
                .build();

        TradeDTO trade = TradeDTO.builder()
                .left(leftSide)
                .right(rightSide)
                .build();
        String tradeJson = objectMapper.writeValueAsString(trade);

        assertThat(rightSideAmount * rightSideItem.getPoints(),
                is(not(leftSideAmount * leftSideItem.getPoints())));

        doCallRealMethod().when(service).checkSurvivorInventory(any(), any());
        doCallRealMethod().when(service).assertThatBothSidesEqualsTheSameAmountOfPoints(any(), any());
        mvc.perform(buildRequest()
                .content(tradeJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage(),
                        containsStringIgnoringCase("points")));
    }

    @Test
    void whenTrade_thenStatusCodeIsNoContent() throws Exception {
        Integer leftSideAmount = 5;
        Item leftSideItem = fijiWater;
        TradeSideDTO leftSide = TradeSideDTO.builder()
                .survivorId(leftSurvivor.getId())
                .items(Collections.singleton(ProposalEntryDTO.builder()
                        .amount(leftSideAmount)
                        .itemId(leftSideItem.getId())
                        .build()))
                .build();

        Integer rightSideAmount = 7;
        Item rightSideItem = firstAid;
        TradeSideDTO rightSide = TradeSideDTO.builder()
                .survivorId(rightSurvivor.getId())
                .items(Collections.singleton(ProposalEntryDTO.builder()
                        .amount(rightSideAmount)
                        .itemId(rightSideItem.getId())
                        .build()))
                .build();

        TradeDTO trade = TradeDTO.builder()
                .left(leftSide)
                .right(rightSide)
                .build();
        String tradeJson = objectMapper.writeValueAsString(trade);

        mvc.perform(buildRequest()
                .content(tradeJson))
                .andExpect(status().isNoContent());
    }
}
