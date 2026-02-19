package kata.functionalshift.declarativeaggregator.domain.vo;

import java.util.Objects;

/** Composite key identifying a line by product snapshot and discount. */
public record LineKey(ProductSnapshot productSnapshot, DiscountId discountId) {
  /** Creates a line key, allowing null discount to represent no discount. */
  public LineKey {
    Objects.requireNonNull(productSnapshot.sku(), "sku is required");
    Objects.requireNonNull(productSnapshot.unitPrice(), "unitPriceSnapshot is required");
    // discountId can be null => means "no discount"
  }
}
