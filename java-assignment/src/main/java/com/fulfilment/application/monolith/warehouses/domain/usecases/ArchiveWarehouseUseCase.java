package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  //public void archive(Warehouse warehouse) {
  @Override
  public void archive(Long id) {

    // 1. Find warehouse
    Warehouse warehouse = warehouseStore.findByIdAsDomain(id);
    if (warehouse == null) {
      throw new IllegalArgumentException("Warehouse not found");
    }

    // 2. Archive it
    warehouse.archivedAt = LocalDateTime.now();

    // 3. Save update
    warehouseStore.update(warehouse);

  }
}
