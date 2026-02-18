package kata.functionalshift.declarativeaggregator.domain.vo;

import java.util.Objects;

public record OrderLine(LineKey key, int quantity) {
  public OrderLine {
    Objects.requireNonNull(key, "key is required");
    if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
  }

  public OrderLine increaseBy(int delta) {
    if (delta <= 0) throw new IllegalArgumentException("delta must be positive");
    return new OrderLine(key, quantity + delta);
  }

  public OrderLine withQuantity(int newQty) {
    if (newQty <= 0) throw new IllegalArgumentException("Quantity must be positive");
    return new OrderLine(key, newQty);
  }

  public Money totalBeforeDiscount() {
    return key.product().unitPrice().multiply(quantity);
  }
}
