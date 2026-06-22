# HireSync AI 🎯

> **Enterprise-grade Job Hunt Tracking System** built with Spring Boot microservices, LangChain4j AI integration, Apache Kafka, pgvector semantic search, and React 18.

[![Build Status](https://github.com/itsmehotpants/hiresync-ai/actions/workflows/ci.yml/badge.svg)](https://github.com/itsmehotpants/hiresync-ai/actions)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      React Frontend (3000)                   │
└──────────────────────────┬──────────────────────────────────┘
                           │ REST + SSE
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
  ┌──────────┐      ┌──────────┐      ┌──────────────┐
  │  Core    │      │    AI    │      │Notification  │
  │ Service  │      │ Service  │      │  + Analytics │
  │  :8081   │      │  :8082   │      │    :8083     │
  └────┬─────┘      └────┬─────┘      └──────┬───────┘
       │                 │                   │
       └─────────────────┴──────────┬────────┘
                                    │ Apache Kafka (KRaft)
       ┌────────────────────────────┼────────────────────┐
       │                            │                    │
       ▼                            ▼                    ▼
  ┌──────────┐              ┌──────────────┐      ┌──────────┐
  │PostgreSQL│              │    Redis 7   │      │  Zipkin  │
  │16+pgvect │              │    Cache     │      │ Tracing  │
  └──────────┘              └──────────────┘      └──────────┘
```

## ✨ Features

| Feature | Description |
|---------|-------------|
| 📋 **Kanban Board** | Drag-and-drop job application tracker across all stages |
| 🤖 **AI Resume Analysis** | ATS scoring, keyword matching, section-by-section feedback |
| 💬 **AI Chat Agent** | Conversational career coach with tool calling & real data |
| 📄 **Cover Letter Generator** | Tone-aware, role-specific cover letters via LLM |
| 🔍 **JD Matcher** | Semantic similarity between your resume and any job description |
| 📊 **Analytics Dashboard** | Funnel conversion, ghost rate, response rate charts |
| 🔔 **Smart Notifications** | Ghost alerts, interview reminders, offer celebrations |
| 📁 **Resume Vault** | Multi-version resume storage with AI scoring per job |
| 👥 **Contact CRM** | Track recruiters, hiring managers, and outreach messages |
| 🎯 **Interview Prep** | AI-generated question banks based on your interview history |

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Spring Boot 3.3, Java 21, Virtual Threads |
| **AI** | LangChain4j 1.0.0-beta, Google Gemini 1.5 Flash (free) |
| **Embeddings** | Google text-embedding-004 via LangChain4j |
| **Vector DB** | PostgreSQL 16 + pgvector extension |
| **Database** | PostgreSQL 16 with Flyway migrations |
| **Cache** | Redis 7.2 |
| **Messaging** | Apache Kafka 3.7 (KRaft — no Zookeeper) |
| **Auth** | Spring Security 6 + JWT (JJWT 0.12) |
| **Frontend** | React 18, Vite, TypeScript, TailwindCSS, TanStack Query |
| **Observability** | Micrometer Tracing + Zipkin + Spring Actuator |
| **Email** | MailHog (local) / Resend.com (production, free tier) |

## 🚀 Quick Start

### Prerequisites
- Java 21+, Maven 3.9+, Docker Desktop, Node.js 20+

### 1. Clone and configure
```bash
git clone https://github.com/itsmehotpants/hiresync-ai.git
cd hiresync-ai
cp .env.example .env
# Edit .env with your Gemini API key
```

### 2. Start infrastructure
```bash
docker compose up -d
```

### 3. Run backend services
```bash
# Terminal 1 — Core Service
mvn -pl hiresync-core spring-boot:run

# Terminal 2 — AI Service
mvn -pl hiresync-ai spring-boot:run

# Terminal 3 — Notification Service
mvn -pl hiresync-notification spring-boot:run
```

### 4. Start frontend
```bash
cd hiresync-frontend
npm install && npm run dev
```

### 5. Open the app
- 🌐 **App**: http://localhost:3000
- 📖 **API Docs**: http://localhost:8081/swagger-ui.html
- 📨 **MailHog**: http://localhost:8025
- 🔍 **Kafka UI**: http://localhost:9090
- 🔭 **Zipkin**: http://localhost:9411

## 📁 Project Structure

```
hiresync-ai/
├── docker-compose.yml          # Full infrastructure stack
├── .env.example                # Environment template (safe to commit)
├── pom.xml                     # Parent Maven POM
├── hiresync-common/            # Shared DTOs, events, exceptions
├── hiresync-core/              # Core Service (auth, jobs, resumes)
├── hiresync-ai/                # AI Service (LangChain4j, RAG, chat)
├── hiresync-notification/      # Notification + Analytics Service
└── hiresync-frontend/          # React SPA
```

## 🔐 Environment Variables

See [`.env.example`](.env.example) for all required variables. **Never commit `.env`.**

## 📝 License

MIT License — see [LICENSE](LICENSE) for details.

---

Built with ❤️ by [@itsmehotpants](https://github.com/itsmehotpants)
