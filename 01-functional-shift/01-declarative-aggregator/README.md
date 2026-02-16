# The Declarative Aggregator

Implement a class `SalesAnalyzer` to analyze a dataset of e-commerce orders.

The goal of this kata is to rewire your brain from **Imperative** thinking (loops, mutable accumulators, `if` statements) to **Declarative** thinking (Streams, Collectors, Function Composition).

## Requirements

- Develop the `SalesAnalyzer` class level by level.
- You **MUST NOT** use `for`, `while`, or `do-while` loops.
- You **MUST NOT** use mutable local variables (e.g., `List<T> list = new ArrayList<>(); list.add(...)` is forbidden inside the logic methods).
- All tests in `SalesAnalyzerTest` MUST pass.
- Each level MUST be completed by a Git commit.

## Development

- Gradle 8.7.
- Java 21.

## Domain Model

You are provided with these immutable Records. You may not change them.

```java
public record Product(String id, String category, BigDecimal price) {}

public record LineItem(Product product, int quantity) {
    public BigDecimal total() {
        return product.price().multiply(BigDecimal.valueOf(quantity));
    }
}

public record Order(String id, LocalDate creationDate, List<LineItem> lines, String status) {
    public BigDecimal total() {
        return lines.stream()
            .map(LineItem::total)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

## Level 1 - Basic Metrics (Filtering & Mapping)

We need to extract simple metrics from a list of orders.

Implement the following methods in `SalesAnalyzer`:

```java
public class SalesAnalyzer {
    public static long countOrdersByStatus(List<Order> orders, String status);
    public static BigDecimal calculateTotalRevenue(List<Order> orders);
    public static List<Product> getDistinctProductsSold(List<Order> orders);
}
```

**Constraints:**

- Use `stream()`, `filter()`, `map()`, `flatMap()`, and `distinct()`.
- Do not use `forEach` with a side-effect (e.g., adding to an external list).

## Level 2 - Grouping Data

Management wants to see sales broken down by category.

```java
// Returns a map where Key is the Category and Value is the list of Products sold in that category.
public static Map<String, List<Product>> groupProductsByCategory(List<Order> orders);
```

**Constraints:**

- Use `Collectors.groupingBy`.
- The lists in the map should not contain duplicates (Optional hint: `Collectors.toSet()` or distinct upstream).

## Level 3 - Advanced Grouping (Downstream Collectors)

Grouping products is nice, but we actually need the **revenue** per category, not the products themselves.

```java
// Returns a map where Key is the Category and Value is the total revenue for that category.
public static Map<String, BigDecimal> calculateRevenueByCategory(List<Order> orders);
```

**Constraints:**

- You must use a "Downstream Collector".
- Do not do `groupProductsByCategory(...).entrySet().stream()...`. Do it in a single pass using `Collectors.groupingBy(classifier, downstream)`.
- Be careful with `BigDecimal` accumulation.

## Level 4 - Partitioning & Summarizing

We need to analyze "High Value" orders vs "Standard" orders. A "High Value" order is defined as an order with a total greater than a given threshold.

```java
// Returns a map with exactly two keys: true (High Value) and false (Standard).
// The value is the count of orders in each partition.
public static Map<Boolean, Long> partitionOrdersByValue(List<Order> orders, BigDecimal threshold);

// Returns statistics (Min, Max, Average, Count, Sum) for the prices of all products sold.
public static DoubleSummaryStatistics getProductPriceStatistics(List<Order> orders);
```

**Constraints:**

- Use `Collectors.partitioningBy`.
- Use `Collectors.summarizingDouble`.

## Level 5 - The "Teeing" Collector (Java 12+)

We need a complex report generated in a single pass. We want to find the **Best Selling Product** (by quantity) and the **Most Expensive Order** simultaneously.

```java
public record MarketSnapshot(Product bestSeller, Order mostExpensive) {}

public static MarketSnapshot getMarketSnapshot(List<Order> orders);
```

**Constraints:**

- You must use `Collectors.teeing()`.
- Do not iterate the stream twice.
- Handle empty lists gracefully (using `Optional` or defaults).

## Level 6 - Top K Trends

We need to find the Top 3 Categories by revenue.

```java
public static List<String> getTopThreeCategories(List<Order> orders);
```

**Constraints:**

- The result must be sorted by Revenue (Descending).
- Limit the result to 3.
- This tests your ability to mix collection, sorting, and limiting in the pipeline.

## Level 7 - Aggregation as a Service

Expose the `SalesAnalyzer` capabilities via a simple HTTP API.

- Endpoint: `POST /api/analytics/revenue-by-category`
- Input: JSON List of Orders.
- Output: JSON Map `<String, BigDecimal>`.

**Requirements:**

- Use any library (Javalin, Spring Boot, Quarkus, or raw `com.sun.net.httpserver`).
- You must deserialize the JSON into the Record structures defined above.
- The logic must delegate to your `SalesAnalyzer` class.

## Level 8 - Further Discussions

These questions are reserved for the Technical Interview.

a. In Level 3, you used `BigDecimal`. When using `Collectors.summingDouble`, precision is lost. How do you implement a custom `Collector` or use `reduce` to sum `BigDecimal`s cleanly within a grouping operation without losing precision?

b. Explain the difference between `Stream.reduce` and `Stream.collect`. Why is `collect` preferred for mutable container aggregation (like Lists or Maps) while `reduce` is preferred for immutable values (like Integers)?

c. `parallelStream()` can speed up aggregation on large datasets. However, if you use a custom Collector that is not `CONCURRENT`, what happens? How does the ForkJoinPool merge the results?

d. In Level 5 (`teeing`), we processed the stream once. If we had written two separate stream pipelines (one for best seller, one for most expensive), what is the performance impact? When does the cost of re-streaming outweigh the complexity of `teeing`?

e. How would you handle an infinite Stream of Orders? (e.g., a live Kafka feed). Which of the methods implemented above would break, and which could be adapted to a "Windowed" approach?
