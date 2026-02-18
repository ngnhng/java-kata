package kata.functionalshift.declarativeaggregator.domain.vo;

import com.fasterxml.uuid.Generators;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record LineId(UUID uuidv7) {

  public LineId {
    Objects.requireNonNull(uuidv7, "Order ID is required");
    if (uuidv7.version() != 7) {
      throw new IllegalArgumentException("Order ID must be a UUIDv7");
    }
  }

  public Instant creationInstant() {
    return Instant.ofEpochMilli(uuidv7.getMostSignificantBits() >>> 16);
  }

  public static LineId newRandom() {
    return new LineId(Generators.timeBasedEpochRandomGenerator().generate());
  }
}
