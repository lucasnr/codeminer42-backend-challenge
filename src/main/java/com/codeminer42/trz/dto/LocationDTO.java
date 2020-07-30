package com.codeminer42.trz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class LocationDTO {
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;

}
