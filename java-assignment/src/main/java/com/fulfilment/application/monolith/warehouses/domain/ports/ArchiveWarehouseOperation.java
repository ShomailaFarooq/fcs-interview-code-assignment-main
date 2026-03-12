package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

//public interface ArchiveWarehouseOperation {
//  void archive(Warehouse warehouse);
//}

public interface ArchiveWarehouseOperation {
  void archive(Long id);
}
