package com.pig4cloud.pig.business;

import com.pig4cloud.pig.common.security.annotation.EnablePigResourceServer;
import com.pig4cloud.pig.common.swagger.annotation.EnablePigDoc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnablePigResourceServer
@EnablePigDoc(value = "admin", isMicro = false)
public class PigBusinessApplication {
	private static final Logger LOGGER = LogManager.getLogger(PigBusinessApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(PigBusinessApplication.class, args);
		LOGGER.info("Pig Business Application started successfully.");
    }
}

