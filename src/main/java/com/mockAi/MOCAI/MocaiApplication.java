package com.mockAi.MOCAI;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MocaiApplication {

	// now let's fix the cookie thing and
	// then add the oauth login

	public static void main(String[] args) {
		SpringApplication.run(MocaiApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> System.out.println("Application started ....");
	}

}