# The Declarative Aggregator

Implement a class `SalesAnalyzer` to analyze a dataset of e-commerce orders.

The goal of this kata is to rewire your brain from **Imperative** thinking (loops, mutable accumulators, `if` statements) to **Declarative** thinking (Streams, Collectors, Function Composition).

## Requirements

- Develop the `SalesAnalyzer` class level by level.
- You **MUST NOT** use `for`, `while`, or `do-while` loops.
- You **MUST NOT** use mutable local variables (e.g., `List<T> list = new ArrayList<>(); list.add(...)` is forbidden inside the logic methods).
- All tests in `SalesAnalyzerTests` MUST pass.
- Each level MUST be completed by a Git commit.

## Development

- Gradle 8.7.
- Java 21.

## Domain Model

You are provided with an immutable domain model (class + value objects). You may not change them.

Key types used by the kata:

```java
public final class Order {
    public OrderId id();
    public Instant creationInstant();
    public OrderStatus status();
    public List<OrderLine> lines();
    public Money totalBeforeDiscount();
}

public enum OrderStatus { NEW, PAID, SHIPPED, RECEIVED }

public record OrderLine(LineId id, LineKey key, int quantity) {}
public record LineKey(ProductSnapshot productSnapshot, DiscountId discountId) {}
public record ProductSnapshot(Sku sku, Money unitPrice) {}
public record Money(BigDecimal amount, Currency currency) {}
public record Sku(String value) {}
public record OrderId(UUID uuidv7) {}
public record LineId(UUID uuidv7) {}
public record DiscountId(UUID value) {}
```

Domain notes:

- `Order` identity is `OrderId` (UUIDv7). `equals/hashCode` are based on `id`.
- `OrderLine` does not expose `product` directly; access product data via
  `line.key().productSnapshot()`.
- `LineKey.discountId()` can be `null` (means "no discount").
- `Money` is currency-aware. Aggregate only comparable values (same currency).

## Level 1 - Basic Metrics (Filtering & Mapping)

We need to extract simple metrics from a list of orders.

Implement the following methods in `SalesAnalyzer`:

```java
public class SalesAnalyzer {
    public static long countOrdersByStatus(List<Order> orders, OrderStatus status);
    public static BigDecimal calculateTotalRevenue(List<Order> orders);
    public static List<ProductSnapshot> getDistinctProductsSold(List<Order> orders);
}
```

**Constraints:**

- Use `stream()`, `filter()`, `map()`, `flatMap()`, and `distinct()`.
- Do not use `forEach` with a side-effect (e.g., adding to an external list).
- For order-level metrics, deduplicate with `distinct()` first (same `OrderId` = same order).

## Level 2 - Grouping Data

Management wants to see orders broken down by status.

```java
// Returns a map where key is status and value is the list of matching order IDs.
public static Map<OrderStatus, List<OrderId>> groupOrderIdsByStatus(List<Order> orders);
```

**Constraints:**

- Use `Collectors.groupingBy`.
- The lists in the map should not contain duplicates (hint: distinct upstream or downstream).
- Group by `order.status()` and map to `order.id()`.

## Level 3 - Advanced Grouping (Downstream Collectors)

Grouping orders is useful, but now we need the **revenue** per order status.

```java
// Returns a map where key is status and value is the total revenue for that status.
public static Map<OrderStatus, BigDecimal> calculateRevenueByStatus(List<Order> orders);
```

**Constraints:**

- You must use a "Downstream Collector".
- Do not do `groupOrderIdsByStatus(...).entrySet().stream()...`. Do it in a single pass using `Collectors.groupingBy(classifier, downstream)`.
- Be careful with `BigDecimal` accumulation from `Money.amount()`.
- Revenue source is `order.totalBeforeDiscount().amount()`.

## Level 4 - Partitioning & Summarizing

We need to analyze "High Value" orders vs "Standard" orders. A "High Value" order has `totalBeforeDiscount()` greater than a given threshold.

```java
// Returns a map with exactly two keys: true (High Value) and false (Standard).
// The value is the count of orders in each partition.
public static Map<Boolean, Long> partitionOrdersByValue(List<Order> orders, Money threshold);

// Returns statistics (Min, Max, Average, Count, Sum) for quantities across all order lines.
public static IntSummaryStatistics getLineQuantityStatistics(List<Order> orders);
```

**Constraints:**

- Use `Collectors.partitioningBy`.
- Use `Collectors.summarizingInt`.
- Compare value with `order.totalBeforeDiscount().amount()` and `threshold.amount()`.
- Quantity stats come from `order.lines()` then `OrderLine::quantity`.

## Level 5 - The "Teeing" Collector (Java 12+)

We need a complex report generated in a single pass. We want to find the **Best Selling SKU** (by quantity) and the **Highest-Value Order** simultaneously.

```java
public record MarketSnapshot(Sku bestSellerSku, Order highestValueOrder) {}

public static MarketSnapshot getMarketSnapshot(List<Order> orders);
```

**Constraints:**

- You must use `Collectors.teeing()`.
- Do not iterate the stream twice.
- Handle empty lists gracefully (using `Optional` or defaults).

## Level 6 - Top K Trends

We need to find the Top 3 SKUs by revenue.

```java
public static List<Sku> getTopThreeSkusByRevenue(List<Order> orders);
```

**Constraints:**

- The result must be sorted by Revenue (Descending).
- Limit the result to 3.
- This tests your ability to mix collection, sorting, and limiting in the pipeline.
- SKU lives at `line.key().productSnapshot().sku()`.
- Line revenue lives at `line.totalBeforeDiscount().amount()`.

## Level 7 - Aggregation as a Service

Expose `SalesAnalyzer` capabilities via a simple HTTP API.

- Endpoint: `POST /api/analytics/revenue-by-status`
- Input: JSON List of Orders.
- Output: JSON Map `<OrderStatus, BigDecimal>`.

**Requirements:**

- Use any library (Javalin, Spring Boot, Quarkus, or raw `com.sun.net.httpserver`).
- You must deserialize JSON into the provided domain/VO structures.
- The logic must delegate to your `SalesAnalyzer` class.

## Level 8 - Further Discussions

These questions are reserved for the technical interview.

a. In Level 3, you use `BigDecimal` from `Money`. `Collectors.summingDouble` loses precision. How would you implement a custom `Collector` (or `reduce`) that keeps precision while grouping?

b. Explain the difference between `Stream.reduce` and `Stream.collect`. Why is `collect` preferred for mutable container aggregation (Lists/Maps) while `reduce` is preferred for immutable values?

c. `parallelStream()` can speed up aggregation on large datasets. However, if you use a custom Collector that is not `CONCURRENT`, what happens? How does the ForkJoinPool merge the results?

d. In Level 5 (`teeing`), we process the stream once. If we wrote two separate pipelines (one for best seller SKU, one for highest-value order), what is the performance impact? When does re-streaming cost more than `teeing` complexity?

e. How would you handle an infinite Stream of Orders? (e.g., a live Kafka feed). Which of the methods implemented above would break, and which could be adapted to a "Windowed" approach?
