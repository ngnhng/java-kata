package kata.functionalshift.declarativeaggregator.domain.vo;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record OrderId(UUID uuidv7) {
  public OrderId {
    Objects.requireNonNull(uuidv7, "Order ID is required");
    if (uuidv7.version() != 7) {
      throw new IllegalArgumentException("Order ID must be a UUIDv7");
    }
  }

  public Instant creationInstant() {
    return Instant.ofEpochMilli(uuidv7.getMostSignificantBits() >>> 16);
  }
}
