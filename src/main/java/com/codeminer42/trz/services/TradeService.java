package com.codeminer42.trz.services;

import com.codeminer42.trz.dto.ProposalEntryDTO;
import com.codeminer42.trz.dto.TradeSideDTO;
import com.codeminer42.trz.exceptions.BadRequestException;
import com.codeminer42.trz.exceptions.NotFoundException;
import com.codeminer42.trz.models.InventoryEntry;
import com.codeminer42.trz.models.Item;
import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.repositories.InventoryEntryRepository;
import com.codeminer42.trz.repositories.ItemRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TradeService {

    @Autowired
    @Setter
    private ItemRepository itemRepository;

    @Autowired
    private InventoryEntryRepository inventoryEntryRepository;

    public void trade(Survivor leftSurvivor, TradeSideDTO left, Survivor rightSurvivor, TradeSideDTO right) {
        assertThatSurvivorsCanTrade(leftSurvivor, rightSurvivor);

        checkSurvivorInventory(leftSurvivor, left.getItems());
        checkSurvivorInventory(rightSurvivor, right.getItems());

        assertThatBothSidesEqualsTheSameAmountOfPoints(left, right);

        swapItems(left.getItems(), leftSurvivor, rightSurvivor);
        swapItems(right.getItems(), rightSurvivor, leftSurvivor);

        updateSurvivorInventory(leftSurvivor);
        updateSurvivorInventory(rightSurvivor);
    }

    public void updateSurvivorInventory(Survivor survivor) {
        inventoryEntryRepository.saveAll(survivor.getInventory());

        List<List<InventoryEntry>> values = new ArrayList<>(
                survivor.getInventory().stream()
                .collect(Collectors.partitioningBy(entry -> entry.getAmount() > 0))
                .values());

        List<InventoryEntry> lesserThanOrEqualZeroAmountEntries = values.get(0);
        List<InventoryEntry> validEntries = values.get(1);

        survivor.setInventory(new HashSet<>(validEntries));
        inventoryEntryRepository.deleteAll(lesserThanOrEqualZeroAmountEntries);
    }

    public void swapItems(Set<ProposalEntryDTO> items, Survivor from, Survivor to) {
        items.forEach(proposal -> {
            Item item = proposal.getItem();
            Integer amount = proposal.getAmount();

            from.removeItem(item, amount);
            to.addItem(item, amount);
        });
    }

    public void checkSurvivorInventory(Survivor survivor, Set<ProposalEntryDTO> items) {
        items.forEach(entry -> {
            Item item = itemRepository.findById(entry.getItemId())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("No item was found with the id [%d]", entry.getItemId())));
            entry.setItem(item);

            InventoryEntry inventoryEntry = survivor.getInventory().stream()
                    .filter(survivorInventoryEntry -> survivorInventoryEntry.getItem().equals(item))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(
                            String.format("The survivor %s of id [%d] does not have %s[%d] in his inventory",
                                    survivor.getName(), survivor.getId(), item.getName(), item.getId())));

            if (inventoryEntry.getAmount() < entry.getAmount())
                throw new BadRequestException(String.format("The survivor %s of id [%d] has only %d %ss[%d] in his inventory",
                        survivor.getName(), survivor.getId(), inventoryEntry.getAmount(), item.getName(), item.getId()));
        });
    }

    public void assertThatSurvivorsCanTrade(Survivor survivor, Survivor otherSurvivor) {
        if (survivor.getId().equals(otherSurvivor.getId()))
            throw new BadRequestException("A survivor cannot trade items with himself");

        if (survivor.isInfected())
            throw new BadRequestException(String.format("The survivor %s of id %d cannot trade because is infected",
                    survivor.getName(), survivor.getId()));

        if (otherSurvivor.isInfected())
            throw new BadRequestException(String.format("The survivor %s of id %d cannot trade because is infected",
                    otherSurvivor.getName(), otherSurvivor.getId()));
    }

    public void assertThatBothSidesEqualsTheSameAmountOfPoints(TradeSideDTO left, TradeSideDTO right) {
        Integer leftPoints = left.getItems().stream()
                .map(entry -> entry.getAmount() * entry.getItem().getPoints())
                .reduce(0, (accumulator, value) -> accumulator + value);

        Integer rightPoints = right.getItems().stream()
                .map(entry -> entry.getAmount() * entry.getItem().getPoints())
                .reduce(0, (accumulator, value) -> accumulator + value);

        if (!leftPoints.equals(rightPoints))
            throw new BadRequestException("The left and right sides of the trade does not equal in points");

    }

}
