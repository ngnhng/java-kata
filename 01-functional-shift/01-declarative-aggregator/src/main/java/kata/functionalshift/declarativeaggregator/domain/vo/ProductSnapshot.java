package kata.functionalshift.declarativeaggregator.domain.vo;

import java.util.Objects;

/** Snapshot of product data captured in an order line. */
public record ProductSnapshot(Sku sku, Money unitPrice) {
  /** Creates a product snapshot with immutable SKU and unit price values. */
  public ProductSnapshot {
    Objects.requireNonNull(sku, "sku is required");
    Objects.requireNonNull(unitPrice, "unitPrice is required");
    unitPrice.requireNonNegative("Unit price cannot be negative");
  }
}
