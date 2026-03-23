package com.pig4cloud.pig.ai.mcp;

import com.pig4cloud.pig.common.swagger.annotation.EnablePigDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnablePigDoc("ai-mcp")
public class PigAiMcpApplication {

	public static void main(String[] args) {
		SpringApplication.run(PigAiMcpApplication.class, args);
	}
}
