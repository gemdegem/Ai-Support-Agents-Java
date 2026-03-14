package com.ragchatbot;

import com.ragchatbot.billing.BillingAgent;
import com.ragchatbot.billing.UserSession;
import com.ragchatbot.client.LlmClient;
import com.ragchatbot.memory.ConversationMemory;
import com.ragchatbot.rag.KnowledgeBase;
import com.ragchatbot.rag.TechnicalAgent;
import com.ragchatbot.rag.VectorSearch;
import com.ragchatbot.router.RouteResult;
import com.ragchatbot.router.Router;

import java.io.IOException;

public class ChatOrchestrator {

    private static final int MAX_INPUT_LENGTH = 1000;

    private final LlmClient llmClient;
    private final UserSession userSession;
    private final ConversationMemory memory;
    private final Router router;
    private final KnowledgeBase knowledgeBase;
    private final TechnicalAgent technicalAgent;
    private final BillingAgent billingAgent;

    public ChatOrchestrator(LlmClient llmClient,
                            UserSession userSession,
                            ConversationMemory memory,
                            Router router,
                            KnowledgeBase knowledgeBase,
                            TechnicalAgent technicalAgent,
                            BillingAgent billingAgent) {
        this.llmClient = llmClient;
        this.userSession = userSession;
        this.memory = memory;
        this.router = router;
        this.knowledgeBase = knowledgeBase;
        this.technicalAgent = technicalAgent;
        this.billingAgent = billingAgent;
    }

    public ChatOrchestrator() {
        this.llmClient = new LlmClient();
        this.userSession = new UserSession();
        this.memory = new ConversationMemory(8);
        this.router = new Router(llmClient);
        this.knowledgeBase = new KnowledgeBase(llmClient);
        VectorSearch vectorSearch = new VectorSearch(knowledgeBase, llmClient);
        this.technicalAgent = new TechnicalAgent(llmClient, vectorSearch);
        this.billingAgent = new BillingAgent(llmClient, userSession);
    }

    public void initialize() throws IOException, InterruptedException {
        System.out.println("[Orchestrator] Initializing knowledge base...");
        knowledgeBase.initialize();
        System.out.println("[Orchestrator] System ready.");
    }

    public String processMessage(String userMessage) {

        String sanitized = sanitizeInput(userMessage);

        memory.addUserMessage(sanitized);

        RouteResult route = router.route(memory);
        System.out.println("[Orchestrator] Router -> " + route);

        String response;
        String tag;

        switch (route) {
            case TECHNICAL -> {
                response = technicalAgent.handle(memory);
                tag = "[Agent: Technical]";
            }
            case BILLING -> {
                response = billingAgent.handle(memory);
                tag = "[Agent: Billing]";
            }
            case GENERAL -> {
                response = Router.GENERAL_MESSAGE;
                tag = "[Agent: General]";
            }
            default -> {

                response = Router.FALLBACK_MESSAGE;
                tag = "[Agent: Fallback]";
            }
        }

        String cleanResponse = response;
        if (tag != null && response.startsWith(tag)) {
            cleanResponse = response.substring(tag.length()).trim();
        }

        memory.addAssistantMessage(cleanResponse, tag);

        return response;
    }

    String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        String trimmed = input.trim();
        if (trimmed.length() > MAX_INPUT_LENGTH) {
            trimmed = trimmed.substring(0, MAX_INPUT_LENGTH);
        }
        return trimmed;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    static int getMaxInputLength() {
        return MAX_INPUT_LENGTH;
    }
}

