package valeriy.knyazhev.multithreading.behaviour;

import valeriy.knyazhev.multithreading.RatesCollector;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * @author Valeriy Knyazhev
 */
public class ThreadsWithJoinRatesCollector implements RatesCollector {

    private final Duration waitTime;

    public ThreadsWithJoinRatesCollector(Duration waitTime) {
        this.waitTime = requireNonNull(waitTime);
    }

    public Collection<Rate> collectRates(String symbol, List<RateProvider> providers) {
        final var rates = new ConcurrentLinkedQueue<Rate>();
        final var threads = providers.stream()
            .map(provider -> new Thread(() -> rates.add(provider.rateFor(symbol))))
            .collect(Collectors.toList());
        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join(this.waitTime.toMillis());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });
        return rates;
    }

}
