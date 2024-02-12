package com.coding.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableJpaRepositories("com.coding.challenge.model.persistence.repositories")
@EntityScan("com.coding.challenge.model.persistence")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class CodingChallengeApplication {

	private static final Logger log = LoggerFactory.getLogger(CodingChallengeApplication.class);


	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {

		SpringApplication.run(CodingChallengeApplication.class, args);
		log.info("Coding Challenge Application Started");
	}

}
