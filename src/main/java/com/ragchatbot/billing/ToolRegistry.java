package com.ragchatbot.billing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ToolRegistry {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ArrayNode getToolDefinitions() {
        ArrayNode tools = MAPPER.createArrayNode();

        tools.add(buildGetCurrentCustomerId());
        tools.add(buildCheckPlanAndPricing());
        tools.add(buildOpenRefundRequest());
        tools.add(buildGetBillingHistory());

        return tools;
    }

    private static ObjectNode buildGetCurrentCustomerId() {
        ObjectNode tool = MAPPER.createObjectNode();
        tool.put("type", "function");

        ObjectNode function = MAPPER.createObjectNode();
        function.put("name", "getCurrentCustomerId");
        function.put("description",
                "Returns the ID of the currently logged-in customer. " +
                "Use when you need the customer identifier for informational purposes.");

        ObjectNode parameters = MAPPER.createObjectNode();
        parameters.put("type", "object");
        parameters.set("properties", MAPPER.createObjectNode());

        function.set("parameters", parameters);
        tool.set("function", function);
        return tool;
    }

    private static ObjectNode buildCheckPlanAndPricing() {
        ObjectNode tool = MAPPER.createObjectNode();
        tool.put("type", "function");

        ObjectNode function = MAPPER.createObjectNode();
        function.put("name", "checkPlanAndPricing");
        function.put("description",
                "Checks the current active subscription plan and pricing ONLY for the logged-in customer. " +
                "Use when the user asks about THEIR specific plan, THEIR price, subscription status, or renewal date. " +
                "Do NOT use this to answer general questions about what plans exist in the system. " +
                "The customer ID is automatically retrieved from the session — do not provide it.");

        ObjectNode parameters = MAPPER.createObjectNode();
        parameters.put("type", "object");
        parameters.set("properties", MAPPER.createObjectNode());

        function.set("parameters", parameters);
        tool.set("function", function);
        return tool;
    }

    private static ObjectNode buildOpenRefundRequest() {
        ObjectNode tool = MAPPER.createObjectNode();
        tool.put("type", "function");

        ObjectNode function = MAPPER.createObjectNode();
        function.put("name", "openRefundRequest");
        function.put("description",
                "Opens a refund request for the logged-in customer. " +
                "Use when the user wants to file a complaint, requests a refund, or reports a double charge. " +
                "The customer ID is automatically retrieved from the session — provide only the refund reason.");

        ObjectNode parameters = MAPPER.createObjectNode();
        parameters.put("type", "object");

        ObjectNode properties = MAPPER.createObjectNode();

        ObjectNode reasonProp = MAPPER.createObjectNode();
        reasonProp.put("type", "string");
        reasonProp.put("description", "Refund reason provided by the customer");
        properties.set("reason", reasonProp);

        parameters.set("properties", properties);
        parameters.set("required", MAPPER.createArrayNode().add("reason"));

        function.set("parameters", parameters);
        tool.set("function", function);
        return tool;
    }

    private static ObjectNode buildGetBillingHistory() {
        ObjectNode tool = MAPPER.createObjectNode();
        tool.put("type", "function");

        ObjectNode function = MAPPER.createObjectNode();
        function.put("name", "getBillingHistory");
        function.put("description",
                "Retrieves the invoice and payment history for the logged-in customer. " +
                "Use when the user asks about previous invoices, charge history, or overdue payments. " +
                "The customer ID is automatically retrieved from the session — do not provide it.");

        ObjectNode parameters = MAPPER.createObjectNode();
        parameters.put("type", "object");
        parameters.set("properties", MAPPER.createObjectNode());

        function.set("parameters", parameters);
        tool.set("function", function);
        return tool;
    }
}

