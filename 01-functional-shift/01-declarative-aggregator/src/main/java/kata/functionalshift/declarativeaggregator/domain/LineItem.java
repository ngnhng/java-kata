package kata.functionalshift.declarativeaggregator.domain;

import java.math.BigDecimal;

public record LineItem(Product product, int quantity) {
  public BigDecimal total() {
    return product.price().multiply(BigDecimal.valueOf(quantity));
  }
}
