package com.ivanjiang.druginteraction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DrugInteractionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrugInteractionApplication.class, args);
    }
}
