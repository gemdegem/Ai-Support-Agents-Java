package com.ragchatbot.router;

import com.ragchatbot.client.LlmClient;
import com.ragchatbot.memory.ConversationMemory;

import java.util.List;

public class Router {

    private static final String ROUTER_SYSTEM_PROMPT = """
            You are an intent classifier. Analyze the short conversation history below \
            to understand context, but identify the intent ONLY \
            in the user's most recent message.

            Respond with ONE word:
            - TECHNICAL — if the question is about technical support, documentation, API, the platform, configuration, \
            software issues, errors, or settings. All questions asking "how" or "what is the API" are TECHNICAL.
            - BILLING — if the question is about payments, invoices, pricing plans, refunds, \
            subscriptions, or billing account.
            - GENERAL — ONLY if the message is a direct greeting (e.g., "hello", "hi") or a meta-question about \
            your capabilities (e.g., "what can you help me with?", "what do you do?"). Do NOT use GENERAL for anything else.

            Respond with ONLY one word. No extra characters, sentences, or explanations.""";

    public static final String FALLBACK_MESSAGE =
            "I'm sorry, but I can't help with that topic. " +
            "Please contact general support.";

    public static final String GENERAL_MESSAGE =
            "I can help you with two areas:\n\n" +
            "1. **Technical support** — API configuration, troubleshooting errors " +
            "(401, 403, 429, 500), webhooks, integrations (HubSpot, Zapier), " +
            "security best practices, and data export.\n\n" +
            "2. **Billing & account** — your current subscription plan and pricing, " +
            "invoice and payment history, and submitting refund requests.\n\n" +
            "Just ask your question and I'll route it to the right specialist!";

    private final LlmClient llmClient;

    public Router(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    public String classify(ConversationMemory memory) {

        ConversationMemory routerMemory = new ConversationMemory(4);
        routerMemory.setSystemPrompt(ROUTER_SYSTEM_PROMPT);

        List<ConversationMemory.Message> allMessages = memory.getMessagesForLlm();
        for (ConversationMemory.Message msg : allMessages) {
            if (!"system".equals(msg.getRole())) {
                if ("user".equals(msg.getRole())) {
                    routerMemory.addUserMessage(msg.getContent());
                } else if ("assistant".equals(msg.getRole())) {
                    routerMemory.addAssistantMessage(msg.getContent());
                }
            }
        }

        return llmClient.chat(routerMemory.getMessagesForLlm());
    }

    public RouteResult route(ConversationMemory memory) {
        String rawResponse = classify(memory);
        String cleaned = rawResponse.trim().toUpperCase();

        return switch (cleaned) {
            case "TECHNICAL" -> RouteResult.TECHNICAL;
            case "BILLING"   -> RouteResult.BILLING;
            case "GENERAL"   -> RouteResult.GENERAL;
            default          -> RouteResult.FALLBACK;
        };
    }
}

