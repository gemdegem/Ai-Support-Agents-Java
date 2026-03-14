package com.ragchatbot.memory;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConversationMemoryTest {

    @Test
    void testSlidingWindow() {

        ConversationMemory memory = new ConversationMemory(3);
        memory.setSystemPrompt("You are a helpful assistant.");

        memory.addUserMessage("Message 1");
        memory.addAssistantMessage("Response 1");
        memory.addUserMessage("Message 2");
        memory.addAssistantMessage("Response 2");
        memory.addUserMessage("Message 3");

        List<ConversationMemory.Message> messages = memory.getMessagesForLlm();

        assertEquals(4, messages.size());
        assertEquals("system", messages.get(0).getRole());
        assertEquals("user", messages.get(1).getRole());
        assertEquals("Message 2", messages.get(1).getContent());
        assertEquals("Message 3", messages.get(3).getContent());
    }

    @Test
    void testAssistantRoleTagging() {

        ConversationMemory memory = new ConversationMemory(5);

        memory.addAssistantMessage("Good morning, how can I help?", "[Agent: Technical]");

        List<ConversationMemory.Message> messages = memory.getMessagesForLlm();
        assertEquals(1, messages.size());

        String content = messages.get(0).getContent();
        assertTrue(content.startsWith("[Agent: Technical]"), "Message should be tagged with appropriate agent tag");
    }
}

