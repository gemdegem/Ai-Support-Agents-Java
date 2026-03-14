package com.ragchatbot.rag;

import com.ragchatbot.client.LlmClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VectorSearch {

    private final KnowledgeBase knowledgeBase;
    private final LlmClient llmClient;

    public VectorSearch(KnowledgeBase knowledgeBase, LlmClient llmClient) {
        this.knowledgeBase = knowledgeBase;
        this.llmClient = llmClient;
    }

    public List<String> search(String query, int topK) throws IOException, InterruptedException {

        double[] queryEmbedding = llmClient.getEmbedding(query);

        List<KnowledgeBase.DocumentChunk> chunks = knowledgeBase.getChunks();
        List<ScoredChunk> scoredChunks = new ArrayList<>();

        for (KnowledgeBase.DocumentChunk chunk : chunks) {
            double similarity = cosineSimilarity(queryEmbedding, chunk.embedding());
            scoredChunks.add(new ScoredChunk(chunk.text(), similarity));
        }

        scoredChunks.sort((a, b) -> Double.compare(b.score(), a.score()));

        List<String> results = new ArrayList<>();
        for (int i = 0; i < Math.min(topK, scoredChunks.size()); i++) {
            results.add(scoredChunks.get(i).text());
        }

        return results;
    }

    public static double cosineSimilarity(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException(
                    "Vectors must have the same length: " + a.length + " vs " + b.length);
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        double denominator = Math.sqrt(normA) * Math.sqrt(normB);
        if (denominator == 0.0) {
            return 0.0;
        }

        return dotProduct / denominator;
    }

    private record ScoredChunk(String text, double score) {}
}

