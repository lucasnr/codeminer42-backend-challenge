package com.codeminer42.trz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class ReportDTO {

    @JsonProperty(value = "reporter_id")
    @NotNull
    @Positive
    private Long reporterId;
}
