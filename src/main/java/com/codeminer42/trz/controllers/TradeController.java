package com.codeminer42.trz.controllers;

import com.codeminer42.trz.dto.SurvivorResponseDTO;
import com.codeminer42.trz.dto.TradeDTO;
import com.codeminer42.trz.dto.TradeSideDTO;
import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.services.SurvivorService;
import com.codeminer42.trz.services.TradeService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/trades", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "trades", description = "Operations regarding items trades by survivors")
public class TradeController {

    @Autowired
    private TradeService service;

    @Autowired
    private SurvivorService survivorService;

    @Operation(summary = "Trade items with other survivors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully traded items", content = @Content),
            @ApiResponse(responseCode = "400", content = @Content,
                    description = "Invalid trade provided or at least one of the survivors is infected"),
            @ApiResponse(responseCode = "404", content = @Content,
                    description = "One the survivors or items was not found")})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> trade(@RequestBody  @Valid TradeDTO trade) {
        TradeSideDTO left = trade.getLeft();
        TradeSideDTO right = trade.getRight();

        Survivor leftSurvivor = survivorService.findByIdOrThrowNotFoundException(left.getSurvivorId());
        Survivor rightSurvivor = survivorService.findByIdOrThrowNotFoundException(right.getSurvivorId());

        service.trade(leftSurvivor, left, rightSurvivor, right);

        return ResponseEntity.noContent().build();
    }
}
