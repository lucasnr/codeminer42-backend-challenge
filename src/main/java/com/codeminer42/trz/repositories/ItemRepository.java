package com.codeminer42.trz.repositories;

import com.codeminer42.trz.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select sum(inv.amount) from Item i join InventoryEntry inv on inv.item.id = i.id" +
            " where i.id = :id group by i.id")
    public long countAmountById(Long id);
}
