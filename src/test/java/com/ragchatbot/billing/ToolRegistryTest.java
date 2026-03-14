package com.ragchatbot.billing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToolRegistryTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void getToolDefinitions_returnsFourTools() {
        ArrayNode tools = ToolRegistry.getToolDefinitions();
        assertEquals(4, tools.size(), "There should be exactly 4 tools (with getCurrentCustomerId)");
    }

    @Test
    void everyToolHasCorrectStructure() {
        ArrayNode tools = ToolRegistry.getToolDefinitions();

        for (JsonNode tool : tools) {
            assertEquals("function", tool.get("type").asText(),
                    "Each tool must have type=function");

            JsonNode function = tool.get("function");
            assertNotNull(function, "Missing 'function' node");
            assertFalse(function.get("name").asText().isBlank(), "Name cannot be empty");
            assertFalse(function.get("description").asText().isBlank(), "Description cannot be empty");

            JsonNode params = function.get("parameters");
            assertNotNull(params, "Missing 'parameters' node");
            assertEquals("object", params.get("type").asText());
            assertTrue(params.has("properties"), "Missing 'properties' in parameters");
        }
    }

    @Test
    void getCurrentCustomerId_hasNoParams() {
        JsonNode tool = ToolRegistry.getToolDefinitions().get(0);
        JsonNode function = tool.get("function");

        assertEquals("getCurrentCustomerId", function.get("name").asText());
        JsonNode props = function.get("parameters").get("properties");
        assertEquals(0, props.size(), "getCurrentCustomerId should have no parameters");
    }

    @Test
    void checkPlanAndPricing_hasNoCustomerIdParam() {
        JsonNode tool = ToolRegistry.getToolDefinitions().get(1);
        JsonNode props = tool.get("function").get("parameters").get("properties");

        assertFalse(props.has("customerId"),
                "SECURITY: customerId should NOT be a tool parameter");
        assertEquals(0, props.size(), "checkPlanAndPricing should have no parameters");
    }

    @Test
    void openRefundRequest_hasOnlyReasonParam() {
        JsonNode tool = ToolRegistry.getToolDefinitions().get(2);
        JsonNode function = tool.get("function");

        assertEquals("openRefundRequest", function.get("name").asText());

        JsonNode props = function.get("parameters").get("properties");
        assertFalse(props.has("customerId"),
                "SECURITY: customerId should NOT be a parameter");
        assertTrue(props.has("reason"), "openRefundRequest should have reason parameter");

        JsonNode required = function.get("parameters").get("required");
        assertEquals(1, required.size(), "openRefundRequest requires 1 parameter (reason)");
        assertEquals("reason", required.get(0).asText());
    }

    @Test
    void getBillingHistory_hasNoParams() {
        JsonNode tool = ToolRegistry.getToolDefinitions().get(3);
        JsonNode function = tool.get("function");

        assertEquals("getBillingHistory", function.get("name").asText());

        JsonNode props = function.get("parameters").get("properties");
        assertFalse(props.has("customerId"),
                "SECURITY: customerId should NOT be a parameter");
        assertEquals(0, props.size(), "getBillingHistory should have no parameters");
    }

    @Test
    void toolDefinitions_serializeToValidJson() throws Exception {
        ArrayNode tools = ToolRegistry.getToolDefinitions();
        String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(tools);

        JsonNode parsed = MAPPER.readTree(json);
        assertTrue(parsed.isArray());
        assertEquals(4, parsed.size());
    }
}

