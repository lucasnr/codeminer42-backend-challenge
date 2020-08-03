package com.codeminer42.trz;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@SpringBootApplication
@CrossOrigin
@OpenAPIDefinition(info = @Info(
        title = "The Resident Zombie Backend API",
        description = "A RESTful API for survivors of an apocalyptic virus. Join the fight against the T-Virus!",
        contact = @Contact(name = "Lucas Nascimento", url = "https://lucasnr.github.io", email = "lucasnascimentoribeiro13@gmail.com"),
        version = "0.1.0",
        license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT")
))
public class TheResidentZombieApplication {

    public static void main(String[] args) {
        SpringApplication.run(TheResidentZombieApplication.class, args);
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.US);
        return sessionLocaleResolver;
    }
}
