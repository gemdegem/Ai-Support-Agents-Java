package com.ragchatbot.billing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BillingServiceTest {

    private BillingService service;

    @BeforeEach
    void setUp() {
        service = new BillingService();
    }

    @Test
    void checkPlanAndPricing_knownCustomer_returnsPlanDetails() {
        Map<String, Object> result = service.checkPlanAndPricing("C001");

        assertEquals(true, result.get("found"));
        assertEquals("Premium", result.get("plan"));
        assertEquals("99 PLN", result.get("priceMonthly"));
        assertEquals("ACTIVE", result.get("status"));
        assertNotNull(result.get("renewalDate"));
    }

    @Test
    void checkPlanAndPricing_unknownCustomer_returnsNotFound() {
        Map<String, Object> result = service.checkPlanAndPricing("C999");

        assertEquals(false, result.get("found"));
        assertTrue(result.get("message").toString().contains("C999"));
    }

    @Test
    void openRefundRequest_knownCustomer_createsTicket() {
        Map<String, Object> result = service.openRefundRequest("C002", "Double charge");

        assertEquals(true, result.get("success"));
        assertTrue(result.get("ticketId").toString().startsWith("REF-"));
        assertEquals("C002", result.get("customerId"));
        assertEquals("Double charge", result.get("reason"));
        assertEquals("OPENED", result.get("status"));
    }

    @Test
    void openRefundRequest_unknownCustomer_returnsFalse() {
        Map<String, Object> result = service.openRefundRequest("C999", "Some reason");

        assertEquals(false, result.get("success"));
        assertTrue(result.get("message").toString().contains("C999"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getBillingHistory_knownCustomer_returnsInvoices() {
        Map<String, Object> result = service.getBillingHistory("C001");

        assertEquals(true, result.get("found"));
        List<Map<String, Object>> invoices = (List<Map<String, Object>>) result.get("invoices");
        assertEquals(3, invoices.size());
        assertEquals("99 PLN", invoices.get(0).get("amount"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getBillingHistory_unknownCustomer_returnsNotFound() {
        Map<String, Object> result = service.getBillingHistory("C999");

        assertEquals(false, result.get("found"));
        List<Map<String, Object>> invoices = (List<Map<String, Object>>) result.get("invoices");
        assertTrue(invoices.isEmpty());
    }
}

