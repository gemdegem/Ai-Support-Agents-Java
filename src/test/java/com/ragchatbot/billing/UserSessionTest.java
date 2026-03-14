package com.ragchatbot.billing;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    private static final Set<String> VALID_IDS = Set.of("C001", "C002", "C003");

    @RepeatedTest(10)
    void randomSession_alwaysPicksValidId() {
        UserSession session = new UserSession();
        assertTrue(VALID_IDS.contains(session.getCustomerId()),
                "Randomly chosen ID should be one of: " + VALID_IDS);
    }

    @Test
    void specificSession_returnsGivenId() {
        UserSession session = new UserSession("C002");
        assertEquals("C002", session.getCustomerId());
    }

    @Test
    void getCustomerName_returnsCorrectName() {
        assertEquals("Anna Kowalska", new UserSession("C001").getCustomerName());
        assertEquals("Jan Nowak", new UserSession("C002").getCustomerName());
        assertEquals("Firma XYZ Sp. z o.o.", new UserSession("C003").getCustomerName());
    }

    @Test
    void unknownCustomer_returnsDefaultName() {
        UserSession session = new UserSession("C999");
        assertEquals("Unknown customer", session.getCustomerName());
    }
}

