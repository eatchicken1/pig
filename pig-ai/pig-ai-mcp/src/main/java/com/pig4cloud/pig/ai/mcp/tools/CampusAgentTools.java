package com.pig4cloud.pig.ai.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class CampusAgentTools {

	@Tool(description = "说明当前 AI 模块的职责边界")
	public String describeArchitecture() {
		return "business-core 负责事务与权限，ai-application 负责模型编排与RAG，mcp-tools 负责工具暴露，python-agent-runtime 负责长流程与多智能体工作流。";
	}

	@Tool(description = "列出第一批应该暴露的校园 MCP 工具")
	public String listInitialTools() {
		return "建议先暴露 4 类工具: echo_profile.read, knowledge_base.search, match_record.create, social_moment.publish。";
	}

	@Tool(description = "输出当前 AI 重构阶段")
	public String currentStage() {
		return "当前处于 scaffold-ready 阶段，重点是模块边界与运行时分层，而不是功能迁移完毕。";
	}
}
