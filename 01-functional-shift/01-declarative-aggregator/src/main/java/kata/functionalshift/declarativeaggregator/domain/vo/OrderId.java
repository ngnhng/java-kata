package kata.functionalshift.declarativeaggregator.domain.vo;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Value object representing an order identifier. */
public record OrderId(UUID uuidv7) {
  /** Creates an order identifier from a UUIDv7 value. */
  public OrderId {
    Objects.requireNonNull(uuidv7, "Order ID is required");
    if (uuidv7.version() != 7) {
      throw new IllegalArgumentException("Order ID must be a UUIDv7");
    }
  }

  /** Returns the creation instant encoded in the UUIDv7 value. */
  public Instant creationInstant() {
    return Instant.ofEpochMilli(uuidv7.getMostSignificantBits() >>> 16);
  }
}
