package com.codeminer42.trz.services;

import com.codeminer42.trz.exceptions.BadRequestException;
import com.codeminer42.trz.exceptions.NotFoundException;
import com.codeminer42.trz.models.Item;
import com.codeminer42.trz.models.Report;
import com.codeminer42.trz.models.ReportId;
import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.repositories.InventoryEntryRepository;
import com.codeminer42.trz.repositories.ItemRepository;
import com.codeminer42.trz.repositories.ReportRepository;
import com.codeminer42.trz.repositories.SurvivorRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SurvivorService {

    @Autowired
    @Setter
    private SurvivorRepository repository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private InventoryEntryRepository inventoryEntryRepository;

    @Autowired
    private ReportRepository reportRepository;

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

    public void reportAsInfected(Long reportedId, Long reporterId) {
        if (reportedId.equals(reporterId))
            throw new BadRequestException("You cannot report yourself as infected");

        ReportId id = ReportId.builder()
                .reportedId(reportedId)
                .reporterId(reporterId)
                .build();

        if (reportRepository.existsById(id))
            throw new BadRequestException("This survivor has already reported this other survivor as infected");

        Report report = new Report(id);
        reportRepository.save(report);
    }

    public Optional<Survivor> findById(Long id) {
        return repository.findById(id);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public void assertThatExistsByIdOrThrowNotFoundException(Long id) {
        if (!repository.existsById(id))
            throw new NotFoundException(notFoundMessage(id));
    }

    public Survivor findByIdOrThrowNotFoundException(Long id) {
        Optional<Survivor> optional = repository.findById(id);
        return optional.orElseThrow(() ->
                new NotFoundException(notFoundMessage(id)));
    }

    public Double getPercentageOfInfected() {
        return getPercentage(true);
    }

    public Double getPercentageOfNonInfected() {
        return getPercentage(false);
    }

    public Integer getPointsLost() {
        return repository.countPointsLost();
    }

    private Double getPercentage(boolean infected) {
        long numberOfInfected = infected ? repository.countInfected() : repository.countNonInfected();
        long total = repository.count();

        if(total == 0)
            return 0.0;

        return (double) numberOfInfected / (double) total;
    }

    private String notFoundMessage(Long id) {
        return String.format("No survivor was found with the id [%d]", id);
    }

}
