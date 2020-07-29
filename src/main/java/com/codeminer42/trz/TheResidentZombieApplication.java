package com.codeminer42.trz;

import com.codeminer42.trz.controllers.SurvivorController;
import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.repositories.SurvivorRepository;
import com.codeminer42.trz.services.SurvivorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableSpringDataWebSupport
@EntityScan(basePackageClasses = {Survivor.class})
@EnableJpaRepositories(basePackageClasses = {SurvivorRepository.class})
@ComponentScan(basePackageClasses = {SurvivorController.class, SurvivorService.class})
@CrossOrigin
public class TheResidentZombieApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheResidentZombieApplication.class, args);
	}

}
