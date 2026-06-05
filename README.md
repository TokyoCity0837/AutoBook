
# 📚 AutoBook

**A creative writing platform combining a social network with a book library.**

AutoBook allows authors to write, publish, and share their work while connecting with readers through a social feed. An integrated AI microservice analyzes each author's unique writing style and assists with text generation.

---

## 🛠 Tech Stack

| Layer | Technologies |
|---|---|
| Backend | Java 21, Spring Boot 3, JPA / Hibernate, MySQL |
| Frontend | React, TypeScript, Axios |
| AI Microservice | Python, FastAPI, spaCy, Llama3 (via Ollama) |
| Testing | JUnit 5, Mockito, JaCoCo |
| Auth | Spring Security, OAuth2 (Google) |

---

## 🏗 Architecture

The project is split into three independent modules:

```
AutoBook/
├── src/          # Java Spring Boot backend
├── frontend/     # React + TypeScript frontend
└── ai-microservice/  # Python FastAPI AI service
```

### Backend — Layered Architecture
```
Controller  →  REST API, HTTP requests
Service     →  Business logic, validation, logging
Factory     →  Entity creation, default values
Repository  →  DB access via JPA / Hibernate
```

### Frontend — Clean Architecture
```
presentation/   React pages, components, hooks
data/           apiClient (Axios) + 11 repositories
domain/         TypeScript models: Book, User, Post, Chapter...
```

---

## ✨ Features

- 📖 **Book library** — create books, chapters with a WYSIWYG A4 editor
- 🌐 **Social feed** — posts, likes, reposts, follows, comments
- 🔐 **Authentication** — login/register + Google OAuth2
- 💾 **Saved items** — bookmark books and chapters
- ✏️ **Edit requests** — collaborative editing workflow
- 🤖 **AI writing assistant** — style analysis and text generation

---

## 🤖 AI Microservice

The AI service analyzes an author's writing style using NLP and assists with writing.

**Endpoints:**

| Endpoint | Description |
|---|---|
| `POST /api/v1/analyze` | Analyzes text → full author style profile |
| `POST /api/v1/suggest` | Suggests words and phrases in the author's style |
| `POST /api/v1/generate` | Generates text continuation via Llama3 |
| `POST /api/v1/vocabulary` | Enriches the author's vocabulary |
| `POST /api/v1/consistency` | Checks stylistic consistency |

**How it works:**
1. Text is split into 500-word chunks and processed by spaCy
2. Tokenization, POS-tagging, and lemmatization extract linguistic features
3. TF-IDF builds a unique style signature (style words + thematic words)
4. Llama3 (local, no cloud, no API keys) generates continuations matching the author's voice

---

## 🧪 Testing

| Group | Classes |
|---|---|
| ServiceTest | 12 |
| ControllerTest | 11 |
| FactoryTest | 7 |
| MapperTest | 6 |
| ConfigTest | 7 |
| Other | 7+ |
| **Total** | **50+** |

**JaCoCo line coverage: 81%**

---

## 🔑 OOP Concepts Used

- **Inheritance** — 12 custom exceptions, `CustomOAuth2User`
- **Interfaces & Generics** — `GenericMapper<E, D>` for 3 mapper hierarchies
- **Polymorphism** — run-time dispatch + compile-time overloading
- **Encapsulation** — private fields, Service layer, DTOs
- **Factory Pattern** — 7 Factory classes
- **Multithreading** — `@Async` thread pool for background tasks
- **Reflection** — `ReflectionUtil.inspectObjectFields()`
- **Serialization** — `ObjectOutputStream` entity backup
- **Lambda & Stream API** — `map`, `filter`, `orElseThrow`, `sorted`

---

## 🚀 Getting Started

### Prerequisites
- Java 21
- Node.js 18+
- Docker & Docker Compose
- Python 3.10+
- [Ollama](https://ollama.com/) with Llama3 model pulled

### Run with Docker Compose

```bash
git clone https://github.com/TokyoCity0837/AutoBook.git
cd AutoBook
docker-compose up
```

### Run manually

**Backend:**
```bash
./gradlew bootRun
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

**AI Microservice:**
```bash
cd ai-microservice
pip install -r requirements.txt
uvicorn main:app --reload
```

---

## 👥 Authors

- **Andrii Dosyn** — [@Dosinn](https://github.com/Dosinn)
- **Anton Hrimov** — [@TokyoCity0837](https://github.com/TokyoCity0837)
