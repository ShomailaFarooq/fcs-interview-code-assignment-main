package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
//    return this.listAll().stream()
//            .map(DbWarehouse::toWarehouse)
//            .toList();
      return this.listActive().stream()
              .map(DbWarehouse::toWarehouse)
              .toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    DbWarehouse db = new DbWarehouse();
    db.businessUnitCode = warehouse.businessUnitCode;
    db.location = warehouse.location;
    db.capacity = warehouse.capacity;
    db.stock = warehouse.stock;
    db.createdAt = LocalDateTime.now();
    db.archivedAt = null;

    this.persist(db);

    // update domain model with generated values
    warehouse.id = db.id;
    warehouse.createdAt = db.createdAt;
  }

  @Override
  public void update(Warehouse warehouse) {
    DbWarehouse db = this.findById(warehouse.id);
    if (db == null) {
      return; // use case will handle not found
    }

    db.businessUnitCode = warehouse.businessUnitCode;
    db.location = warehouse.location;
    db.capacity = warehouse.capacity;
    db.stock = warehouse.stock;
    db.createdAt = warehouse.createdAt;
    db.archivedAt = warehouse.archivedAt;
  }

  @Override
  public void remove(Warehouse warehouse) {
    DbWarehouse db = this.findById(warehouse.id);
    if (db == null) {
      return;
    }

    db.archivedAt = LocalDateTime.now();
    warehouse.archivedAt = db.archivedAt;
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse db = this.find("businessUnitCode", buCode).firstResult();
    return db != null ? db.toWarehouse() : null;
  }

  // Helper: find active warehouse by BU code
  public Warehouse findActiveByBusinessUnitCode(String buCode) {
    DbWarehouse db = this.find("businessUnitCode = ?1 AND archivedAt IS NULL", buCode)
            .firstResult();
    return db != null ? db.toWarehouse() : null;
  }

  // Helper: find by ID as domain model
  public Warehouse findByIdAsDomain(Long id) {
    DbWarehouse db = this.findById(id);
    return db != null ? db.toWarehouse() : null;
  }

  public List<DbWarehouse> listActive() {
    return find("archivedAt is null").list();
  }
}