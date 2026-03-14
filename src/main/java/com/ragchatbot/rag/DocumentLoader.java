package com.ragchatbot.rag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DocumentLoader {

    private static final int MIN_CHUNK_LENGTH = 30;

    private static final String[] DEFAULT_DOC_FILES = {
            "docs/system-overview.txt",
            "docs/api-setup.txt",
            "docs/troubleshooting.txt",
            "docs/security-guide.txt",
            "docs/integration-guide.txt"
    };

    private final String[] docFiles;

    public DocumentLoader() {
        this(DEFAULT_DOC_FILES);
    }

    public DocumentLoader(String[] docFiles) {
        this.docFiles = docFiles;
    }

    public List<String> loadAndChunk() throws IOException {
        List<String> allChunks = new ArrayList<>();

        for (String docFile : docFiles) {
            String content = loadResource(docFile);
            List<String> chunks = splitIntoChunks(content);
            allChunks.addAll(chunks);
        }

        return allChunks;
    }

    private String loadResource(String resourcePath) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            }
        }
    }

    private List<String> splitIntoChunks(String content) {
        List<String> chunks = new ArrayList<>();

        String[] paragraphs = content.split("\\n\\s*\\n");

        for (String paragraph : paragraphs) {
            String trimmed = paragraph.trim();
            if (trimmed.length() >= MIN_CHUNK_LENGTH) {
                chunks.add(trimmed);
            }
        }

        return chunks;
    }
}

