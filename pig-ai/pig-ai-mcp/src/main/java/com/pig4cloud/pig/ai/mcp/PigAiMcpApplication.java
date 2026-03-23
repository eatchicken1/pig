package com.pig4cloud.pig.ai.mcp;

import com.pig4cloud.pig.common.swagger.annotation.EnablePigDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnablePigDoc("ai-mcp")
@EnableFeignClients
public class PigAiMcpApplication {

	public static void main(String[] args) {
		SpringApplication.run(PigAiMcpApplication.class, args);
	}
}
