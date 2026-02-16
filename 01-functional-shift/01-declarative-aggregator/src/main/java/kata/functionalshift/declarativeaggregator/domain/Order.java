package kata.functionalshift.declarativeaggregator.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record Order(String id, LocalDate creationDate, List<LineItem> lines, String status) {
  public BigDecimal total() {
    return lines.stream().map(LineItem::total).reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
