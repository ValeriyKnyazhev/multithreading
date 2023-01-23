package valeriy.knyazhev.multithreading.behaviour;

import valeriy.knyazhev.multithreading.RatesCollector;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * @author Valeriy Knyazhev
 */
public class ThreadsWithCountDownLatchRatesCollector implements RatesCollector {

    private final Duration waitTime;

    public ThreadsWithCountDownLatchRatesCollector(Duration waitTime) {
        this.waitTime = requireNonNull(waitTime);
    }

    public Collection<Rate> collectRates(String symbol, List<RateProvider> providers) {
        final var latch = new CountDownLatch(providers.size());
        final var rates = new ConcurrentLinkedQueue<Rate>();
        providers.stream()
            .map(provider -> new Thread(() -> {
                rates.add(provider.rateFor(symbol));
                latch.countDown();
            }))
            .forEach(Thread::start);
        try {
            latch.await(this.waitTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return rates;
    }

}
