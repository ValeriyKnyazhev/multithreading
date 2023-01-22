package valeriy.knyazhev.multithreading.service;

import java.time.Duration;

/**
 * @author Valeriy Knyazhev
 */
public interface DelayGenerator {

    Duration newDelay();

}
