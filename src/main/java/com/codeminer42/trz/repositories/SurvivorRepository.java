package com.codeminer42.trz.repositories;

import com.codeminer42.trz.models.Survivor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SurvivorRepository extends JpaRepository<Survivor, Long> {

    @Query("select count(distinct s) from Survivor s join s.reports r where r.size >= 5")
    public long countInfected();
}
