package kata.functionalshift.declarativeaggregator.domain.vo;

import java.util.Objects;

/** Value object representing one order line entry. */
public record OrderLine(LineId id, LineKey key, int quantity) {
  /** Creates an order line with identity, key, and quantity. */
  public OrderLine {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(key, "key is required");
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
  }

  /** Returns a new line with quantity increased by the given delta. */
  public OrderLine increaseBy(int delta) {
    if (delta <= 0) {
      throw new IllegalArgumentException("delta must be positive");
    }
    return new OrderLine(id, key, quantity + delta);
  }

  /** Returns a new line with the specified absolute quantity. */
  public OrderLine withQuantity(int newQty) {
    if (newQty <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    return new OrderLine(id, key, newQty);
  }

  /** Returns total gross amount before any discount rules are applied. */
  public Money totalBeforeDiscount() {
    return key.productSnapshot().unitPrice().multiply(quantity);
  }
}
