package com.codeminer42.trz.repositories;

import com.codeminer42.trz.models.Report;
import com.codeminer42.trz.models.ReportId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, ReportId> {
}
