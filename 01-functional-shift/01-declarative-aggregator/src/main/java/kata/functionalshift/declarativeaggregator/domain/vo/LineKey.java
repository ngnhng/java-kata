package kata.functionalshift.declarativeaggregator.domain.vo;

import java.util.Objects;

public record LineKey(ProductSnapshot product, DiscountId discountId) {
  public LineKey {
    Objects.requireNonNull(product.sku(), "sku is required");
    Objects.requireNonNull(product.unitPrice(), "unitPriceSnapshot is required");
    // discountId can be null => means "no discount"
  }
}
