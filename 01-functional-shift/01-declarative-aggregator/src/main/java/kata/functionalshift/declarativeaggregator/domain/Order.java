package kata.functionalshift.declarativeaggregator.domain;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import kata.functionalshift.declarativeaggregator.domain.vo.DiscountId;
import kata.functionalshift.declarativeaggregator.domain.vo.LineKey;
import kata.functionalshift.declarativeaggregator.domain.vo.Money;
import kata.functionalshift.declarativeaggregator.domain.vo.OrderId;
import kata.functionalshift.declarativeaggregator.domain.vo.OrderLine;
import kata.functionalshift.declarativeaggregator.domain.vo.ProductSnapshot;

/**
 * Simplistic implementation of the Order immutable entity.
 *
 * <p>Immutable entities can happen such as when the entity holds a historic transitional point. For
 * example, adding a new LineItem to the List cannot change the fact that there was once a List
 * without that new LineItem
 */
public final class Order {
  private final OrderId id;
  private final Map<LineKey, OrderLine> lines;
  private final OrderStatus status;

  public Order(OrderId id, Map<LineKey, OrderLine> lines, OrderStatus status) {
    this.id = Objects.requireNonNull(id, "Order ID is required");
    this.status = Objects.requireNonNull(status, "Order Status is required");
    Objects.requireNonNull(lines, "Order Lines cannot be null");
    this.lines = Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(lines)));

    if (lines.isEmpty() && OrderStatus.SHIPPED.equals(status)) {
      throw new IllegalStateException("Cannot ship an empty order");
    }
  }

  public Order add(ProductSnapshot product, int quantity, DiscountId discountId) {
    Objects.requireNonNull(product, "product is required");
    if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");

    var key = new LineKey(product, discountId);
    var next = new LinkedHashMap<>(this.lines);

    next.merge(
        key,
        new OrderLine(key, quantity),
        (oldLine, newLine) -> oldLine.increaseBy(newLine.quantity()));

    return new Order(this.id, next, this.status);
  }

  public Order setQuantity(ProductSnapshot product, int quantity, DiscountId discountId) {
    Objects.requireNonNull(product, "product is required");
    var key = new LineKey(product, discountId);

    var next = new LinkedHashMap<>(this.lines);
    if (quantity <= 0) {
      next.remove(key); // common UX: set to 0 => remove
    } else {
      var existing = next.get(key);
      if (existing == null) throw new IllegalArgumentException("Line not found");
      next.put(key, existing.withQuantity(quantity));
    }
    return new Order(this.id, next, this.status);
  }

  public Instant creationInstant() {
    return id.creationInstant();
  }

  public OrderId id() {
    return id;
  }

  public List<OrderLine> lines() {
    return List.copyOf(lines.values());
  }

  public OrderStatus status() {
    return status;
  }

  public Money totalBeforeDiscount() {
    return lines.values().stream()
        .map(OrderLine::totalBeforeDiscount)
        .reduce(Money::add)
        .orElseThrow(() -> new IllegalStateException("Cannot total an empty order"));
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Order order)) {
      return false;
    }
    return id.equals(order.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
