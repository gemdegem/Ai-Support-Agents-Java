package com.ragchatbot.rag;

import com.ragchatbot.client.LlmClient;
import com.ragchatbot.memory.ConversationMemory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TechnicalAgentTest {

    private TechnicalAgent createAgent() throws Exception {
        LlmClient llmClient = new LlmClient();
        KnowledgeBase kb = new KnowledgeBase(llmClient);
        kb.initialize();
        VectorSearch search = new VectorSearch(kb, llmClient);
        return new TechnicalAgent(llmClient, search);
    }

    @Test
    public void testHandle_relevantQuestion_returnsAnswer() throws Exception {
        TechnicalAgent agent = createAgent();
        ConversationMemory memory = new ConversationMemory(6);

        String question = "What is the rate limit on Pro plan?";
        System.out.println("USER: " + question);
        memory.addUserMessage(question);

        String response = agent.handle(memory);
        System.out.println("AGENT A: " + response);

        assertNotNull(response, "Response should not be null");
        assertTrue(response.length() > 10, "Response should contain an explanation");

        assertTrue(response.toLowerCase().contains("1000") || response.toLowerCase().contains("pro"),
                "Response should contain information about Pro plan (1000 requests/min)");
    }

    @Test
    public void testHandle_irrelevantQuestion_refusesToAnswer() throws Exception {
        TechnicalAgent agent = createAgent();
        ConversationMemory memory = new ConversationMemory(6);

        String question = "What is the recipe for borscht?";
        System.out.println("USER: " + question);
        memory.addUserMessage(question);

        String response = agent.handle(memory);
        System.out.println("AGENT A (refusal): " + response);

        assertNotNull(response, "Response should not be null");

        assertTrue(
                response.toLowerCase().contains("unfortunately") ||
                response.toLowerCase().contains("couldn't find") ||
                response.toLowerCase().contains("contact technical support"),
                "Agent should refuse to answer a question outside documentation. Response: " + response
        );
    }
}

