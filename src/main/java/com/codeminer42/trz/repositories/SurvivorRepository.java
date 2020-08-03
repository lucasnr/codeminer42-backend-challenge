package com.codeminer42.trz.repositories;

import com.codeminer42.trz.models.Survivor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SurvivorRepository extends JpaRepository<Survivor, Long> {

    @Query("select count(s) from Survivor s where s.reports.size >= 5")
    public long countInfected();

    @Query("select count(s) from Survivor s where s.reports.size < 5")
    public long countNonInfected();

    @Query("select sum(i.points * inv.amount) from Survivor s join s.inventory inv" +
            " join inv.item i where s.reports.size >= 5")
    public int countPointsLost();
}
