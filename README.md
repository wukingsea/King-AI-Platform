# King AI Platform

> Enterprise-grade AI Agent development platform · P0 rebranding baseline
> Adapted from upstream [xuxueli/xxl-ai](https://github.com/xuxueli/xxl-ai) 1.5.0-SNAPSHOT (commit `1854b1e`).

## 1. Overview

King AI Platform is a "cloud + local" AI Agent development platform. A single monorepo provides two delivery forms that can be used independently or together:

| Edition | Audience | Positioning | Dependencies |
|---|---|---|---|
| **Cloud** (web server) | Teams, multiple business spaces | Agent orchestration and one-click publishing, RAG knowledge bases, MCP tools, SKILL skills, multi-vendor models, RBAC governance | MySQL + Redis + Milvus |
| **Desk** (desktop client) | Individuals | Local-first; works directly with local projects / terminal / Git / browser; data is stored only in local SQLite | No external dependencies |

This repository is currently in the **P0 rebranding phase**: project identifiers, Maven coordinates, Java package names, image names and configuration prefixes have been unified as `com.king.ai` / `king-ai-*`. Functionality remains identical to upstream and serves as the baseline for subsequent enterprise enhancements.

## 2. Module Layout

| Module | Description |
|---|---|
| `king-ai-api` | Cloud backend, Spring Boot 4.1.2 / Java 17, port 8080; two layers: `business` (agent/chat/session/knowledge/mcp/skill/memory/space/supplier) + `framework` |
| `king-ai-ui` | Cloud frontend, Vue 3.5 + Element Plus + TypeScript + Vite, dev port 3000 |
| `king-ai-desk` | Desktop edition, Electron + Vue 3 + Pi Agent runtime + SQLite |
| `king-ai-sample` | Sample MCP server, Spring AI `@McpTool`, port 8091 |
| `docker` | One-click deployment stack: mysql / redis / etcd / minio / milvus / attu / api / sample |
| `doc` | Official documentation, schema script `db/tables_king_ai.sql`, UI screenshots |
| `.agents/skills` | Development guidelines for AI coding assistants (two SKILL.md sets: king-ai / king-ai-desk) |

## 3. Quick Start

```bash
# Backend (Maven aggregates king-ai-api + king-ai-sample)
mvn -B package --file pom.xml

# Cloud frontend
cd king-ai-ui && pnpm install && pnpm dev

# Desktop client
cd king-ai-desk && pnpm install && pnpm dev

# One-click deployment
cd docker && docker compose up -d
```

During development, frontend and backend run separately (UI 3000 / API 8080). For deployment, the frontend build is embedded into the API jar and shipped as a single package on a single port. See `doc/King-AI-Platform官方文档.md` for detailed deployment and configuration instructions.

## 4. Origin and License (Important)

- Upstream repository: https://github.com/xuxueli/xxl-ai, author xuxueli (许雪里)
- License: **GNU GPL v3**, see [LICENSE](LICENSE). The original author's copyright notices are fully retained.
- This repository is a derivative work of the upstream project. Under GPL-3.0, if the derivative work is distributed externally (delivery beyond providing a network service), it must remain open source under GPL-3.0 with copyright notices retained; purely internal enterprise use does not trigger distribution obligations.
- If you plan closed-source commercialization, you must obtain separate authorization from the upstream author or adopt a different licensing arrangement.

## 5. Change Log

| Phase | Scope | Status |
|---|---|---|
| P0 | Rebranding: Maven coordinates / Java package names / module names / config prefixes / image names / docs and UI copy unified as King AI Platform | ✅ Done |
| P1 | Enterprise security: ★ AK/SK + HMAC-SHA256 signature authentication, unified identity integration, audit trails, encryption of sensitive configuration | ⏳ Planned |
| P2 | Platformization: ★ Spring Cloud Gateway / Nacos / Resilience4j / OpenFeign and observability stack | ⏳ Planned |

> The current version keeps the native XXL tech stack (Spring Boot monolith + XXL-SSO + MySQL/Redis/Milvus) without additional middleware.
