package com.ragchatbot.memory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConversationMemory {

    private final int maxMessages;

    private Message systemPrompt;

    private final LinkedList<Message> windowMessages;

    public ConversationMemory(int maxMessages) {
        this.maxMessages = maxMessages;
        this.windowMessages = new LinkedList<>();
    }

    public void setSystemPrompt(String content) {
        this.systemPrompt = new Message("system", content);
    }

    public void addUserMessage(String content) {
        addMessage(new Message("user", content));
    }

    public void addAssistantMessage(String content, String tag) {
        String taggedContent = tag != null && !tag.isEmpty() ? tag + " " + content : content;
        addMessage(new Message("assistant", taggedContent));
    }

    public void addAssistantMessage(String content) {
         addAssistantMessage(content, null);
    }

    public void addToolMessage(String content) {
        addMessage(new Message("tool", content));
    }

    private void addMessage(Message message) {
        windowMessages.add(message);
        if (windowMessages.size() > maxMessages) {
            windowMessages.removeFirst();
        }
    }

    public List<Message> getMessagesForLlm() {
        List<Message> allMessages = new ArrayList<>();
        if (systemPrompt != null) {
            allMessages.add(systemPrompt);
        }
        allMessages.addAll(windowMessages);
        return allMessages;
    }

    public static class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}

