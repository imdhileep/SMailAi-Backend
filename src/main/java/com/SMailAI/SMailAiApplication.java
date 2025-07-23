// SMailAiApplication.java
package com.SMailAI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
@EnableRetry
public class SMailAiApplication {

	public static void main(String[] args) {
		// Load .env file
		Dotenv dotenv = Dotenv.load();

		// Set system properties (Spring will pick these up)
		System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
		System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
		System.setProperty("ADMIN_PASS", dotenv.get("ADMIN_PASS")); 
		System.setProperty("DB_USER", dotenv.get("DB_USER"));// if you're using this
		System.setProperty("DB_PASS", dotenv.get("DB_PASS"));

		SpringApplication.run(SMailAiApplication.class, args);
	}
}

