package com.ragchatbot.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ragchatbot.memory.ConversationMemory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LlmClient {

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String EMBEDDINGS_URL = "https://openrouter.ai/api/v1/embeddings";
    private static final String EMBEDDINGS_MODEL = "openai/text-embedding-3-small";

    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public LlmClient() {
        this.apiKey = System.getenv("OPENAI_API_KEY");
        if (this.apiKey == null || this.apiKey.trim().isEmpty()) {
            throw new IllegalStateException("Missing environment variable OPENAI_API_KEY");
        }
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.model = "openai/gpt-4o-mini";
    }

    public LlmClient(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String askQuestion(String question) {
        try {

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", this.model);

            ArrayNode messagesArray = requestBody.putArray("messages");
            ObjectNode messageNode = objectMapper.createObjectNode();
            messageNode.put("role", "user");
            messageNode.put("content", question);
            messagesArray.add(messageNode);

            String requestBodyString = objectMapper.writeValueAsString(requestBody);

            return sendRequest(requestBodyString);

        } catch (IOException | InterruptedException e) {
            return handleException(e);
        }
    }

    public String chat(List<ConversationMemory.Message> messages) {
        try {
            ObjectNode requestBody = buildRequestBody(messages, null);
            String requestBodyString = objectMapper.writeValueAsString(requestBody);
            return sendRequest(requestBodyString);
        } catch (IOException | InterruptedException e) {
            return handleException(e);
        }
    }

    public JsonNode chatWithTools(List<ConversationMemory.Message> messages, ArrayNode tools) {
        try {
            ObjectNode requestBody = buildRequestBody(messages, tools);
            String requestBodyString = objectMapper.writeValueAsString(requestBody);
            return sendRequestRaw(requestBodyString);
        } catch (IOException | InterruptedException e) {
            handleException(e);
            return null;
        }
    }

    public JsonNode chatRaw(ArrayNode messagesArray, ArrayNode tools) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", this.model);
            requestBody.set("messages", messagesArray);

            if (tools != null && !tools.isEmpty()) {
                requestBody.set("tools", tools);
            }

            String requestBodyString = objectMapper.writeValueAsString(requestBody);
            return sendRequestRaw(requestBodyString);
        } catch (IOException | InterruptedException e) {
            handleException(e);
            return null;
        }
    }

    private ObjectNode buildRequestBody(List<ConversationMemory.Message> messages, ArrayNode tools) {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", this.model);

        ArrayNode messagesArray = requestBody.putArray("messages");
        for (ConversationMemory.Message msg : messages) {
            ObjectNode messageNode = objectMapper.createObjectNode();
            messageNode.put("role", msg.getRole());
            messageNode.put("content", msg.getContent());
            messagesArray.add(messageNode);
        }

        if (tools != null && !tools.isEmpty()) {
            requestBody.set("tools", tools);
        }

        return requestBody;
    }

    private String sendRequest(String requestBodyString) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + this.apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && !choicesNode.isEmpty()) {
                JsonNode messageContentNode = choicesNode.get(0).path("message").path("content");
                return messageContentNode.asText();
            } else {
                return "Error: No response from model in correct format.";
            }
        } else {
            return "API Error: Header status - " + response.statusCode() + " | Body: " + response.body();
        }
    }

    private JsonNode sendRequestRaw(String requestBodyString) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + this.apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && !choicesNode.isEmpty()) {
                return choicesNode.get(0).path("message");
            }
        }
        System.err.println("API Error (raw): Status " + response.statusCode() + " | Body: " + response.body());
        return null;
    }

    private String handleException(Exception e) {
        System.err.println("Error during communication with OpenAI API: " + e.getMessage());
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        return "An exception occurred during communication with API.";
    }

    public double[] getEmbedding(String text) throws IOException, InterruptedException {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", EMBEDDINGS_MODEL);
        requestBody.put("input", text);

        String requestBodyString = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EMBEDDINGS_URL))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + this.apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Embeddings API error: status " + response.statusCode()
                    + " | Body: " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode embeddingArray = root.path("data").get(0).path("embedding");

        double[] vector = new double[embeddingArray.size()];
        for (int i = 0; i < embeddingArray.size(); i++) {
            vector[i] = embeddingArray.get(i).asDouble();
        }
        return vector;
    }

    public List<double[]> getEmbeddings(List<String> texts) throws IOException, InterruptedException {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", EMBEDDINGS_MODEL);

        ArrayNode inputArray = requestBody.putArray("input");
        for (String text : texts) {
            inputArray.add(text);
        }

        String requestBodyString = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EMBEDDINGS_URL))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + this.apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Embeddings API error: status " + response.statusCode()
                    + " | Body: " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode dataArray = root.path("data");

        List<double[]> results = new ArrayList<>();
        for (JsonNode dataItem : dataArray) {
            JsonNode embeddingArray = dataItem.path("embedding");
            double[] vector = new double[embeddingArray.size()];
            for (int i = 0; i < embeddingArray.size(); i++) {
                vector[i] = embeddingArray.get(i).asDouble();
            }
            results.add(vector);
        }
        return results;
    }
}

