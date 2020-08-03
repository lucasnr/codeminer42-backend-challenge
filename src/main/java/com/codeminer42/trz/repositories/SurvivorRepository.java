package com.codeminer42.trz.repositories;

import com.codeminer42.trz.models.Survivor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SurvivorRepository extends JpaRepository<Survivor, Long> {

    @Query("select count(s) from Survivor s where s.reports.size >= 5")
    public Optional<Integer> countInfected();

    @Query("select count(s) from Survivor s where s.reports.size < 5")
    public Optional<Integer> countNonInfected();

    @Query("select sum(i.points * inv.amount) from Survivor s join s.inventory inv" +
            " join inv.item i where s.reports.size >= 5")
    public Optional<Integer> countPointsLost();
}
