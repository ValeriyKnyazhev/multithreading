package valeriy.knyazhev.multithreading.service.behaviour;

import valeriy.knyazhev.multithreading.service.DelayGenerator;
import valeriy.knyazhev.multithreading.utils.RandomGenerator;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author Valeriy Knyazhev
 */
public class RandomDelayGenerator implements DelayGenerator {

    private final RandomGenerator random;
    private final long minDelayInMicros;
    private final long delayWindowSizeInMicros;

    public RandomDelayGenerator(Duration minDelay, Duration maxDelay) {
        this.random = new RandomGenerator();
        this.minDelayInMicros = minDelay.toNanos() / 1000;
        this.delayWindowSizeInMicros = maxDelay.minus(minDelay).toNanos() / 1000;
    }
    @Override
    public Duration newDelay() {
        return Duration.of(minDelayInMicros + (long) (delayWindowSizeInMicros * random.random()), ChronoUnit.MICROS);
    }
}
