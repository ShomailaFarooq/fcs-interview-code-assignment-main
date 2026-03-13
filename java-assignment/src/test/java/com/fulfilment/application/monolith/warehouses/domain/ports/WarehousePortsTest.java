package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WarehousePortsTest {

    @Test
    void testCreateWarehouseOperation() {
        // Fake implementation of the interface
        CreateWarehouseOperation op = warehouse -> {
            warehouse.createdAt = LocalDateTime.now();
        };

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "WH-001";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 500;

        op.create(warehouse);

        assertEquals("WH-001", warehouse.businessUnitCode);
        assertEquals("ZWOLLE-001", warehouse.location);
        assertEquals(500, warehouse.capacity);
        assertNotNull(warehouse.createdAt);
    }

    @Test
    void testArchiveWarehouseOperation() {
        // Fake implementation — interface only takes an ID
        ArchiveWarehouseOperation op = id -> {
            // No-op, just verifying the interface accepts the call
        };

        assertDoesNotThrow(() -> op.archive(1L));
    }

    @Test
    void testLocationResolver() {
        LocationResolver resolver = identifier -> {
            if (!identifier.startsWith("LOC-")) {
                throw new IllegalArgumentException("Invalid location");
            }
            return new Location(
                    identifier,
                    5,      // maxNumberOfWarehouses
                    1000,   // maxCapacity
                    2,      // currentWarehouseCount
                    400     // currentTotalCapacity
            );
        };

        Location loc = resolver.resolveByIdentifier("LOC-001");
        assertEquals("LOC-001", loc.identification);

        assertThrows(IllegalArgumentException.class,
                () -> resolver.resolveByIdentifier("BAD-123"));
    }
}