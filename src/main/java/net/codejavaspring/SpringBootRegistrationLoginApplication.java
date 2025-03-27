package net.codejavaspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"net.codejavaspring", "net.codejavaspring.controller", 
                              "net.codejavaspring.service", "net.codejavaspring.security",
                              "net.codejavaspring.config"})
@EntityScan("net.codejavaspring.model")
@EnableJpaRepositories("net.codejavaspring.repository")
public class SpringBootRegistrationLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRegistrationLoginApplication.class, args);
    }
}
