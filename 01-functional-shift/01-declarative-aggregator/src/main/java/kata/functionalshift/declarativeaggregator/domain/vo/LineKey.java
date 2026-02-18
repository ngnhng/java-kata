package kata.functionalshift.declarativeaggregator.domain.vo;

import java.util.Objects;

public record LineKey(ProductSnapshot productSnapshot, DiscountId discountId) {
  public LineKey {
    Objects.requireNonNull(productSnapshot.sku(), "sku is required");
    Objects.requireNonNull(productSnapshot.unitPrice(), "unitPriceSnapshot is required");
    // discountId can be null => means "no discount"
  }
}
