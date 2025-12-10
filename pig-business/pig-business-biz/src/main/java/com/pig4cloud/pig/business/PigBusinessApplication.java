package com.pig4cloud.pig.business;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PigBusinessApplication {
	private static final Logger LOGGER = LogManager.getLogger(PigBusinessApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(PigBusinessApplication.class, args);
		LOGGER.info("Pig Business Application started successfully.");
    }
}

