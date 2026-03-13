package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReplaceWarehouseUseCaseTest {

    WarehouseStore warehouseStore;
    LocationResolver locationResolver;
    ReplaceWarehouseUseCase useCase;

    @BeforeEach
    void setup() {
        warehouseStore = mock(WarehouseStore.class);
        locationResolver = mock(LocationResolver.class);
        useCase = new ReplaceWarehouseUseCase(warehouseStore, locationResolver);
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
    void shouldReplaceWarehouseSuccessfully() {
        Warehouse old = buildWarehouse("MWH.001", "ZWOLLE-001", 30, 10);
        Warehouse newW = buildWarehouse("MWH.001", "AMSTERDAM-001", 50, 10);
        Location loc = buildLocation("AMSTERDAM-001", 5, 100, 0, 0);

        when(warehouseStore.findActiveByBusinessUnitCode("MWH.001")).thenReturn(old);
        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(loc);

        useCase.replace(newW);

        assertNotNull(old.archivedAt);
        verify(warehouseStore).update(old);
        verify(warehouseStore).create(newW);
    }

    @Test
    void shouldFailWhenActiveWarehouseNotFound() {
        Warehouse newW = buildWarehouse("MWH.999", "ZWOLLE-001", 30, 10);

        when(warehouseStore.findActiveByBusinessUnitCode("MWH.999")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> useCase.replace(newW));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void shouldFailWhenStockDoesNotMatch() {
        Warehouse old = buildWarehouse("MWH.001", "ZWOLLE-001", 30, 10);
        Warehouse newW = buildWarehouse("MWH.001", "ZWOLLE-001", 30, 99);

        when(warehouseStore.findActiveByBusinessUnitCode("MWH.001")).thenReturn(old);

        assertThrows(IllegalArgumentException.class, () -> useCase.replace(newW));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void shouldFailWhenNewCapacityLessThanStock() {
        Warehouse old = buildWarehouse("MWH.001", "ZWOLLE-001", 30, 10);
        Warehouse newW = buildWarehouse("MWH.001", "ZWOLLE-001", 5, 10);

        when(warehouseStore.findActiveByBusinessUnitCode("MWH.001")).thenReturn(old);

        assertThrows(IllegalArgumentException.class, () -> useCase.replace(newW));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void shouldFailWhenLocationDoesNotExist() {
        Warehouse old = buildWarehouse("MWH.001", "ZWOLLE-001", 30, 10);
        Warehouse newW = buildWarehouse("MWH.001", "INVALID-999", 30, 10);

        when(warehouseStore.findActiveByBusinessUnitCode("MWH.001")).thenReturn(old);
        when(locationResolver.resolveByIdentifier("INVALID-999"))
                .thenThrow(new IllegalArgumentException("Location not found"));

        assertThrows(IllegalArgumentException.class, () -> useCase.replace(newW));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void shouldFailWhenLocationCannotHostWarehouse() {
        Warehouse old = buildWarehouse("MWH.001", "ZWOLLE-001", 30, 10);
        Warehouse newW = buildWarehouse("MWH.001", "TILBURG-001", 30, 10);
        Location loc = buildLocation("TILBURG-001", 1, 40, 1, 40);

        when(warehouseStore.findActiveByBusinessUnitCode("MWH.001")).thenReturn(old);
        when(locationResolver.resolveByIdentifier("TILBURG-001")).thenReturn(loc);

        assertThrows(IllegalArgumentException.class, () -> useCase.replace(newW));
        verify(warehouseStore, never()).create(any());
    }
}