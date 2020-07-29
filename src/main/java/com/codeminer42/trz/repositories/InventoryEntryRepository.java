package com.codeminer42.trz.repositories;

import com.codeminer42.trz.models.InventoryEntryId;
import com.codeminer42.trz.models.InventoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryEntryRepository extends JpaRepository<InventoryEntry, InventoryEntryId> {
}
