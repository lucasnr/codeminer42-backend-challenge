package com.codeminer42.trz.dto;

import com.codeminer42.trz.models.InventoryEntry;
import com.codeminer42.trz.models.Item;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
public class InventoryEntryDTO {

    @JsonProperty(value = "item_id")
    @NotNull
    private Long itemId;

    @NotNull
    @Positive
    @Min(1)
    @Max(999)
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
