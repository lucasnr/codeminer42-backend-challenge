package com.codeminer42.trz.config;

import com.codeminer42.trz.models.Survivor;
import com.codeminer42.trz.repositories.SurvivorRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import javax.sql.DataSource;
import java.net.URISyntaxException;

@Configuration
@EnableSpringDataWebSupport
@EntityScan(basePackageClasses = {Survivor.class})
@EnableJpaRepositories(basePackageClasses = {SurvivorRepository.class})
public class JpaConfiguration {

    @Bean
    @Profile("prod")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public DataSource dataSource() throws URISyntaxException {
        String dbUrl = System.getenv("SPRING_DATASOURCE_URL").concat("&stringtype=unspecified");

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(dbUrl);
        return dataSourceBuilder.build();
    }
}
