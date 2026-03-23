# AI 重构骨架

## 目标

- `business-core` 继续负责用户、订单、钱包、权限、审计和事务。
- `ai-application` 负责模型编排、RAG、Prompt 管理和统一 AI 路由。
- `mcp-tools` 负责把校园业务能力暴露成工具。
- `python-agent-runtime` 只处理长流程、多智能体和需要 checkpoint 的任务。

## 当前模块

- `pig-ai-api`: 统一 AI 契约、命令对象和业务侧边界接口。
- `pig-ai-app`: Java AI 应用层骨架，后续承接 Spring AI Alibaba 编排。
- `pig-ai-mcp`: MCP 服务骨架，后续暴露校园业务工具。
- `pig-business-biz`: 通过 `AiChatGateway`、`AiKnowledgeGateway`、`AiMatchGateway` 访问 AI，不再直接耦合某种实现。

## 本次调整原则

- 不打断现有 `business -> python-engine` 链路。
- 先抽边界接口，再逐步迁移具体能力。
- 前端、数据库表和业务入口先不做破坏式搬迁。
