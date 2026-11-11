# King AI Platform

> Enterprise-grade AI Agent development platform · P0 rebranding baseline · version `1.0.0-SNAPSHOT`
> Adapted from upstream [xuxueli/xxl-ai](https://github.com/xuxueli/xxl-ai) 1.5.0-SNAPSHOT (commit `1854b1e`). Licensed under GPL-3.0.

King AI Platform is a "cloud + local" AI Agent development platform. A single monorepo ships two editions that can be used independently or together:

| | **Cloud** (web server) | **Desk** (desktop client) |
|---|---|---|
| Form | B/S web app, used in the browser | Cross-platform desktop app (macOS / Windows / Linux) |
| Audience | Teams — multiple users and business spaces (tenants) | Individuals — single-user local workbench |
| Focus | Agent orchestration, one-click publishing, permission governance | Local-first: works directly on local projects, terminal, Git and browser |
| Extensions | MCP + SKILL + RAG, multi-vendor models, streaming chat | File / Git / terminal / browser tool panels, three permission levels |
| Modules | `king-ai-api` + `king-ai-ui` (+ `king-ai-sample`) | `king-ai-desk` |
| Dependencies | MySQL + Redis + Milvus | None (local SQLite only) |

## Features

### Cloud edition

- **Agent orchestration** — combine a chat model, system prompt, knowledge bases, MCP services and SKILLs into an Agent (multi-select binding for each resource).
- **One-click publishing** — a published Agent gets a public, login-free URL (`/#/chat/{uuid}`); admins can review visitor sessions and messages.
- **Streaming chat** — SSE output of reasoning and reply, made stateless with Redis Streams (task queue + result stream), so it works in a cluster and resumes after a disconnect or page refresh.
- **RAG knowledge bases** — documents are chunked, embedded and stored in Milvus (one collection per knowledge base, `kb_base_{id}`); relevant chunks are injected into the conversation via Spring AI `QuestionAnswerAdvisor`.
- **MCP tools** — connect remote (Streamable HTTP) and local (stdio) MCP servers, test connectivity and list tools; tools are bridged to Spring AI tool callbacks as `{mcpName}__{toolName}`.
- **SKILLs** — package domain knowledge and scripts as `SKILL.md` + a file tree, edited online and materialized on disk for the Agent (built-in samples: `sql-optimizer`, `ppt`).
- **Long-term memory** — per Agent × visitor memory, generated from history and new conversations and appended to the system prompt.
- **Multi-vendor models** — any OpenAI-compatible endpoint (DeepSeek, Zhipu GLM, Ollama, OpenCode Go, …) with vendor/model management, connectivity tests and remote model import; chat and embedding models are distinguished.
- **Space isolation and security** — data isolated by business space with per-user space grants; login via XXL-SSO (tokens stored in Redis); RBAC with menu/button-level permissions; user, system config and audit-log management.
- **i18n** — Chinese and English UI, selected by system configuration (`system.i18n.language`).

### Desk edition

- **Project as workspace** — each project is bound 1:1 to a local directory; sessions belong to a project, and per-project long-term memory is optional.
- **Multi-Agent and extension center** — configure each Agent's instructions, SKILLs, MCP servers and built-in tools in one place.
- **Observable execution** — reasoning → tool calls → reply rendered as a timeline, with parameters, status, duration, token usage and context occupancy.
- **Permission sandbox** — three levels (read-only / modify within workspace / full access), with inline approval for out-of-scope writes.
- **Tool panels** — file tree and editor, Git review, terminal (`node-pty`) and browser tabs, isolated per session.
- **Process isolation** — Renderer / Main / Worker (`utilityProcess`) three-process architecture; the Pi Agent runtime runs in the worker so long tasks never block the UI.

## Tech Stack

| Area | Stack |
|---|---|
| Backend | Java 17, Spring Boot 4.1.2, Spring AI 2.0.2 (OpenAI-compatible models, Milvus vector store, MCP client, agent utils), MyBatis 4.1, MySQL Connector/J, Redis (Lettuce), XXL-SSO 2.4.1, FreeMarker, Actuator |
| Cloud frontend | Vue 3.5, Element Plus, TypeScript, Vite 8, Pinia, Vue Router, ECharts, marked + KaTeX + highlight.js |
| Desk client | Electron 45, electron-vite, Vue 3.5, Element Plus, Pi Agent (`@earendil-works/pi-*`), better-sqlite3 + Drizzle ORM, node-pty + xterm.js, CodeMirror 6 |
| Sample MCP server | Spring AI MCP Server, annotation-based `@McpTool`, Streamable HTTP |
| Infrastructure | MySQL 8.4, Redis 8.2, Milvus 3.0 (etcd + MinIO), Attu, Docker Compose |

## Repository Layout

| Path | Description |
|---|---|
| `king-ai-api` | Cloud backend (port 8080). `business/` holds the domain modules (agent, chat, session, knowledge, mcp, skill, memory, space, supplier) and `harness/` the shared runtimes (LLM factory, chat stream, RAG, MCP, SKILL, memory); `framework/` holds login, users, roles, config, logs and web infrastructure. In production it also serves the embedded frontend from `resources/static`. |
| `king-ai-ui` | Cloud frontend (dev port 3000, hash routing), organized as `src/modules/{framework,business}/*` |
| `king-ai-desk` | Desk client; domain modules under `src/modules` (agent, chat, project, provider, mcp, skill, file, git, terminal, browser, settings, appCenter, apps/note, …) |
| `king-ai-sample` | Sample MCP server (port 8091, endpoint `/sample/mcp`) exposing `get_current_time` and `calculator` |
| `docker` | Deployment stack: `docker-compose.yml` + `.env` (mysql / redis / etcd / minio / milvus / attu / api / sample) |
| `doc` | Official documentation (Chinese), DB schema and seed data `db/tables_king_ai.sql`, screenshots |
| `.agents/skills` | Development guidelines for AI coding assistants (`king-ai` for Cloud, `king-ai-desk` for Desk); see also `AGENTS.md` |
| `ci` | GitHub Actions Maven workflow, currently disabled (`maven.yml.bak`) |

## Requirements

- JDK 17+ and Maven 3+
- Node.js 22+ (npm)
- MySQL 8.0+ and Redis 7.0+
- Milvus 2.6+ (optional; required only for RAG vectorization)
- Docker + Docker Compose (for containerized deployment)

## Quick Start (Cloud)

### 1. Initialize the database

Run `doc/db/tables_king_ai.sql` against MySQL. It creates the `king_ai` schema (16 tables) and seeds:

- users `admin` (administrator) and `user` (regular user), both with password `123456`;
- a default business space;
- model vendors OpenCode Go, Ollama, DeepSeek and Zhipu GLM with sample models (API keys left empty — fill them in the console);
- sample MCP services (the bundled `king-ai-sample`, plus Fetch / Filesystem / Playwright via `npx`) and sample SKILLs.

### 2. Configure the backend

Edit `king-ai-api/src/main/resources/application.properties`:

| Key | Default | Purpose |
|---|---|---|
| `spring.datasource.url` / `username` / `password` | `jdbc:mysql://127.0.0.1:3306/king_ai`, `root` / `root_pwd` | MySQL connection |
| `spring.data.redis.host` / `port` / `password` | `localhost` / `6379` / empty | Redis (login tokens, chat streams) |
| `king-ai.milvus.uri` | `http://127.0.0.1:19530` | Milvus vector database |
| `king-ai.chat.stream.timeout` / `ttl` | `180000` ms / `600` s | Max generation time; resume window for result streams |
| `king-ai.chat.sse.max` / `worker.count` | `64` / `4` | Per-node SSE connection limit and forwarding threads |
| `king-ai.chat.history.limit` | `50` | Recent messages sent to the model as context |
| `king-ai.skill.root` | `/data/applogs/king-ai/skills/` | SKILL materialization directory (use a shared volume in a cluster) |

Models are built at runtime from the vendor settings in the console, so Spring AI model auto-configuration is intentionally disabled.

### 3. Development mode (frontend and backend run separately)

```bash
# Backend: builds king-ai-api + king-ai-sample (tests are skipped by default)
mvn clean package
java -jar king-ai-api/target/king-ai-api-*.jar          # API on :8080
java -jar king-ai-sample/target/king-ai-sample-*.jar    # optional sample MCP server on :8091

# Frontend
cd king-ai-ui
npm install
npm run dev                                             # UI on http://localhost:3000
```

The Vite dev server proxies requests prefixed with `/api` to `VITE_API_URL` (see `king-ai-ui/.env.development`).

### 4. Production: single jar

The frontend is embedded into the API jar, so pages and APIs are served from the same port without Nginx:

```bash
cd king-ai-ui && npm install && npm run build && npm run sync:dist && cd ..
mvn clean package
java -jar king-ai-api/target/king-ai-api-*.jar
```

`npm run sync:dist` replaces `king-ai-api/src/main/resources/static` with the latest `king-ai-ui/dist`.

### 5. Production: Docker Compose

The Compose stack builds the API and sample images from local jars, so build them first:

```bash
cd king-ai-ui && npm install && npm run build && npm run sync:dist && cd ..
mvn clean package

cd docker
# Review .env first: set MYSQL_ROOT_PASSWORD / REDIS_PASSWORD and change
# MYSQL_PATH / REDIS_PATH / MILVUS_PATH to persistent directories on your host
docker compose up -d        # stop with: docker compose down
```

The schema script is applied automatically when the MySQL volume is first initialized. Exposed ports: API `8080`, sample MCP `8091`, MySQL `3306`, Redis `6379`, Milvus `19530`, MinIO `9000/9001`, Attu `3030`.

### 6. Sign in

Open `http://localhost:3000` (development) or `http://localhost:8080` (production) and sign in with `admin / 123456`. Change the default passwords before exposing the service.

A typical first run: configure a vendor API key → import models → add MCP services / SKILLs / knowledge bases → create an Agent → publish it and chat at `/#/chat/{uuid}`.

## Quick Start (Desk)

```bash
cd king-ai-desk
npm install            # postinstall downloads the Electron binary and rebuilds native modules
npm run dev            # development mode with HMR

npm run type-check     # main + renderer type checks
npm run build          # type check + build all three processes
npm run pack           # unpacked app for the current platform
npm run build:mac      # macOS arm64 dmg/zip
npm run build:win      # Windows x64 NSIS installer
```

Installers are written to `king-ai-desk/release/{version}`. On first launch, the four default vendors are seeded; set API keys under Settings → Model Providers, create a project bound to a local directory, then pick a project and Agent to start chatting. If `npm install` ran with `--ignore-scripts`, run `npx install-electron` and `npx electron-builder install-app-deps` manually.

## Documentation

- Full guide (Chinese): [`doc/King-AI-Platform官方文档.md`](doc/King-AI-Platform官方文档.md)
- Contributor and AI-assistant conventions: [`AGENTS.md`](AGENTS.md), [`.agents/skills`](.agents/skills)

## Origin and License

- Upstream repository: https://github.com/xuxueli/xxl-ai, author xuxueli (许雪里).
- License: **GNU GPL v3**, see [LICENSE](LICENSE). The original author's copyright notices are fully retained.
- This repository is a derivative work. Under GPL-3.0, if the derivative work is distributed externally (delivery beyond providing a network service), it must remain open source under GPL-3.0 with copyright notices retained; purely internal enterprise use does not trigger distribution obligations.
- Closed-source commercialization requires separate authorization from the upstream author or a different licensing arrangement.

## Roadmap

| Phase | Scope | Status |
|---|---|---|
| P0 | Rebranding: Maven coordinates / Java package names / module names / config prefixes / image names / docs and UI copy unified as King AI Platform | ✅ Done |
| P1 | Enterprise security: AK/SK + HMAC-SHA256 signature authentication, unified identity integration, audit trails, encryption of sensitive configuration | ⏳ Planned |
| P2 | Platformization: Spring Cloud Gateway / Nacos / Resilience4j / OpenFeign and an observability stack | ⏳ Planned |

> The current version keeps the native XXL tech stack (Spring Boot monolith + XXL-SSO + MySQL / Redis / Milvus) without additional middleware.
