package com.codeminer42.trz.dto;

import com.codeminer42.trz.models.Survivor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SurvivorRequestDTO extends SurvivorDTO {

    @NotNull
    @NotEmpty
    @Valid
    private Set<InventoryEntryDTO> inventory;

    public SurvivorRequestDTO(Survivor survivor) {
        super(survivor);

        this.inventory = survivor.getInventory()
                .stream()
                .map(InventoryEntryDTO::new)
                .collect(Collectors.toSet());
    }

    public Survivor toModel() {
        return Survivor.builder()
                .name(super.name)
                .age(super.age)
                .gender(super.gender)
                .latitude(super.location.getLatitude())
                .longitude(super.location.getLongitude())
                .inventory(this.inventory.stream()
                        .map(InventoryEntryDTO::toModel)
                        .collect(Collectors.toSet()))
                .build();
    }
}
