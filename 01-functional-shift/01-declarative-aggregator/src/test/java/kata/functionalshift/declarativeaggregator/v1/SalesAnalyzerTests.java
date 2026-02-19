package kata.functionalshift.declarativeaggregator.v1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.uuid.Generators;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kata.functionalshift.declarativeaggregator.domain.Order;
import kata.functionalshift.declarativeaggregator.domain.OrderStatus;
import kata.functionalshift.declarativeaggregator.domain.vo.LineId;
import kata.functionalshift.declarativeaggregator.domain.vo.LineKey;
import kata.functionalshift.declarativeaggregator.domain.vo.Money;
import kata.functionalshift.declarativeaggregator.domain.vo.OrderId;
import kata.functionalshift.declarativeaggregator.domain.vo.OrderLine;
import kata.functionalshift.declarativeaggregator.domain.vo.ProductSnapshot;
import kata.functionalshift.declarativeaggregator.domain.vo.Sku;
import kata.functionalshift.declarativeaggregator.solution.SalesAnalyzer;
import org.junit.jupiter.api.Test;

class SalesAnalyzerTests {
  private static final Currency USD = Currency.getInstance("USD");
  private static final Map<String, UUID> IDS = new HashMap<>();

  @Test
  void countOrdersByStatusCountsOnlyDistinctOrdersWithMatchingStatus() {
    Order newOrder = order("o-1", OrderStatus.NEW, lineItem(product("p-1", "10.00"), 1));
    Order duplicateNewOrder = order("o-1", OrderStatus.NEW, lineItem(product("p-1", "10.00"), 1));
    Order shippedOrder = order("o-2", OrderStatus.SHIPPED, lineItem(product("p-2", "20.00"), 2));

    long newCount =
        SalesAnalyzer.countOrdersByStatus(
            List.of(newOrder, duplicateNewOrder, shippedOrder), OrderStatus.NEW);

    long shippedCount =
        SalesAnalyzer.countOrdersByStatus(
            List.of(newOrder, duplicateNewOrder, shippedOrder), OrderStatus.SHIPPED);

    assertThat(newCount).isEqualTo(1L);
    assertThat(shippedCount).isEqualTo(1L);
  }

  @Test
  void calculateTotalRevenueSumsTotalsOfDistinctOrders() {
    Order first = order("o-1", OrderStatus.NEW, lineItem(product("p-1", "10.00"), 2));
    Order firstDuplicate = order("o-1", OrderStatus.NEW, lineItem(product("p-1", "10.00"), 2));
    Order second =
        order(
            "o-2",
            OrderStatus.RECEIVED,
            lineItem(product("p-2", "20.00"), 1),
            lineItem(product("p-3", "7.50"), 2));

    BigDecimal revenue =
        SalesAnalyzer.calculateTotalRevenue(List.of(first, firstDuplicate, second));

    assertThat(revenue).isEqualByComparingTo(new BigDecimal("55.00"));
  }

  @Test
  void getDistinctProductsSoldReturnsUniqueProductsAcrossAllOrders() {
    ProductSnapshot book = product("p-1", "10.00");
    ProductSnapshot laptop = product("p-2", "900.00");
    ProductSnapshot equalLaptop = product("p-2", "900.00");

    Order first = order("o-1", OrderStatus.NEW, lineItem(book, 1), lineItem(laptop, 1));
    Order second = order("o-2", OrderStatus.SHIPPED, lineItem(book, 3), lineItem(equalLaptop, 2));

    List<ProductSnapshot> products = SalesAnalyzer.getDistinctProductsSold(List.of(first, second));

    assertThat(products).containsExactly(book, laptop);
  }

  @Test
  void getDistinctProductsSoldStillReturnsUniqueProductsAcrossDifferentCreationDates() {
    ProductSnapshot book = product("p-1", "10.00");

    Order firstDay = order("o-1", OrderStatus.NEW, lineItem(book, 1));
    Order secondDay = order("o-2", OrderStatus.NEW, lineItem(book, 2));

    List<ProductSnapshot> products =
        SalesAnalyzer.getDistinctProductsSold(List.of(firstDay, secondDay));

    assertThat(products).containsExactly(book);
  }

  @Test
  void groupOrderIdsByStatusReturnsCorrectMappingAcrossOrders() {
    Order firstNew = order("o-1", OrderStatus.NEW, lineItem(product("p-1", "10.00"), 1));
    Order shipped = order("o-2", OrderStatus.SHIPPED, lineItem(product("p-2", "20.00"), 1));
    Order secondNew = order("o-3", OrderStatus.NEW, lineItem(product("p-3", "30.00"), 1));
    Order firstDuplicate = order("o-1", OrderStatus.NEW, lineItem(product("p-1", "10.00"), 1));

    Map<OrderStatus, List<OrderId>> mapping =
        SalesAnalyzer.groupOrderIdsByStatus(List.of(firstNew, shipped, secondNew, firstDuplicate));

    assertThat(mapping).containsOnlyKeys(OrderStatus.NEW, OrderStatus.SHIPPED);
    assertThat(mapping.get(OrderStatus.NEW)).containsExactly(firstNew.id(), secondNew.id());
    assertThat(mapping.get(OrderStatus.SHIPPED)).containsExactly(shipped.id());
  }

  @Test
  void calculateRevenueByStatusReturnsEmptyMapForEmptyInput() {
    Map<OrderStatus, BigDecimal> revenueByStatus =
        SalesAnalyzer.calculateRevenueByStatus(List.of());

    assertThat(revenueByStatus).isEmpty();
  }

  @Test
  void calculateRevenueByStatusSumsTotalsPerStatusAcrossDistinctOrders() {
    Order firstNew = order("o-1", OrderStatus.NEW, lineItem(product("p-1", "10.00"), 2));
    Order firstDuplicate = order("o-1", OrderStatus.NEW, lineItem(product("p-1", "10.00"), 2));
    Order secondNew = order("o-2", OrderStatus.NEW, lineItem(product("p-2", "7.50"), 2));
    Order shipped = order("o-3", OrderStatus.SHIPPED, lineItem(product("p-3", "20.00"), 1));

    Map<OrderStatus, BigDecimal> revenueByStatus =
        SalesAnalyzer.calculateRevenueByStatus(
            List.of(firstNew, firstDuplicate, secondNew, shipped));

    assertThat(revenueByStatus)
        .containsEntry(OrderStatus.NEW, new BigDecimal("35.00"))
        .containsEntry(OrderStatus.SHIPPED, new BigDecimal("20.00"));
  }

  @Test
  void calculateRevenueByStatusOmitsStatusesWithoutOrders() {
    Order newOrder = order("o-1", OrderStatus.NEW, lineItem(product("p-1", "10.00"), 1));

    Map<OrderStatus, BigDecimal> revenueByStatus =
        SalesAnalyzer.calculateRevenueByStatus(List.of(newOrder));

    assertThat(revenueByStatus).containsOnlyKeys(OrderStatus.NEW);
  }

  @Test
  void calculateRevenueByStatusThrowsWhenOrderHasNoLines() {
    Order emptyOrder = order("o-1", OrderStatus.NEW);

    assertThatThrownBy(() -> SalesAnalyzer.calculateRevenueByStatus(List.of(emptyOrder)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Cannot total an empty order");
  }

  private static Order order(String orderKey, OrderStatus status, OrderLine... lines) {
    Map<LineKey, LineId> primaryLineForKey = new LinkedHashMap<>();
    Map<LineId, OrderLine> lineMap = new LinkedHashMap<>();
    for (OrderLine line : lines) {
      primaryLineForKey.put(line.key(), line.id());
      lineMap.put(line.id(), line);
    }
    return new Order(new OrderId(uuidV7(orderKey)), primaryLineForKey, lineMap, status, 0);
  }

  private static UUID uuidV7(String orderKey) {
    return IDS.computeIfAbsent(
        orderKey, ignored -> Generators.timeBasedEpochRandomGenerator().generate());
  }

  private static OrderLine lineItem(ProductSnapshot product, int quantity) {
    return new OrderLine(LineId.newRandom(), new LineKey(product, null), quantity);
  }

  private static ProductSnapshot product(String id, String price) {
    return new ProductSnapshot(new Sku(id), Money.of(new BigDecimal(price), USD));
  }
}
