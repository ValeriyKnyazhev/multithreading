package valeriy.knyazhev.multithreading.service;

import valeriy.knyazhev.multithreading.model.Rate;

import java.math.BigDecimal;

import static java.lang.Math.random;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.valueOf;
import static java.time.Instant.now;
import static valeriy.knyazhev.multithreading.model.Rate.rate;

/**
 * @author Valeriy Knyazhev
 */
public class RandomRateProvider implements RateProvider {

    private final Rate targetPrice;

    public RandomRateProvider(Rate targetPrice) {
        this.targetPrice = targetPrice;
    }

    @Override
    public Rate rateFor(String symbol) {
        final BigDecimal ask = targetPrice.ask.multiply(ONE.add(valueOf(random())));
        final BigDecimal bid = targetPrice.bid.multiply(ONE.subtract(valueOf(random())));
        return rate()
            .symbol(symbol)
            .ask(ask)
            .bid(bid)
            .at(now());
    }
}
