package com.example.Tambola;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.example.Tambola")
@EnableScheduling
public class TambolaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TambolaApplication.class, args);
	}

}
