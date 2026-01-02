package com.pig4cloud.pig.biz;

import com.pig4cloud.pig.common.security.annotation.EnablePigResourceServer;
import com.pig4cloud.pig.common.swagger.annotation.EnablePigDoc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnablePigResourceServer
@EnablePigDoc(value = "business")
@EnableAsync
@EnableFeignClients
public class PigBusinessApplication {
	private static final Logger LOGGER = LogManager.getLogger(PigBusinessApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(PigBusinessApplication.class, args);
		LOGGER.info("Pig Business Application started successfully.");
    }
}

