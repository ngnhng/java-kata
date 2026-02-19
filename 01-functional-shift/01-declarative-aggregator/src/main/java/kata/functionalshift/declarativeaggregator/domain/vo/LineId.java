package kata.functionalshift.declarativeaggregator.domain.vo;

import com.fasterxml.uuid.Generators;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Value object representing an order line identifier. */
public record LineId(UUID uuidv7) {

  /** Creates a line identifier from a UUIDv7 value. */
  public LineId {
    Objects.requireNonNull(uuidv7, "Order ID is required");
    if (uuidv7.version() != 7) {
      throw new IllegalArgumentException("Order ID must be a UUIDv7");
    }
  }

  /** Returns the creation instant encoded in the UUIDv7 value. */
  public Instant creationInstant() {
    return Instant.ofEpochMilli(uuidv7.getMostSignificantBits() >>> 16);
  }

  /** Creates a new random UUIDv7-backed line identifier. */
  public static LineId newRandom() {
    return new LineId(Generators.timeBasedEpochRandomGenerator().generate());
  }
}
