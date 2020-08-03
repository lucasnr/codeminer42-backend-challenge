package com.codeminer42.trz.repositories;

import com.codeminer42.trz.TheResidentZombieApplication;
import com.codeminer42.trz.models.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = {TheResidentZombieApplication.class})
@ActiveProfiles("test")
public class ItemRepositoryTests {

    @Autowired
    private ItemRepository repository;

    @Test
    void whenLoadsDataSQLFile_thenItemsCanBeFound() {
        String[] names = {"Fiji Water", "Campbell Soup", "First Aid Pouch", "AK47"};
        Stream.of(names).forEach(name -> {
            Optional<Item> item = repository.findByName(name);
            assertThat(item.isPresent(), is(true));
        });
    }
}
