package com.pig4cloud.pig.ai.app;

import com.pig4cloud.pig.ai.app.config.AiModuleProperties;
import com.pig4cloud.pig.common.security.annotation.EnablePigResourceServer;
import com.pig4cloud.pig.common.swagger.annotation.EnablePigDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnablePigDoc("ai-app")
@EnablePigResourceServer
@EnableFeignClients
@EnableConfigurationProperties(AiModuleProperties.class)
public class PigAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PigAiApplication.class, args);
	}
}
