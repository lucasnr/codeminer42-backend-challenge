package com.codeminer42.trz.dto;

import com.codeminer42.trz.models.Item;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class ItemDTO {

    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 50)
    private String name;

    @Positive
    @Max(3)
    private Integer points;

    public ItemDTO(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.points = item.getPoints();
    }
}
