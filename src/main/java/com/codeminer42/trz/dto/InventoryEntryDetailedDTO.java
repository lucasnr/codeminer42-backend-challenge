package com.codeminer42.trz.dto;

import com.codeminer42.trz.models.InventoryEntry;
import lombok.Getter;
import lombok.experimental.Delegate;

public class InventoryEntryDetailedDTO {

    @Delegate private ItemDTO item;
    @Getter private Integer amount;

    public InventoryEntryDetailedDTO(InventoryEntry entry) {
        this.item = new ItemDTO(entry.getItem());
        this.amount = entry.getAmount();
    }
}
