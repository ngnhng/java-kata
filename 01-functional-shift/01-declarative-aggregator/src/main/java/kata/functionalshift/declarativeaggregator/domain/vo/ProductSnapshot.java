package kata.functionalshift.declarativeaggregator.domain.vo;

import java.util.Objects;

public record ProductSnapshot(Sku sku, Money unitPrice) {
  public ProductSnapshot {
    Objects.requireNonNull(sku, "sku is required");
    Objects.requireNonNull(unitPrice, "unitPrice is required");
    unitPrice.requireNonNegative("Unit price cannot be negative");
  }
}
