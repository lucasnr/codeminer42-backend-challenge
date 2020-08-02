package com.codeminer42.trz.controllers;

import com.codeminer42.trz.dto.ReportDTO;
import com.codeminer42.trz.models.InventoryEntry;
import com.codeminer42.trz.models.Item;
import com.codeminer42.trz.models.Report;
import com.codeminer42.trz.models.ReportId;
import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.services.SurvivorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {SurvivorController.class})
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class SurvivorControllerReportInfectedUnitTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SurvivorService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Survivor survivor;

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
                .name("Hermaeus Mora")
                .age(666)
                .gender(Survivor.Gender.OTHER)
                .latitude(-3.4324)
                .longitude(-5.4956)
                .inventory(Collections.singleton(entry))
                .build();

        Set<Report> reports = new HashSet<>();
        for (int i = 1; i <= 4; i++) {
            Report report = new Report();
            report.setId(new ReportId(1L, i + 1L));
            reports.add(report);
        }
        survivor.setReports(reports);

        this.survivor = survivor;
    }

    @BeforeEach
    void onStart() {
        when(service.findByIdOrThrowNotFoundException(survivor.getId())).thenReturn(survivor);
    }

    private MockHttpServletRequestBuilder buildRequest() {
        return post(String.format("/survivors/%d/report", survivor.getId()))
                .contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @Order(1)
    void whenSurvivorHasLassThanFiveReports_thenInfectedIsFalse() throws Exception {
        assertThat(survivor.getReports().size(), is(lessThan(5)));
        mvc.perform(get("/survivors/" + survivor.getId()))
                .andExpect(jsonPath("$.infected").value(false));
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
    void whenContentIsNotValidJson_thenStatusCodeIsBadRequest() throws Exception {
        mvc.perform(buildRequest()
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenContentIsMissingField_thenStatusCodeIsBadRequestAndResponseBodyContainsMissingField() throws Exception {
        mvc.perform(buildRequest()
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString(),
                        containsString("reporter_id")));
    }

    @Test
    void whenReporterIsReported_thenStatusIsBadRequest() throws Exception {
        ReportDTO report = new ReportDTO();
        report.setReporterId(survivor.getId());
        String reportJson = objectMapper.writeValueAsString(report);

        doCallRealMethod().when(service).reportAsInfected(any(), any());

        mvc.perform(buildRequest()
                .content(reportJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void whenReport_thenStatusCodeIsNoContentAndReportsAreIncreased() throws Exception {
        Long reporterId = survivor.getReports().size() + 2L;
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setReporterId(reporterId);
        String reportJson = objectMapper.writeValueAsString(reportDTO);

        doAnswer(invocation -> {
            Report report = new Report(new ReportId(survivor.getId(), reporterId));
            survivor.getReports().add(report);
            return null;
        }).when(service).reportAsInfected(survivor.getId(), reporterId);

        Integer size = survivor.getReports().size();

        mvc.perform(buildRequest()
                .content(reportJson))
                .andExpect(status().isNoContent());

        assertThat(survivor.getReports().size(), is(size + 1));
    }

    @Test
    @Order(3)
    void whenSurvivorHasFiveOrMoreReports_thenInfectedIsTrue() throws Exception {
        assertThat(survivor.getReports().size(), is(greaterThanOrEqualTo(5)));
        mvc.perform(get("/survivors/" + survivor.getId()))
                .andExpect(jsonPath("$.infected").value(true));
    }
}
