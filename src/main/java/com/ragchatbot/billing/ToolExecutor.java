package com.ragchatbot.billing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ToolExecutor {

    private final BillingService billingService;
    private final UserSession session;
    private final ObjectMapper objectMapper;

    public ToolExecutor(BillingService billingService, UserSession session) {
        this.billingService = billingService;
        this.session = session;
        this.objectMapper = new ObjectMapper();
    }

    public String execute(String functionName, String argumentsJson) {
        try {
            JsonNode args = objectMapper.readTree(argumentsJson);
            String customerId = session.getCustomerId();

            Map<String, Object> result = switch (functionName) {
                case "getCurrentCustomerId" -> Map.of(
                        "customerId", customerId,
                        "info", "This is the ID of the currently logged-in customer"
                );
                case "checkPlanAndPricing" -> billingService.checkPlanAndPricing(customerId);
                case "openRefundRequest" -> billingService.openRefundRequest(
                        customerId,
                        args.get("reason").asText()
                );
                case "getBillingHistory" -> billingService.getBillingHistory(customerId);
                default -> Map.of("error", "Unknown tool: " + functionName);
            };

            return objectMapper.writeValueAsString(result);

        } catch (Exception e) {
            return "{\"error\": \"Tool execution error: " + e.getMessage() + "\"}";
        }
    }
}

