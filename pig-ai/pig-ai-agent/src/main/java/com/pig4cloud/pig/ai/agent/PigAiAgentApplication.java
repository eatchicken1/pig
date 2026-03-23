package com.pig4cloud.pig.ai.agent;

import com.pig4cloud.pig.common.security.annotation.EnablePigResourceServer;
import com.pig4cloud.pig.common.swagger.annotation.EnablePigDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnablePigDoc("ai-agent")
@EnablePigResourceServer
@EnableFeignClients
public class PigAiAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PigAiAgentApplication.class, args);
	}
}
