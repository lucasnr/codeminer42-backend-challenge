package com.codeminer42.trz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ItemAmountPairDTO {
    private String name;
    private Double amount;
}
