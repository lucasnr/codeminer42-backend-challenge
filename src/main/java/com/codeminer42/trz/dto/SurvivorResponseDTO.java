package com.codeminer42.trz.dto;

import com.codeminer42.trz.models.Survivor;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class SurvivorResponseDTO extends SurvivorDTO {
    private final Set<InventoryEntryDetailedDTO> inventory;
    private final boolean infected;
    private final Long id;

    public SurvivorResponseDTO(Survivor survivor) {
        super(survivor);

        this.id = survivor.getId();
        this.inventory = survivor.getInventory()
                .stream()
                .map(InventoryEntryDetailedDTO::new)
                .collect(Collectors.toSet());
        this.infected = survivor.isInfected();
    }
}
