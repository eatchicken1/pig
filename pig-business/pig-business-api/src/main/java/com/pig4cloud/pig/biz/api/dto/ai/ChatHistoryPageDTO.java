package com.pig4cloud.pig.biz.api.dto.ai;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatHistoryPageDTO {

	private List<ChatHistoryMessageDTO> records = new ArrayList<>();

	private long total;

	private long size;

	private long current;

	private long pages;
}
