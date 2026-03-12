package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore,
                                 LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void replace(Warehouse newWarehouse) {

    // 1. Find the active warehouse
    Warehouse oldWarehouse =
            warehouseStore.findActiveByBusinessUnitCode(newWarehouse.businessUnitCode);

    if (oldWarehouse == null) {
      throw new IllegalArgumentException("Active warehouse not found");
    }

    // 2. Stock must remain the same
    if (!oldWarehouse.stock.equals(newWarehouse.stock)) {
      throw new IllegalArgumentException("Stock must remain unchanged during replacement");
    }

    // 3. New capacity must be >= stock
    if (newWarehouse.capacity < newWarehouse.stock) {
      throw new IllegalArgumentException("New capacity cannot be smaller than current stock");
    }

    // 4. Location must exist
    Location location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) {
      throw new IllegalArgumentException("Location does not exist");
    }

    // 5. Location must be able to host the new warehouse
    if (!location.canHostWarehouse(newWarehouse.capacity)) {
      throw new IllegalArgumentException("Location cannot host warehouse with given capacity");
    }

    // 6. Archive old warehouse
    oldWarehouse.archivedAt = LocalDateTime.now();
    warehouseStore.update(oldWarehouse);

    // 7. Create new warehouse
    newWarehouse.createdAt = LocalDateTime.now();
    newWarehouse.archivedAt = null;
    warehouseStore.create(newWarehouse);
  }
}