package valeriy.knyazhev.multithreading.service;

import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.utils.RandomGenerator;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.math.BigDecimal.valueOf;
import static java.util.Objects.requireNonNull;
import static valeriy.knyazhev.multithreading.model.Rate.rate;

/**
 * @author Valeriy Knyazhev
 */
public class TestRateProvider implements RateProvider {

    private static final int NUMBER_OF_GENERATED_RATES = 10000;

    private final DelayGenerator delayGenerator;
    private final Rate[] generatedRates = new Rate[NUMBER_OF_GENERATED_RATES];
    private final AtomicInteger counter = new AtomicInteger(0);

    public TestRateProvider(Rate targetPrice, DelayGenerator delayGenerator) {
        this.delayGenerator = requireNonNull(delayGenerator);

        final var random = new RandomGenerator();
        IntStream.range(0, NUMBER_OF_GENERATED_RATES)
            .forEach(i -> generatedRates[i] = generateRate(targetPrice, random));
    }

    @Override
    public Rate rateFor(String symbol) {
        sleepUninterruptibly(delayGenerator.newDelay());

        return this.generatedRates[counter.getAndIncrement() % NUMBER_OF_GENERATED_RATES];
    }

    private static Rate generateRate(Rate targetPrice, RandomGenerator random) {
        final BigDecimal ask = targetPrice.ask.multiply(valueOf(1.0 + random.random()));
        final BigDecimal bid = targetPrice.bid.multiply(valueOf(1.0 - random.random()));
        return rate()
            .symbol(targetPrice.symbol)
            .ask(ask)
            .bid(bid)
            .build();
    }

}
