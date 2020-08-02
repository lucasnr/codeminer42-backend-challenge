package com.codeminer42.trz.controllers;

import com.codeminer42.trz.dto.LocationDTO;
import com.codeminer42.trz.dto.ReportDTO;
import com.codeminer42.trz.dto.SurvivorRequestDTO;
import com.codeminer42.trz.dto.SurvivorResponseDTO;
import com.codeminer42.trz.models.Survivor;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(path = "/survivors", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "survivors", description = "Operations with the survivors")
public class SurvivorController {

    @Autowired
    private SurvivorService service;

    @Operation(summary = "Get a survivor by the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the survivor",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SurvivorResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id provided", content = @Content),
            @ApiResponse(responseCode = "404", description = "Survivor not found", content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<SurvivorResponseDTO> findById(@PathVariable("id") Long id) {
        Survivor survivor = service.findByIdOrThrowNotFoundException(id);
        return ResponseEntity.ok(new SurvivorResponseDTO(survivor));
    }

    @Operation(summary = "Register a survivor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered the survivor",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SurvivorResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid survivor provided", content = @Content)})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<SurvivorResponseDTO> save(@RequestBody @Valid SurvivorRequestDTO survivorDTO,
                                                    UriComponentsBuilder uriBuilder) {
        Survivor survivor = survivorDTO.toModel();
        service.save(survivor);

        URI location = uriBuilder.path("/survivors/{id}")
                .buildAndExpand(survivor.getId()).toUri();
        return ResponseEntity.created(location).body(new SurvivorResponseDTO(survivor));
    }

    @Operation(summary = "Update a survivor location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the survivor location",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SurvivorResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id or location provided", content = @Content),
            @ApiResponse(responseCode = "404", description = "Survivor not found", content = @Content)})
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<SurvivorResponseDTO> updateLocation(@PathVariable("id") Long id,
                                                              @RequestBody @Valid LocationDTO location) {
        Survivor survivor = service.findByIdOrThrowNotFoundException(id);
        survivor.setLatitude(location.getLatitude());
        survivor.setLongitude(location.getLongitude());
        return ResponseEntity.ok(new SurvivorResponseDTO(survivor));
    }

    @Operation(summary = "Report a survivor as infected")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully reported the survivor", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id or reporter provided", content = @Content),
            @ApiResponse(responseCode = "404", description = "Survivor not found", content = @Content)})
    @PostMapping(path = "/{id}/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> reportAsInfected(@PathVariable("id") Long id, @RequestBody @Valid ReportDTO report) {
        service.assertThatExistsByIdOrThrowNotFoundException(id);

        Long reporterId = report.getReporterId();
        service.assertThatExistsByIdOrThrowNotFoundException(reporterId);

        service.reportAsInfected(id, reporterId);
        return ResponseEntity.noContent().build();
    }

}
