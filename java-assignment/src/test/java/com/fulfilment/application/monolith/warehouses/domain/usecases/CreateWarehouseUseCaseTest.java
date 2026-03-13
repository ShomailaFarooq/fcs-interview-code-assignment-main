package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateWarehouseUseCaseTest {

    WarehouseStore warehouseStore;
    LocationResolver locationResolver;
    CreateWarehouseUseCase useCase;

    @BeforeEach
    void setup() {
        warehouseStore = mock(WarehouseStore.class);
        locationResolver = mock(LocationResolver.class);
        useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);
    }

    private Warehouse buildWarehouse(String code, String location, int capacity, int stock) {
        Warehouse w = new Warehouse();
        w.businessUnitCode = code;
        w.location = location;
        w.capacity = capacity;
        w.stock = stock;
        return w;
    }

    private Location buildLocation(String id, int maxWarehouses, int maxCapacity, int currentCount, int currentCapacity) {
        return new Location(id, maxWarehouses, maxCapacity, currentCount, currentCapacity);
    }

    @Test
    void shouldCreateWarehouseSuccessfully() {
        Warehouse w = buildWarehouse("MWH.NEW", "ZWOLLE-001", 30, 10);
        Location loc = buildLocation("ZWOLLE-001", 2, 40, 0, 0);

        when(warehouseStore.findByBusinessUnitCode("MWH.NEW")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("ZWOLLE-001")).thenReturn(loc);

        useCase.create(w);

        verify(warehouseStore).create(w);
    }

    @Test
    void shouldFailWhenBusinessUnitCodeAlreadyExists() {
        Warehouse existing = buildWarehouse("MWH.001", "ZWOLLE-001", 30, 10);
        existing.archivedAt = null;

        Warehouse w = buildWarehouse("MWH.001", "ZWOLLE-001", 30, 10);

        when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(existing);

        assertThrows(IllegalArgumentException.class, () -> useCase.create(w));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void shouldFailWhenLocationDoesNotExist() {
        Warehouse w = buildWarehouse("MWH.NEW2", "INVALID-999", 10, 5);

        when(warehouseStore.findByBusinessUnitCode("MWH.NEW2")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("INVALID-999"))
                .thenThrow(new IllegalArgumentException("Location not found"));

        assertThrows(IllegalArgumentException.class, () -> useCase.create(w));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void shouldFailWhenStockExceedsCapacity() {
        Warehouse w = buildWarehouse("MWH.NEW3", "ZWOLLE-001", 5, 99);
        Location loc = buildLocation("ZWOLLE-001", 2, 40, 0, 0);

        when(warehouseStore.findByBusinessUnitCode("MWH.NEW3")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("ZWOLLE-001")).thenReturn(loc);

        assertThrows(IllegalArgumentException.class, () -> useCase.create(w));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void shouldFailWhenLocationCannotHostWarehouse() {
        Warehouse w = buildWarehouse("MWH.NEW4", "ZWOLLE-001", 30, 10);
        // location already full
        Location loc = buildLocation("ZWOLLE-001", 1, 40, 1, 40);

        when(warehouseStore.findByBusinessUnitCode("MWH.NEW4")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("ZWOLLE-001")).thenReturn(loc);

        assertThrows(IllegalArgumentException.class, () -> useCase.create(w));
        verify(warehouseStore, never()).create(any());
    }
}