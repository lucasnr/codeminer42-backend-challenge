package com.codeminer42.trz.controllers;

import com.codeminer42.trz.dto.LocationDTO;
import com.codeminer42.trz.dto.ReportDTO;
import com.codeminer42.trz.dto.SurvivorRequestDTO;
import com.codeminer42.trz.dto.SurvivorResponseDTO;
import com.codeminer42.trz.exceptions.NotFoundException;
import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.services.SurvivorService;
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
import java.util.Optional;

@RestController
@RequestMapping(path = "/survivors", produces = MediaType.APPLICATION_JSON_VALUE)
public class SurvivorController {

    @Autowired
    private SurvivorService service;

    @GetMapping("/{id}")
    public ResponseEntity<SurvivorResponseDTO> findById(@PathVariable("id") Long id) {
        Survivor survivor = findByIdOrThrowNotFoundException(id);
        return ResponseEntity.ok(new SurvivorResponseDTO(survivor));
    }

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

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<SurvivorResponseDTO> updateLocation(@PathVariable("id") Long id,
                                                              @RequestBody @Valid LocationDTO location) {
        Survivor survivor = findByIdOrThrowNotFoundException(id);
        survivor.setLatitude(location.getLatitude());
        survivor.setLongitude(location.getLongitude());
        return ResponseEntity.ok(new SurvivorResponseDTO(survivor));
    }

    @PostMapping(path = "/{id}/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> reportAsInfected(@PathVariable("id") Long id, @RequestBody @Valid ReportDTO report) {
        assertThatExistsByIdOrThrowNotFoundException(id);

        Long reporterId = report.getReporterId();
        assertThatExistsByIdOrThrowNotFoundException(reporterId);

        service.reportAsInfected(id, reporterId);
        return ResponseEntity.noContent().build();
    }

    private void assertThatExistsByIdOrThrowNotFoundException(Long id) {
        if(! service.existsById(id))
            throw new NotFoundException(notFoundMessage(id));
    }

    private Survivor findByIdOrThrowNotFoundException(Long id) {
        Optional<Survivor> optional = service.findById(id);
        return optional.orElseThrow(() ->
                new NotFoundException(notFoundMessage(id)));
    }

    private String notFoundMessage(Long id) {
        return String.format("No survivor was found with the id [%d]", id);
    }
}
