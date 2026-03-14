package com.ragchatbot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainTest {

    @Test
    void testJacksonIsAvailable() {
        assertDoesNotThrow(() -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.createObjectNode().put("test", "value");
        }, "Jackson (ObjectMapper) should be available in classpath");
    }

    @Test
    void testChatOrchestratorCanBeInstantiatedWithStubs() {

        assertDoesNotThrow(() -> {
            var llmClient = new com.ragchatbot.client.LlmClient("fake-key", "fake-model");
            var session = new com.ragchatbot.billing.UserSession("C001");
            var memory = new com.ragchatbot.memory.ConversationMemory(8);
            var router = new com.ragchatbot.router.Router(llmClient);
            var kb = new com.ragchatbot.rag.KnowledgeBase(llmClient);
            var vs = new com.ragchatbot.rag.VectorSearch(kb, llmClient);
            var ta = new com.ragchatbot.rag.TechnicalAgent(llmClient, vs);
            var ba = new com.ragchatbot.billing.BillingAgent(llmClient, session);

            new ChatOrchestrator(llmClient, session, memory, router, kb, ta, ba);
        }, "ChatOrchestrator should be instantiable with components");
    }
}

