package com.parth.quizapp;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuizappApplication {

	public static void main(String[] args) {
		// Load .env and inject variables into system properties
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		// Start Spring Boot app
		SpringApplication.run(QuizappApplication.class, args);
	}
}
