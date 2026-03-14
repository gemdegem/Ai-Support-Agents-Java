package com.ragchatbot.billing;

import com.ragchatbot.client.LlmClient;
import com.ragchatbot.memory.ConversationMemory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BillingAgentIntegrationTest {

    @Test
    public void testFullToolCallingLoop_withoutUserProvidingId() {

        UserSession session = new UserSession("C001");
        LlmClient llmClient = new LlmClient();
        BillingAgent agent = new BillingAgent(llmClient, session);
        ConversationMemory memory = new ConversationMemory(6);

        String userMessage = "I would like to request a refund, the reason is a double charge on my account.";
        System.out.println("SESSION: Logged in as " + session.getCustomerName() + " (" + session.getCustomerId() + ")");
        System.out.println("USER: " + userMessage);
        memory.addUserMessage(userMessage);

        String response = agent.handle(memory);
        System.out.println("AGENT B FINAL RESPONSE: " + response);

        assertNotNull(response);
        assertTrue(response.length() > 10, "Response should contain an explanation after calling a tool");

        assertTrue(response.toLowerCase().contains("refund") || response.toLowerCase().contains("ticket") || response.toLowerCase().contains("ref-"),
            "Response should refer to the refund request");
    }
}

