package com.codeminer42.trz.config;

import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.repositories.SurvivorRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport
@EntityScan(basePackageClasses = {Survivor.class})
@EnableJpaRepositories(basePackageClasses = {SurvivorRepository.class})
public class JpaConfiguration {
}
