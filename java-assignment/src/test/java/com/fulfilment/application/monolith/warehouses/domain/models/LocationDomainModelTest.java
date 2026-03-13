package com.fulfilment.application.monolith.warehouses.domain.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationDomainModelTest {

    @Test
    void testLocationConstructorAndFields() {
        Location location = new Location(
                "ZWOLLE-001",
                5,      // max warehouses
                1000,   // max capacity
                2,      // current warehouses
                400     // current capacity used
        );

        assertEquals("ZWOLLE-001", location.identification);
        assertEquals(5, location.maxNumberOfWarehouses);
        assertEquals(1000, location.maxCapacity);
        assertEquals(2, location.currentWarehouseCount);
        assertEquals(400, location.currentTotalCapacity);
    }

    @Test
    void testCanHostWarehouseReturnsTrue() {
        Location location = new Location(
                "ZWOLLE-001",
                5,      // max warehouses
                1000,   // max capacity
                2,      // current warehouses
                400     // current capacity used
        );

        boolean result = location.canHostWarehouse(200);

        assertTrue(result); // 2 < 5 AND 400 + 200 <= 1000
    }

    @Test
    void testCanHostWarehouseReturnsFalseWhenNoSlots() {
        Location location = new Location(
                "ZWOLLE-001",
                3,      // max warehouses
                1000,
                3,      // already full
                400
        );

        boolean result = location.canHostWarehouse(100);

        assertFalse(result); // no warehouse slots available
    }

    @Test
    void testCanHostWarehouseReturnsFalseWhenNoCapacity() {
        Location location = new Location(
                "ZWOLLE-001",
                5,
                500,    // max capacity
                2,
                480     // almost full
        );

        boolean result = location.canHostWarehouse(50);

        assertFalse(result); // 480 + 50 > 500
    }
}