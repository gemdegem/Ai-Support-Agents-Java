package com.ragchatbot.rag;

import com.ragchatbot.client.LlmClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KnowledgeBaseTest {

    @Test
    public void testInitialize_loadsAndVectorizesChunks() throws Exception {
        LlmClient llmClient = new LlmClient();
        KnowledgeBase kb = new KnowledgeBase(llmClient);

        kb.initialize();

        assertTrue(kb.isInitialized(), "Knowledge base should be initialized");
        assertTrue(kb.size() > 0, "There should be more than 0 indexed chunks");
        System.out.println("[KnowledgeBaseTest] Indexed " + kb.size() + " chunks.");

        for (KnowledgeBase.DocumentChunk chunk : kb.getChunks()) {
            assertNotNull(chunk.text(), "Chunk text should not be null");
            assertNotNull(chunk.embedding(), "Chunk embedding should not be null");
            assertTrue(chunk.embedding().length > 0, "Vector should have dimensions");
            System.out.println("  Chunk (" + chunk.text().length() + " chars, "
                    + chunk.embedding().length + " dimensions): "
                    + chunk.text().substring(0, Math.min(60, chunk.text().length())) + "...");
        }
    }
}

