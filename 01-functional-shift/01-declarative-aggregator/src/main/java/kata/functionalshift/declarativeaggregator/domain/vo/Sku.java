package kata.functionalshift.declarativeaggregator.domain.vo;

import java.util.Objects;
import java.util.regex.Pattern;

/** Value object representing a stock keeping unit code. */
public record Sku(String value) {
  private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9_-]{1,63}$");

  /** Creates a normalized SKU in uppercase and validates its format. */
  public Sku {
    Objects.requireNonNull(value, "sku is required");
    var v = value.trim().toUpperCase();
    if (!SKU_PATTERN.matcher(v).matches()) {
      throw new IllegalArgumentException("Invalid SKU format");
    }
    value = v;
  }
}
