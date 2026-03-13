package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArchiveWarehouseUseCaseTest {

    WarehouseStore warehouseStore;
    ArchiveWarehouseUseCase useCase;

    @BeforeEach
    void setup() {
        warehouseStore = mock(WarehouseStore.class);
        useCase = new ArchiveWarehouseUseCase(warehouseStore);
    }

    @Test
    void shouldArchiveWarehouseSuccessfully() {
        // given
        Warehouse wh = new Warehouse();
        wh.id = 1L;
        wh.businessUnitCode = "BU-001";
        wh.location = "ZWOLLE-001";
        wh.capacity = 40;
        wh.stock = 10;

        when(warehouseStore.findByIdAsDomain(1L))
                .thenReturn(wh);

        // when
        useCase.archive(1L);

        // then
        assertNotNull(wh.archivedAt, "archivedAt must be set");
        verify(warehouseStore).update(wh);
    }

    @Test
    void shouldThrowWhenWarehouseNotFound() {
        when(warehouseStore.findByIdAsDomain(999L))
                .thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> useCase.archive(999L));
    }
}