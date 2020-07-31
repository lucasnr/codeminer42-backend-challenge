package com.codeminer42.trz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class TradeDTO {
    @Valid
    @NotNull
    private TradeSideDTO left;
    @Valid
    @NotNull
    private TradeSideDTO right;
}
