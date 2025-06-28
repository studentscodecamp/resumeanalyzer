package com.resumeanalyzer;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Spring Boot Resume Analyzer Application.
 * This class uses Spring's @SpringBootApplication annotation, which
 * combines @Configuration, @EnableAutoConfiguration, and @ComponentScan.
 * It sets up the core components and starts the embedded Tomcat server.
 */
@SpringBootApplication
public class ResumeAnalyzerApplication {

	public static void main(String[] args) {
		// Run the Spring Boot application. This method sets up the application context,
		// performs classpath scanning, and starts the embedded web server (e.g., Tomcat).
		SpringApplication.run(ResumeAnalyzerApplication.class, args);
	}
}