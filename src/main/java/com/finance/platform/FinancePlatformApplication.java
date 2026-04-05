package com.finance.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Finance Data Processing Platform
 *
 * Entry point. Spring Boot auto-configures:
 * - Embedded Tomcat (web)
 * - H2 DataSource (persistence)
 * - Spring Security filter chain (auth)
 * - Bean validation (input safety)
 *
 * The @SpringBootApplication annotation combines:
 * - @Configuration (this class is a config source)
 * - @EnableAutoConfiguration (Spring Boot magic)
 * - @ComponentScan (finds our @Service, @Repository, @Controller beans)
 */

@SpringBootApplication
public class FinancePlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancePlatformApplication.class, args);
	}

}
