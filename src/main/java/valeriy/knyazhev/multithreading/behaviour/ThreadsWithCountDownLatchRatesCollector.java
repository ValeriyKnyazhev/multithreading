package valeriy.knyazhev.multithreading.behaviour;

import valeriy.knyazhev.multithreading.RatesCollector;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author Valeriy Knyazhev
 */
public class ThreadsWithCountDownLatchRatesCollector implements RatesCollector {

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
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return rates;
    }

}
