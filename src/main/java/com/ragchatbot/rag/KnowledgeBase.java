package com.ragchatbot.rag;

import com.ragchatbot.client.LlmClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KnowledgeBase {

    public record DocumentChunk(String text, double[] embedding) {}

    private final LlmClient llmClient;
    private final DocumentLoader documentLoader;
    private final List<DocumentChunk> chunks;

    public KnowledgeBase(LlmClient llmClient) {
        this(llmClient, new DocumentLoader());
    }

    public KnowledgeBase(LlmClient llmClient, DocumentLoader documentLoader) {
        this.llmClient = llmClient;
        this.documentLoader = documentLoader;
        this.chunks = new ArrayList<>();
    }

    public void initialize() throws IOException, InterruptedException {

        List<String> rawChunks = documentLoader.loadAndChunk();
        System.out.println("[KnowledgeBase] Loaded " + rawChunks.size() + " documentation fragments.");

        List<double[]> embeddings = llmClient.getEmbeddings(rawChunks);
        System.out.println("[KnowledgeBase] Generated " + embeddings.size() + " embedding vectors.");

        chunks.clear();
        for (int i = 0; i < rawChunks.size(); i++) {
            chunks.add(new DocumentChunk(rawChunks.get(i), embeddings.get(i)));
        }

        System.out.println("[KnowledgeBase] Initialization completed successfully.");
    }

    public List<DocumentChunk> getChunks() {
        return chunks;
    }

    public boolean isInitialized() {
        return !chunks.isEmpty();
    }

    public int size() {
        return chunks.size();
    }
}

