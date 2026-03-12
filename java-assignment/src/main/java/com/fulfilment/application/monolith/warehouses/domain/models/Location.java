package com.fulfilment.application.monolith.warehouses.domain.models;

public class Location {

  public String identification;

  // maximum number of warehouses allowed
  public int maxNumberOfWarehouses;

  // maximum total capacity allowed
  public int maxCapacity;

  // current number of warehouses already in this location
  public int currentWarehouseCount;

  // current total capacity already used in this location
  public int currentTotalCapacity;

  public Location(String identification,
                  int maxNumberOfWarehouses,
                  int maxCapacity,
                  int currentWarehouseCount,
                  int currentTotalCapacity) {

    this.identification = identification;
    this.maxNumberOfWarehouses = maxNumberOfWarehouses;
    this.maxCapacity = maxCapacity;
    this.currentWarehouseCount = currentWarehouseCount;
    this.currentTotalCapacity = currentTotalCapacity;
  }

  // Business rule: can this location host a warehouse with this capacity?
  public boolean canHostWarehouse(int newWarehouseCapacity) {

    boolean hasWarehouseSlot =
            currentWarehouseCount < maxNumberOfWarehouses;

    boolean hasCapacityAvailable =
            (currentTotalCapacity + newWarehouseCapacity) <= maxCapacity;

    return hasWarehouseSlot && hasCapacityAvailable;
  }
}