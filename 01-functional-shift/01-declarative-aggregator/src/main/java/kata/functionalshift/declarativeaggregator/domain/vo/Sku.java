package kata.functionalshift.declarativeaggregator.domain.vo;

import java.util.Objects;
import java.util.regex.Pattern;

public record Sku(String value) {
  private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9_-]{1,63}$");

  public Sku {
    Objects.requireNonNull(value, "sku is required");
    var v = value.trim().toUpperCase();
    if (!SKU_PATTERN.matcher(v).matches()) {
      throw new IllegalArgumentException("Invalid SKU format");
    }
    value = v;
  }
}
