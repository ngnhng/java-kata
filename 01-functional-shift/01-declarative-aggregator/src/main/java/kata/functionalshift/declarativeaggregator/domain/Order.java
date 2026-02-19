package kata.functionalshift.declarativeaggregator.domain;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import kata.functionalshift.declarativeaggregator.domain.vo.DiscountId;
import kata.functionalshift.declarativeaggregator.domain.vo.LineId;
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
 * without that new LineItem.
 */
public final class Order {
  private final OrderId id;
  private final OrderStatus status;

  /** Optimistic concurrency control. */
  private final int version;

  // Helps merge same item added twice
  private final Map<LineKey, LineId> primaryLineForKey;
  private final Map<LineId, OrderLine> lines;

  /** Creates an immutable order from identity, lines, status, and version metadata. */
  public Order(
      OrderId id,
      Map<LineKey, LineId> primaryLineForKey,
      Map<LineId, OrderLine> lines,
      OrderStatus status,
      int version) {
    this.id = Objects.requireNonNull(id, "Order ID is required");
    this.status = Objects.requireNonNull(status, "Order Status is required");

    Objects.requireNonNull(lines, "Order Lines cannot be null");
    Objects.requireNonNull(primaryLineForKey, "Order primaryLineForKey cannot be null");

    this.primaryLineForKey =
        Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(primaryLineForKey)));
    this.lines = Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(lines)));

    if (lines.isEmpty() && OrderStatus.SHIPPED.equals(status)) {
      throw new IllegalStateException("Cannot ship an empty order");
    }
    this.version = version;
  }

  /** Adds quantity of a product line and returns a new immutable order instance. */
  public Order add(ProductSnapshot productSnapshot, int quantity, DiscountId discountId) {
    Objects.requireNonNull(productSnapshot, "product is required");
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }

    LineKey key = new LineKey(productSnapshot, discountId);
    LinkedHashMap<LineKey, LineId> nextPrimaryLineForKey =
        new LinkedHashMap<>(this.primaryLineForKey);

    LineId id = this.primaryLineForKey.getOrDefault(key, LineId.newRandom());

    LinkedHashMap<LineId, OrderLine> nextLines = new LinkedHashMap<>(this.lines);

    nextLines.merge(
        id,
        new OrderLine(id, key, 0),
        (oldLine, newLine) -> oldLine.increaseBy(newLine.quantity()));

    return new Order(this.id, nextPrimaryLineForKey, nextLines, this.status, this.version);
  }

  /** Sets line quantity for a product and returns a new immutable order instance. */
  public Order setQuantity(ProductSnapshot product, int quantity, DiscountId discountId) {
    Objects.requireNonNull(product, "product is required");
    var key = new LineKey(product, discountId);
    LineId id = this.primaryLineForKey.getOrDefault(key, null);
    if (id == null) {
      throw new IllegalArgumentException("Product does not exist in order");
    }

    var next = new LinkedHashMap<>(this.lines);
    if (quantity <= 0) {
      next.remove(id); // common UX: set to 0 => remove
    } else {
      var existing = next.get(id);
      if (existing == null) {
        throw new IllegalArgumentException("Line not found");
      }
      next.put(id, existing.withQuantity(quantity));
    }
    return new Order(this.id, this.primaryLineForKey, next, this.status, this.version);
  }

  /** Returns the order creation time encoded in the order identifier. */
  public Instant creationInstant() {
    return id.creationInstant();
  }

  /** Returns the order identifier. */
  public OrderId id() {
    return id;
  }

  /** Returns the current lines as an immutable list copy. */
  public List<OrderLine> lines() {
    return List.copyOf(lines.values());
  }

  /** Returns the current order status. */
  public OrderStatus status() {
    return status;
  }

  /** Returns total gross amount before discount for all lines. */
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
