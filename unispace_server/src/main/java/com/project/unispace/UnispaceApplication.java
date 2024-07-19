package com.project.unispace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UnispaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnispaceApplication.class, args);
	}

}
