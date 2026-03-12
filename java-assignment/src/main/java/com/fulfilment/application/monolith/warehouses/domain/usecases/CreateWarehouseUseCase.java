package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore,
                                LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {

    // 1. businessUnitCode must be unique
    Warehouse existing = warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);
    if (existing != null && existing.archivedAt == null) {
      throw new IllegalArgumentException("Warehouse with this businessUnitCode already exists");
    }

    // 2. location must exist
    Location location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new IllegalArgumentException("Location does not exist");
    }

    // 3. stock must be <= capacity
    if (warehouse.stock > warehouse.capacity) {
      throw new IllegalArgumentException("Stock cannot exceed capacity");
    }

    // 4. location capacity rules
    if (!location.canHostWarehouse(warehouse.capacity)) {
      throw new IllegalArgumentException("Location cannot host warehouse with this capacity");
    }

    // If all validations pass → create warehouse
    warehouseStore.create(warehouse);
  }
}