package com.ragchatbot.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LlmClientTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".*")
    void testAskQuestion_Success() {

        LlmClient client = new LlmClient();
        String simpleQuestion = "Say 'Hello' in one word.";

        String response = client.askQuestion(simpleQuestion);

        assertNotNull(response);
        assertFalse(response.isBlank(), "Response from model should not be empty");
        assertFalse(response.startsWith("Error:"), "There should be no error: " + response);
        assertFalse(response.startsWith("API Error:"), "There should be no API error: " + response);

        System.out.println("Response from OpenAI: " + response);

        assertTrue(response.toLowerCase().contains("hello") || response.toLowerCase().contains("hi"),
                "Response should at least contain a word related to 'Hello', received: " + response);
    }
}

