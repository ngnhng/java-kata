package kata.functionalshift.declarativeaggregator.domain;

import java.math.BigDecimal;

public record Product(String id, String category, BigDecimal price) {}
