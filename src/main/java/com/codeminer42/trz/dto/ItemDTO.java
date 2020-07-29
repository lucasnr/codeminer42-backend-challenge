package com.codeminer42.trz.dto;

import com.codeminer42.trz.models.Item;
import lombok.Data;

@Data
public class ItemDTO {

    private Long id;
    private String name;
    private Integer points;

    public ItemDTO(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.points = item.getPoints();
    }
}
