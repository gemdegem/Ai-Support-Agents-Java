package com.ragchatbot.billing;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BillingService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Map<String, Map<String, Object>> customers;

    private final Map<String, List<Map<String, Object>>> invoices;

    public BillingService() {
        this.customers = initCustomers();
        this.invoices = initInvoices();
    }

    public Map<String, Object> checkPlanAndPricing(String customerId) {
        Map<String, Object> customer = customers.get(customerId);
        if (customer == null) {
            return Map.of(
                    "found", false,
                    "message", "Customer not found with ID: " + customerId
            );
        }

        Map<String, Object> result = new LinkedHashMap<>(customer);
        result.put("found", true);
        return result;
    }

    public Map<String, Object> openRefundRequest(String customerId, String reason) {
        if (!customers.containsKey(customerId)) {
            return Map.of(
                    "success", false,
                    "message", "Customer not found with ID: " + customerId
            );
        }

        String ticketId = "REF-" + (10000 + new Random().nextInt(90000));
        return Map.of(
                "success", true,
                "ticketId", ticketId,
                "customerId", customerId,
                "reason", reason,
                "status", "OPENED",
                "estimatedResolution", "5-7 business days"
        );
    }

    public Map<String, Object> getBillingHistory(String customerId) {
        if (!customers.containsKey(customerId)) {
            return Map.of(
                    "found", false,
                    "invoices", List.of(),
                    "message", "Customer not found with ID: " + customerId
            );
        }

        List<Map<String, Object>> history = invoices.getOrDefault(customerId, List.of());
        return Map.of(
                "found", true,
                "customerId", customerId,
                "invoices", history
        );
    }

    private static Map<String, Map<String, Object>> initCustomers() {
        Map<String, Map<String, Object>> db = new HashMap<>();

        db.put("C001", new LinkedHashMap<>(Map.of(
                "customerId", "C001",
                "name", "Anna Kowalska",
                "plan", "Premium",
                "priceMonthly", "99 PLN",
                "status", "ACTIVE",
                "renewalDate", LocalDate.now().plusMonths(1).format(DATE_FMT)
        )));

        db.put("C002", new LinkedHashMap<>(Map.of(
                "customerId", "C002",
                "name", "Jan Nowak",
                "plan", "Basic",
                "priceMonthly", "29 PLN",
                "status", "ACTIVE",
                "renewalDate", LocalDate.now().plusMonths(2).format(DATE_FMT)
        )));

        db.put("C003", new LinkedHashMap<>(Map.of(
                "customerId", "C003",
                "name", "Firma XYZ Sp. z o.o.",
                "plan", "Enterprise",
                "priceMonthly", "299 PLN",
                "status", "ACTIVE",
                "renewalDate", LocalDate.now().plusDays(15).format(DATE_FMT)
        )));

        return db;
    }

    public Set<String> getCustomerIds() {
        return customers.keySet();
    }

    private static Map<String, List<Map<String, Object>>> initInvoices() {
        Map<String, List<Map<String, Object>>> db = new HashMap<>();

        db.put("C001", List.of(
                Map.of("date", "2026-03-01", "amount", "99 PLN", "status", "PAID"),
                Map.of("date", "2026-02-01", "amount", "99 PLN", "status", "PAID"),
                Map.of("date", "2026-01-01", "amount", "99 PLN", "status", "PAID")
        ));

        db.put("C002", List.of(
                Map.of("date", "2026-03-01", "amount", "29 PLN", "status", "PAID"),
                Map.of("date", "2026-02-01", "amount", "29 PLN", "status", "PAID"),
                Map.of("date", "2026-01-01", "amount", "29 PLN", "status", "OVERDUE")
        ));

        db.put("C003", List.of(
                Map.of("date", "2026-03-01", "amount", "299 PLN", "status", "PAID"),
                Map.of("date", "2026-02-01", "amount", "299 PLN", "status", "PAID"),
                Map.of("date", "2026-01-01", "amount", "299 PLN", "status", "PAID")
        ));

        return db;
    }
}

