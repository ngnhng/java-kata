package kata.functionalshift.declarativeaggregator.solution;

import java.math.BigDecimal;
import java.util.List;
import kata.functionalshift.declarativeaggregator.domain.Order;
import kata.functionalshift.declarativeaggregator.domain.OrderStatus;
import kata.functionalshift.declarativeaggregator.domain.vo.ProductSnapshot;

/** SalesAnalyzer sample solution by author. */
public class SalesAnalyzer {

  // --- Level 1 ---

  /**
   * Tries to count the number of orders with the provided status.
   *
   * @param orders The order List.
   * @param status The status to be counted against.
   * @return The number of order with the status.
   */
  public static long countOrdersByStatus(List<Order> orders, OrderStatus status) {
    return orders.stream().distinct().filter((o) -> o.status().equals(status)).count();
  }

  /**
   * Calculates total gross revenue from all non-empty orders.
   *
   * @param orders The order List.
   * @return The gross revenue before discounts.
   */
  public static BigDecimal calculateTotalRevenue(List<Order> orders) {
    return orders.stream()
        .distinct()
        .filter(order -> !order.lines().isEmpty())
        .map(o -> o.totalBeforeDiscount())
        .map(money -> money.amount())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Collects distinct products sold across all order lines.
   *
   * @param orders The order List.
   * @return The distinct products present in sales.
   */
  public static List<ProductSnapshot> getDistinctProductsSold(List<Order> orders) {
    return orders.stream()
        .flatMap(o -> o.lines().stream())
        .distinct()
        .map(line -> line.key().productSnapshot())
        .distinct()
        .toList();
  }

  // --- Level 2 ----
}
