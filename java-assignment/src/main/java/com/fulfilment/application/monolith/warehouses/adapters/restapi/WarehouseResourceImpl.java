package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.ports.*;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject private WarehouseStore warehouseStore;
  @Inject private CreateWarehouseOperation createWarehouse;
  @Inject private ReplaceWarehouseOperation replaceWarehouse;
  @Inject private ArchiveWarehouseOperation archiveWarehouse;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseStore.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Transactional
  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    var domain = toDomain(data);
    createWarehouse.create(domain);
    return toWarehouseResponse(domain);
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    var domain = warehouseStore.findByIdAsDomain(Long.valueOf(id));
    if (domain == null) {
      throw new NotFoundException("Warehouse not found");
    }
    return toWarehouseResponse(domain);
  }

  @Transactional
  @Override
  public void archiveAWarehouseUnitByID(String id) {
    archiveWarehouse.archive(Long.valueOf(id));
  }

  @Transactional
  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(String businessUnitCode, @NotNull Warehouse data) {
    var domain = toDomain(data);
    domain.businessUnitCode = businessUnitCode;

    replaceWarehouse.replace(domain);
    return toWarehouseResponse(domain);
  }

  private Warehouse toWarehouseResponse(
          com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {

    var response = new Warehouse();
    response.setId(warehouse.id != null ? warehouse.id.toString() : null);
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);
    return response;
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomain(Warehouse w) {
    var domain = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    domain.id = w.getId() != null ? Long.valueOf(w.getId()) : null;
    domain.businessUnitCode = w.getBusinessUnitCode();
    domain.location = w.getLocation();
    domain.capacity = w.getCapacity();
    domain.stock = w.getStock();
    return domain;
  }
}