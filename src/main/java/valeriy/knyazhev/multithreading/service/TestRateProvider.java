package valeriy.knyazhev.multithreading.service;

import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.utils.RandomGenerator;

import java.math.BigDecimal;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.math.BigDecimal.valueOf;
import static java.util.Objects.requireNonNull;
import static valeriy.knyazhev.multithreading.model.Rate.rate;

/**
 * @author Valeriy Knyazhev
 */
public class TestRateProvider implements RateProvider {

    private final Rate targetPrice;
    private final RandomGenerator random;
    private final DelayGenerator delayGenerator;

    public TestRateProvider(Rate targetPrice, DelayGenerator delayGenerator) {
        this.targetPrice = requireNonNull(targetPrice);
        this.random = new RandomGenerator();
        this.delayGenerator = requireNonNull(delayGenerator);
    }

    @Override
    public Rate rateFor(String symbol) {
        sleepUninterruptibly(delayGenerator.newDelay());

        final BigDecimal ask = targetPrice.ask.multiply(valueOf(1.0 + random.random()));
        final BigDecimal bid = targetPrice.bid.multiply(valueOf(1.0 - random.random()));
        return rate()
            .symbol(symbol)
            .ask(ask)
            .bid(bid)
            .build();
    }
}
