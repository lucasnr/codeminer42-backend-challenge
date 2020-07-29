package com.codeminer42.trz.services;

import com.codeminer42.trz.exceptions.NotFoundException;
import com.codeminer42.trz.models.InventoryEntry;
import com.codeminer42.trz.models.Item;
import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.repositories.InventoryEntryRepository;
import com.codeminer42.trz.repositories.ItemRepository;
import com.codeminer42.trz.repositories.SurvivorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SurvivorService {

    @Autowired
    private SurvivorRepository repository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private InventoryEntryRepository inventoryEntryRepository;

    public void save(Survivor survivor) {
        repository.save(survivor);

        survivor.getInventory().forEach(entry -> {
            Long itemId = entry.getItem().getId();
            Optional<Item> optional = itemRepository.findById(itemId);
            Item item = optional.orElseThrow(() ->
                    new NotFoundException(String.format("No item was found with the id [%d]", itemId)));

            entry.setItem(item);
            entry.setSurvivorId(survivor.getId());
        });

        inventoryEntryRepository.saveAll(survivor.getInventory());
    }

    public Optional<Survivor> findById(Long id) {
        return repository.findById(id);
    }
}
