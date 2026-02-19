package kata.functionalshift.declarativeaggregator.domain.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/** Value object for an amount and currency pair. */
public record Money(BigDecimal amount, Currency currency) {
  /** Creates money and normalizes amount scale using the currency defaults. */
  public Money {
    Objects.requireNonNull(amount, "amount is required");
    Objects.requireNonNull(currency, "currency is required");

    // Normalize scale (most currencies use 2; some don't, but this is a sane default)
    int scale = Math.max(currency.getDefaultFractionDigits(), 0);
    amount = amount.setScale(scale, RoundingMode.HALF_UP);
  }

  /** Factory method to create money from amount and currency values. */
  public static Money of(BigDecimal amount, Currency currency) {
    return new Money(amount, currency);
  }

  /** Ensures this amount is non-negative or throws with the supplied message. */
  public Money requireNonNegative(String message) {
    if (amount.signum() < 0) {
      throw new IllegalArgumentException(message);
    }
    return this;
  }

  /** Adds another money value in the same currency. */
  public Money add(Money other) {
    requireSameCurrency(other);
    return new Money(this.amount.add(other.amount), this.currency);
  }

  /** Multiplies this amount by a non-negative integer factor. */
  public Money multiply(int factor) {
    if (factor < 0) {
      throw new IllegalArgumentException("factor must be >= 0");
    }
    return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), this.currency);
  }

  /** Validates currency compatibility for arithmetic operations. */
  private void requireSameCurrency(Money other) {
    if (!this.currency.equals(other.currency)) {
      throw new IllegalArgumentException("Currency mismatch");
    }
  }
}
