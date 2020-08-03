package com.codeminer42.trz.controllers;

import com.codeminer42.trz.dto.ReportInfoDTO;
import com.codeminer42.trz.services.SurvivorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "reports", description = "Operations for reports of the current data in the api")
public class ReportController {

    @Autowired
    private SurvivorService service;

    @Operation(summary = "Report the percentage of infected survivors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated percentage of infected survivors",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReportInfoDTO.class))})})
    @GetMapping("/infected")
    public ResponseEntity<ReportInfoDTO<Double>> percentageOfInfectedSurvivors() {
        Double percentage = service.getPercentageOfInfected();
        ReportInfoDTO<Double> report = new ReportInfoDTO<>("Percentage of infected survivors", percentage);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Report the percentage of non-infected survivors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated percentage of non-infected survivors",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReportInfoDTO.class))})})
    @GetMapping("/non-infected")
    public ResponseEntity<ReportInfoDTO<Double>> percentageOfNonInfectedSurvivors() {
        Double percentage = service.getPercentageOfNonInfected();
        ReportInfoDTO<Double> report = new ReportInfoDTO<>("Percentage of non-infected survivors", percentage);
        return ResponseEntity.ok(report);
    }
}
