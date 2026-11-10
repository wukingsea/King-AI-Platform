# King AI Platform

> 企业级 AI Agent 开发平台 · P0 品牌化基线
> 基于上游 [xuxueli/xxl-ai](https://github.com/xuxueli/xxl-ai) 1.5.0-SNAPSHOT（提交 `1854b1e`）改造。

## 一、项目简介

King AI Platform 是一个「云本结合」的 AI Agent 开发平台，同一 Monorepo 提供两种可独立使用、也可组合的交付形态：

| 形态 | 面向 | 定位 | 依赖 |
|---|---|---|---|
| **云版 Cloud**（Web 服务端） | 团队、多业务空间 | Agent 编排与一键发布、RAG 知识库、MCP 工具、SKILL 技能、多供应商模型、RBAC 权限治理 | MySQL + Redis + Milvus |
| **本地版 Desk**（桌面客户端） | 个人 | 本地优先，直接操作本机项目 / 终端 / Git / 浏览器，数据仅落本地 SQLite | 零外部依赖 |

本仓库当前处于 **P0 品牌化阶段**：工程标识、Maven 坐标、Java 包名、镜像与配置前缀已统一为 `com.king.ai` / `king-ai-*`，功能与上游保持一致，作为后续企业级改造的基线。

## 二、模块结构

| 模块 | 说明 |
|---|---|
| `king-ai-api` | 云版后端，Spring Boot 4.1.2 / Java 17，端口 8080；`business`（agent/chat/session/knowledge/mcp/skill/memory/space/supplier）+ `framework` 两层 |
| `king-ai-ui` | 云版前端，Vue 3.5 + Element Plus + TypeScript + Vite，开发端口 3000 |
| `king-ai-desk` | 本地版桌面端，Electron + Vue3 + Pi Agent 运行时 + SQLite |
| `king-ai-sample` | 示例 MCP 服务，Spring AI `@McpTool`，端口 8091 |
| `docker` | 一键部署栈：mysql / redis / etcd / minio / milvus / attu / api / sample |
| `doc` | 官方文档、建表脚本 `db/tables_king_ai.sql`、界面截图 |
| `.agents/skills` | AI 编程助手开发规范（king-ai / king-ai-desk 两套 SKILL.md） |

## 三、快速开始

```bash
# 后端（Maven 聚合 king-ai-api + king-ai-sample）
mvn -B package --file pom.xml

# 云版前端
cd king-ai-ui && pnpm install && pnpm dev

# 桌面端
cd king-ai-desk && pnpm install && pnpm dev

# 一键部署
cd docker && docker compose up -d
```

开发期前后端分离（UI 3000 / API 8080）；部署期前端产物内嵌进 API jar，单包单端口发布。详细部署与配置见 `doc/King-AI-Platform官方文档.md`。

## 四、来源与许可（重要）

- 上游仓库：https://github.com/xuxueli/xxl-ai ，作者 xuxueli（许雪里）
- 许可证：**GNU GPL v3**，见 [LICENSE](LICENSE)。原作者版权声明完整保留。
- 本仓库为上游的衍生作品。依据 GPL-3.0，若对外分发或提供网络服务以外的交付，衍生作品需继续以 GPL-3.0 开源并保留版权声明；仅企业内部自用不触发分发义务。
- 如计划闭源商业化，需另行取得上游作者授权或更换许可方案。

## 五、改造记录

| 阶段 | 内容 | 状态 |
|---|---|---|
| P0 | 品牌化：Maven 坐标 / Java 包名 / 模块名 / 配置前缀 / 镜像名 / 文档与 UI 文案统一为 King AI Platform | ✅ 已完成 |
| P1 | 企业安全：★ AK/SK + HMAC-SHA256 签名鉴权、统一身份认证对接、审计留痕、敏感配置加密 | ⏳ 规划中 |
| P2 | 平台化：★ Spring Cloud Gateway / Nacos / Resilience4j / OpenFeign 与可观测体系 | ⏳ 规划中 |

> 当前版本保持 XXL 原生技术栈（Spring Boot 单体 + XXL-SSO + MySQL/Redis/Milvus），未引入额外中间件。
