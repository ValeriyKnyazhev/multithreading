package valeriy.knyazhev.multithreading.service.behaviour;

import valeriy.knyazhev.multithreading.service.DelayGenerator;

import java.time.Duration;

/**
 * @author Valeriy Knyazhev
 */
public class NoDelayGenerator implements DelayGenerator {

    private static final Duration ZERO_DELAY = Duration.ZERO;

    @Override
    public Duration newDelay() {
        return ZERO_DELAY;
    }

}
