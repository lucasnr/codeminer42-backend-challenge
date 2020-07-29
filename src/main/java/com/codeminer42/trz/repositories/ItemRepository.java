package com.codeminer42.trz.repositories;

import com.codeminer42.trz.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
