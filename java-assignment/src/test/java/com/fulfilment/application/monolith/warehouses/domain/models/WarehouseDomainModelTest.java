package com.fulfilment.application.monolith.warehouses.domain.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseDomainModelTest {

    @Test
    void testWarehouseFieldAssignment() {
        Warehouse warehouse = new Warehouse();

        warehouse.id = 1L;
        warehouse.businessUnitCode = "WH-001";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 500;
        warehouse.stock = 120;
        warehouse.createdAt = LocalDateTime.now();
        warehouse.archivedAt = null;

        assertEquals(1L, warehouse.id);
        assertEquals("WH-001", warehouse.businessUnitCode);
        assertEquals("ZWOLLE-001", warehouse.location);
        assertEquals(500, warehouse.capacity);
        assertEquals(120, warehouse.stock);
        assertNotNull(warehouse.createdAt);
        assertNull(warehouse.archivedAt);
    }

    @Test
    void testWarehouseDefaultValues() {
        Warehouse warehouse = new Warehouse();

        assertNull(warehouse.id);
        assertNull(warehouse.businessUnitCode);
        assertNull(warehouse.location);
        assertNull(warehouse.capacity);
        assertNull(warehouse.stock);
        assertNull(warehouse.createdAt);
        assertNull(warehouse.archivedAt);
    }
}