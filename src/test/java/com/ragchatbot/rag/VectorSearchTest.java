package com.ragchatbot.rag;

import com.ragchatbot.client.LlmClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VectorSearchTest {

    @Test
    public void testSearch_returnsRelevantChunks() throws Exception {

        LlmClient llmClient = new LlmClient();
        KnowledgeBase kb = new KnowledgeBase(llmClient);
        kb.initialize();

        VectorSearch search = new VectorSearch(kb, llmClient);

        String query = "How to configure API key?";
        List<String> results = search.search(query, 3);

        System.out.println("[VectorSearchTest] Query: \"" + query + "\"");
        System.out.println("[VectorSearchTest] Found " + results.size() + " chunks:");
        for (int i = 0; i < results.size(); i++) {
            System.out.println("  Top " + (i + 1) + ": " + results.get(i).substring(0, Math.min(100, results.get(i).length())) + "...");
        }

        assertFalse(results.isEmpty(), "Search results should not be empty");
        assertEquals(3, results.size(), "Exactly 3 chunks should be returned");
    }

    @Test
    public void testCosineSimilarity_identicalVectors() {
        double[] a = {1.0, 2.0, 3.0};
        double[] b = {1.0, 2.0, 3.0};
        double similarity = VectorSearch.cosineSimilarity(a, b);
        assertEquals(1.0, similarity, 0.0001, "Identical vectors should have similarity = 1.0");
    }

    @Test
    public void testCosineSimilarity_orthogonalVectors() {
        double[] a = {1.0, 0.0};
        double[] b = {0.0, 1.0};
        double similarity = VectorSearch.cosineSimilarity(a, b);
        assertEquals(0.0, similarity, 0.0001, "Orthogonal vectors should have similarity = 0.0");
    }

    @Test
    public void testCosineSimilarity_oppositeVectors() {
        double[] a = {1.0, 0.0};
        double[] b = {-1.0, 0.0};
        double similarity = VectorSearch.cosineSimilarity(a, b);
        assertEquals(-1.0, similarity, 0.0001, "Opposite vectors should have similarity = -1.0");
    }
}

