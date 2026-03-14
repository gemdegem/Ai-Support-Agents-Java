# RAG Chatbot — Conversational AI Support Agents

A conversational support system featuring two AI agents collaborating within a single conversation. The system dynamically routes user messages to the most appropriate agent based on user intent.

## Agents

### Agent A — Technical Specialist (RAG)
Answers technical questions using Retrieval-Augmented Generation (RAG) based on local documentation files. The agent retrieves the most relevant documentation sections, passes them to the LLM, and generates answers **only** from the provided context. If the answer is not in the documentation, the agent clearly states that the information is unavailable.

### Agent B — Billing Specialist (Tool Calling)
Handles billing-related questions using a manual Tool Calling loop. The agent can:
- Check the customer's plan and pricing (`checkPlanAndPricing`)
- Open a refund request (`openRefundRequest`)
- Retrieve billing history (`getBillingHistory`)
- Identify the currently logged-in customer (`getCurrentCustomerId`)

**Security:** The customer ID is injected server-side from the user session — the LLM has no control over which account is accessed.

## Architecture

```
User Input
    │
    ▼
┌────────────────────┐
│   Input Sanitizer  │  (max 1000 chars)
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│      Router        │  LLM classifies intent → TECHNICAL / BILLING / GENERAL
└────────┬───────────┘
         │
    ┌────┴────┐
    │  Java   │  Deterministic switch (hard fallback)
    │ switch  │
    └────┬────┘
    ┌────┼──────┼────────┐
    ▼    ▼      ▼        ▼
Agent A  Agent B  General  Fallback
 (RAG)  (Tools)  (static)  (static)
```

Key design principles:
- **No agentic frameworks** — pure Java orchestration (no LangChain, Spring AI, etc.)
- **Sliding Window Memory** — last 6-8 messages, anchored system prompt
- **Context Isolation** — agent tags prevent context poisoning
- **Hard Fallback** — unrecognized router output defaults to safe static message

## Requirements

- **Java 25** (latest)
- **Maven 3.8+**
- **API Key** — OpenRouter-compatible API key

## Setup

### 1. Set your API key

The application uses **OpenRouter** by default to access LLMs (which requires an OpenRouter API key).

```bash
export OPENAI_API_KEY="your-openrouter-key-here"
```

> **Note for pure OpenAI users:** 
> If you want to use a direct native OpenAI API key (`sk-...`), you need to change the `API_URL` and `EMBEDDINGS_URL` constants in `src/main/java/com/ragchatbot/client/LlmClient.java` to point to `https://api.openai.com/...`.

### 2. Build and run

```bash
mvn clean compile exec:java
```

The application will:
1. Load and vectorize documentation files (one-time at startup)
2. Randomly select a simulated logged-in customer (C001/C002/C003)
3. Start an interactive console chat loop

### 3. Run tests

```bash
mvn test
```

## Project Structure

```
src/main/java/com/ragchatbot/
├── Main.java                  # Entry point — console chat loop
├── ChatOrchestrator.java      # Main orchestration (Router → Agent A/B)
├── client/
│   └── LlmClient.java        # HTTP client for LLM API (Chat + Embeddings)
├── memory/
│   └── ConversationMemory.java # Sliding Window + role tagging
├── router/
│   ├── Router.java            # Intent classifier (LLM-based)
│   └── RouteResult.java       # Enum: TECHNICAL / BILLING / GENERAL / FALLBACK
├── rag/
│   ├── DocumentLoader.java    # Loads docs from classpath, splits into chunks
│   ├── KnowledgeBase.java     # In-memory vector store (text + embedding)
│   ├── VectorSearch.java      # Cosine similarity search
│   └── TechnicalAgent.java    # Agent A — RAG-based responses
└── billing/
    ├── BillingAgent.java      # Agent B — Tool Calling loop
    ├── BillingService.java    # Mocked billing backend
    ├── ToolRegistry.java      # JSON Schema definitions for tools
    ├── ToolExecutor.java      # Dispatches tool calls securely
    └── UserSession.java       # Simulated logged-in user session

src/main/resources/docs/       # Technical documentation for RAG
├── system-overview.txt        # Platform overview, key capabilities, plans
├── api-setup.txt              # API configuration guide
├── troubleshooting.txt        # Troubleshooting FAQ
├── security-guide.txt         # Security best practices
└── integration-guide.txt      # Integration guide (HubSpot, Webhooks)
```

## Documentation Files

The `src/main/resources/docs/` directory contains technical documentation used by Agent A:

| File | Content |
|------|---------|
| `system-overview.txt` | Platform overview, key capabilities, subscription plans |
| `api-setup.txt` | API setup, authentication, rate limits, versioning |
| `troubleshooting.txt` | Common errors (401, 403, 429, 500) and resolutions |
| `security-guide.txt` | Key management, scopes, webhook security, attack protection |
| `integration-guide.txt` | HubSpot integration, webhooks, Zapier, data export |

## Example Conversations

See [example_conversations.txt](example_conversations.txt) for sample conversations demonstrating the system's capabilities.

### Screenshots

**Conversation 1: Dynamic Agent Switching (Technical → Billing → Technical)**
![Conversation 1](images/conv1.png)

**Conversation 2: Multi-turn Tool Calling (Refund Request)**
![Conversation 2](images/conv2.png)

**Conversation 3: Out-of-Scope Handling & Prompt Injection Defense**
![Conversation 3](images/conv3.png)
**Conversation 4: Switching between technical and billing support**
![Conversation 4](images/conv4.png)
![Conversation 4](images/conv5.png)
