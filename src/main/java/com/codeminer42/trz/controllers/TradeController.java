package com.codeminer42.trz.controllers;

import com.codeminer42.trz.dto.TradeDTO;
import com.codeminer42.trz.dto.TradeSideDTO;
import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.services.SurvivorService;
import com.codeminer42.trz.services.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/trades", produces = MediaType.APPLICATION_JSON_VALUE)
public class TradeController {

    @Autowired
    private TradeService service;

    @Autowired
    private SurvivorService survivorService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> trade(@RequestBody  @Valid TradeDTO trade) {
        TradeSideDTO left = trade.getLeft();
        TradeSideDTO right = trade.getRight();

        Survivor leftSurvivor = survivorService.findByIdOrThrowNotFoundException(left.getSurvivorId());
        Survivor rightSurvivor = survivorService.findByIdOrThrowNotFoundException(right.getSurvivorId());

        service.trade(leftSurvivor, left, rightSurvivor, right);

        return ResponseEntity.noContent().build();
    }
}
