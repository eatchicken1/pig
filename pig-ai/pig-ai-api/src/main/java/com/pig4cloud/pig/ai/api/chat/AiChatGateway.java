package com.pig4cloud.pig.ai.api.chat;

import java.io.InputStream;

public interface AiChatGateway {

	InputStream stream(AiChatCommand command);
}
