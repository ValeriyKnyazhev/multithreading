package valeriy.knyazhev.multithreading.service.behaviour;

import valeriy.knyazhev.multithreading.service.DelayGenerator;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

/**
 * @author Valeriy Knyazhev
 */
public class ConstantDelayGenerator implements DelayGenerator {

    private final Duration delay;

    public ConstantDelayGenerator(Duration delay) {
        this.delay = requireNonNull(delay);
    }
    @Override
    public Duration newDelay() {
        return this.delay;
    }

}
