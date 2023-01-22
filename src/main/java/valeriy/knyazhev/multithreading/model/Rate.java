package valeriy.knyazhev.multithreading.model;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.requireNonNull;

/**
 * @author Valeriy Knyazhev
 */
public final class Rate {

    public final String symbol;
    public final BigDecimal ask;
    public final BigDecimal bid;

    private Rate(String symbol, BigDecimal ask, BigDecimal bid) {
        this.symbol = requireNonNull(symbol);
        if (ask.compareTo(bid) < 0) {
            throw new IllegalArgumentException("ask must be greater or equal to bid");
        }
        this.ask = requirePositive("ask", ask);
        this.bid = requirePositive("bid", bid);
    }

    public static Builder rate() {
        return new Builder();
    }

    public static class Builder {
        private String symbol;
        private BigDecimal ask;
        private BigDecimal bid;

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder ask(BigDecimal ask) {
            this.ask = ask;
            return this;
        }

        public Builder bid(BigDecimal bid) {
            this.bid = bid;
            return this;
        }

        public Rate build() {
            return new Rate(symbol, ask, bid);
        }
    }

    private static BigDecimal requirePositive(String name, BigDecimal value) {
        if (value == null || value.compareTo(ZERO) <= 0) {
            throw new IllegalStateException(format("%s value must be positive", name));
        }
        return value;
    }

}
