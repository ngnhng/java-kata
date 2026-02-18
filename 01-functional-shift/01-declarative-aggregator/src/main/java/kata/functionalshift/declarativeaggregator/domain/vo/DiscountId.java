package kata.functionalshift.declarativeaggregator.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record DiscountId(UUID value) {
  public DiscountId {
    Objects.requireNonNull(value, "discountId is required");
  }

  public static DiscountId of(UUID id) {
    return new DiscountId(id);
  }
}
