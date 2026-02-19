package kata.functionalshift.declarativeaggregator.domain.vo;

import java.util.Objects;
import java.util.UUID;

/** Value object representing a discount identifier. */
public record DiscountId(UUID value) {
  /** Creates a discount identifier from a UUID value. */
  public DiscountId {
    Objects.requireNonNull(value, "discountId is required");
  }

  /** Factory method to create a discount identifier. */
  public static DiscountId of(UUID id) {
    return new DiscountId(id);
  }
}
