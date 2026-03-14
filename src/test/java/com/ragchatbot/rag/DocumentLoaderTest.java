package com.ragchatbot.rag;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentLoaderTest {

    @Test
    public void testLoadAndChunk_returnsNonEmptyChunks() throws IOException {
        DocumentLoader loader = new DocumentLoader();
        List<String> chunks = loader.loadAndChunk();

        assertFalse(chunks.isEmpty(), "Chunks list should not be empty");
        System.out.println("[DocumentLoaderTest] Loaded " + chunks.size() + " chunks.");

        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            assertFalse(chunk.isBlank(), "Chunk " + i + " should not be empty");
            System.out.println("  Chunk " + i + " (" + chunk.length() + " chars): "
                    + chunk.substring(0, Math.min(80, chunk.length())) + "...");
        }
    }

    @Test
    public void testLoadAndChunk_chunksHaveMinLength() throws IOException {
        DocumentLoader loader = new DocumentLoader();
        List<String> chunks = loader.loadAndChunk();

        for (String chunk : chunks) {
            assertTrue(chunk.length() >= 30,
                    "Each chunk should have at least 30 characters, but has: " + chunk.length());
        }
    }
}

