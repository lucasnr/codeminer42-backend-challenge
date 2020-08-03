package com.codeminer42.trz.services;

import com.codeminer42.trz.dto.ItemAmountPairDTO;
import com.codeminer42.trz.repositories.ItemRepository;
import com.codeminer42.trz.repositories.SurvivorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository repository;

    @Autowired
    private SurvivorRepository survivorRepository;

    public Set<ItemAmountPairDTO> getTotalAmountsPerSurvivor() {
        double survivorsCount = survivorRepository.count();

        return repository.findAll().stream()
                .map(item -> {
                    Double totalPerSurvivor;
                    if (survivorsCount == 0.0) totalPerSurvivor = 0.0;
                    else totalPerSurvivor = repository.countAmountById(item.getId()) / survivorsCount;

                    return ItemAmountPairDTO.builder()
                            .name(item.getName())
                            .amount(totalPerSurvivor)
                            .build();
                })
                .collect(Collectors.toSet());
    }
}
