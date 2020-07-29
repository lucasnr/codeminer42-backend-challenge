package com.codeminer42.trz.dto;

import com.codeminer42.trz.models.Survivor;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class SurvivorResponseDTO extends SurvivorDTO {
    private Set<InventoryEntryDetailedDTO> inventory;

    public SurvivorResponseDTO(Survivor survivor) {
        super(survivor);

        this.inventory = survivor.getInventory()
                .stream()
                .map(InventoryEntryDetailedDTO::new)
                .collect(Collectors.toSet());
    }
}
