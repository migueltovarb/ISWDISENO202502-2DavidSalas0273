package com.vetcarepro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VetCareProApplication {

	public static void main(String[] args) {
		SpringApplication.run(VetCareProApplication.class, args);
	}

}
