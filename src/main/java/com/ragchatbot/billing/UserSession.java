package com.ragchatbot.billing;

import java.util.*;

public class UserSession {

    private static final Map<String, String> CUSTOMER_NAMES = Map.of(
            "C001", "Anna Kowalska",
            "C002", "Jan Nowak",
            "C003", "Firma XYZ Sp. z o.o."
    );

    private final String customerId;

    public UserSession() {
        List<String> ids = new ArrayList<>(CUSTOMER_NAMES.keySet());
        Collections.sort(ids);
        Collections.shuffle(ids);
        this.customerId = ids.getFirst();
    }

    public UserSession(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return CUSTOMER_NAMES.getOrDefault(customerId, "Unknown customer");
    }
}

