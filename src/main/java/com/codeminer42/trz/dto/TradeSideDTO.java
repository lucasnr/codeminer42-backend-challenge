package com.codeminer42.trz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeSideDTO {
    @JsonProperty(value = "survivor_id")
    @NotNull
    @Positive
    private Long survivorId;

    @NotNull
    @NotEmpty
    @Valid
    private Set<ProposalEntryDTO> items = new HashSet<>();
}
