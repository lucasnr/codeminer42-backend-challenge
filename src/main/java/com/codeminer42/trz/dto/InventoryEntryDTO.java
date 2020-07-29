package com.codeminer42.trz.dto;

import com.codeminer42.trz.models.InventoryEntry;
import com.codeminer42.trz.models.Item;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventoryEntryDTO {

    @JsonProperty(value = "item_id")
    private Long itemId;
    private Integer amount;

    public InventoryEntryDTO(InventoryEntry entry) {
        this.itemId = entry.getItem().getId();
        this.amount = entry.getAmount();
    }

    public InventoryEntry toModel() {
        return InventoryEntry.builder()
                .amount(this.amount)
                .item(Item.builder().id(this.itemId).build())
                .build();
    }
}
