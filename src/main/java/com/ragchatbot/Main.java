package com.ragchatbot;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ChatOrchestrator orchestrator = new ChatOrchestrator();

        try {
            orchestrator.initialize();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during initialization: " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return;
        }

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║          RAG Chatbot — Technical Assistant           ║");
        System.out.println("║              & Billing Specialist                    ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  Logged in as:     " + padRight(orchestrator.getUserSession().getCustomerName(), 34) + "║");
        System.out.println("║  Customer ID:      " + padRight(orchestrator.getUserSession().getCustomerId(), 34) + "║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  Type your message and press Enter.                  ║");
        System.out.println("║  Type 'exit' or 'quit' to terminate.                 ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Assistant: Hi! I am your virtual assistant. I can answer your technical questions or help you with billing and account details. How can I help you today?\n");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("You: ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String input = scanner.nextLine();

                if (input.trim().equalsIgnoreCase("exit")
                    || input.trim().equalsIgnoreCase("quit")) {
                    System.out.println("Goodbye!");
                    break;
                }

                if (input.trim().length() < 2) {
                    if (!input.trim().isEmpty()) {
                        System.out.println("(Message too short, ignored)");
                    }
                    continue;
                }

                System.out.println("Assistant is thinking...");
                String response = orchestrator.processMessage(input);
                System.out.println("\rAssistant: " + response);
                System.out.println();
            }
        }
    }

    private static String padRight(String text, int length) {
        if (text.length() >= length) {
            return text.substring(0, length);
        }
        return text + " ".repeat(length - text.length());
    }
}

