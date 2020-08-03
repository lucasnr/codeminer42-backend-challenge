package com.codeminer42.trz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportInfoDTO<T> {
    private String description;
    private T value;
}
