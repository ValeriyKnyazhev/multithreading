package valeriy.knyazhev.multithreading.service;

import valeriy.knyazhev.multithreading.model.Rate;

import java.time.Duration;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.lang.Math.random;
import static java.time.temporal.ChronoUnit.NANOS;

/**
 * @author Valeriy Knyazhev
 */
public class DelayedRandomRateProvider extends RandomRateProvider {

    private final Duration minDelay;
    private final Duration maxDelay;

    public DelayedRandomRateProvider(Rate targetRate, Duration constantDelay) {
        this(targetRate, constantDelay, constantDelay);
    }

    public DelayedRandomRateProvider(Rate targetRate, Duration minDelay, Duration maxDelay) {
        super(targetRate);
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
    }

    @Override
    public Rate rateFor(String symbol) {
        sleepUninterruptibly(calculateDelay());
        return super.rateFor(symbol);
    }

    private Duration calculateDelay() {
        final var diff = maxDelay.toNanos() - minDelay.toNanos();
        final long extraDelay = (long) (random() * diff);
        return minDelay.plus(extraDelay, NANOS);
    }
}
