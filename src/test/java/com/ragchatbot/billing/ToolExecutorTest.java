package com.ragchatbot.billing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToolExecutorTest {

    private ToolExecutor executor;

    @BeforeEach
    void setUp() {

        UserSession session = new UserSession("C001");
        executor = new ToolExecutor(new BillingService(), session);
    }

    @Test
    void execute_getCurrentCustomerId_returnsSessionId() {
        String result = executor.execute("getCurrentCustomerId", "{}");

        assertTrue(result.contains("C001"), "Should return session ID");
    }

    @Test
    void execute_checkPlanAndPricing_usesSessionId() {

        String result = executor.execute("checkPlanAndPricing", "{}");

        assertTrue(result.contains("Premium"), "Should contain plan for customer C001");
        assertTrue(result.contains("99 PLN"), "Should contain price for customer C001");
    }

    @Test
    void execute_openRefundRequest_usesSessionId() {

        String result = executor.execute("openRefundRequest",
                "{\"reason\": \"Double charge\"}");

        assertTrue(result.contains("REF-"), "Should contain ticket number");
        assertTrue(result.contains("OPENED"), "Status should be OPENED");
        assertTrue(result.contains("C001"), "Should use session ID C001");
    }

    @Test
    void execute_getBillingHistory_usesSessionId() {

        String result = executor.execute("getBillingHistory", "{}");

        assertTrue(result.contains("99 PLN"), "Should contain invoices for customer C001");
        assertTrue(result.contains("PAID"), "Invoice status should be PAID");
    }

    @Test
    void execute_unknownFunction_returnsError() {
        String result = executor.execute("deleteEverything", "{}");

        assertTrue(result.contains("error"), "Should return error");
        assertTrue(result.contains("Unknown tool"), "Should contain info about unknown tool");
    }

    @Test
    void execute_differentSession_returnsDifferentData() {

        UserSession otherSession = new UserSession("C003");
        ToolExecutor otherExecutor = new ToolExecutor(new BillingService(), otherSession);

        String result = otherExecutor.execute("checkPlanAndPricing", "{}");

        assertTrue(result.contains("Enterprise"), "Should contain plan for customer C003");
        assertTrue(result.contains("299 PLN"), "Should contain price for customer C003");
    }
}

