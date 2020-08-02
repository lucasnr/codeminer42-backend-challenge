package com.codeminer42.trz.dto;

import com.codeminer42.trz.models.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalEntryDTO {
    @JsonProperty("item_id")
    @NotNull
    @Positive
    private Long itemId;

    @NotNull
    @Positive
    private Integer amount;

    @JsonIgnore
    private Item item;
}
