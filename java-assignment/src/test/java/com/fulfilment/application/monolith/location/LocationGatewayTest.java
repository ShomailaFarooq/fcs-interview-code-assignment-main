package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationGatewayTest {

  LocationGateway gateway;

  @BeforeEach
  void setup() {
    gateway = new LocationGateway();
  }

  @Test
  void shouldResolveKnownLocation() {
    Location loc = gateway.resolveByIdentifier("ZWOLLE-001");
    assertNotNull(loc);
    assertEquals("ZWOLLE-001", loc.identification);
  }

  @Test
  void shouldResolveLocationCaseInsensitive() {
    Location loc = gateway.resolveByIdentifier("amsterdam-001");
    assertNotNull(loc);
    assertEquals("AMSTERDAM-001", loc.identification);
  }

  @Test
  void shouldThrowForUnknownLocation() {
    assertThrows(IllegalArgumentException.class,
            () -> gateway.resolveByIdentifier("UNKNOWN-999"));
  }

  @Test
  void shouldResolveAllKnownLocations() {
    String[] known = {
            "ZWOLLE-001", "ZWOLLE-002", "AMSTERDAM-001", "AMSTERDAM-002",
            "TILBURG-001", "HELMOND-001", "EINDHOVEN-001", "VETSBY-001"
    };
    for (String id : known) {
      Location loc = gateway.resolveByIdentifier(id);
      assertNotNull(loc, "Should resolve: " + id);
      assertEquals(id, loc.identification);
    }
  }
}