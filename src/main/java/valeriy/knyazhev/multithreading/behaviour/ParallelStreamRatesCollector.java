package valeriy.knyazhev.multithreading.behaviour;

import valeriy.knyazhev.multithreading.RatesCollector;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Valeriy Knyazhev
 */
public class ParallelStreamRatesCollector implements RatesCollector {

    private final ExecutorService executor;
    private final Duration waitTime;

    public ParallelStreamRatesCollector(ExecutorService executor, Duration waitTime) {
        this.executor = requireNonNull(executor);
        this.waitTime = requireNonNull(waitTime);
    }

    public Collection<Rate> collectRates(String symbol, List<RateProvider> providers) {
        final var rates = new ConcurrentLinkedQueue<Rate>();
        final Future<List<Rate>> fetchRates = this.executor.submit(() -> providers.parallelStream()
            .map(provider -> {
                final Rate rate = provider.rateFor(symbol);
                rates.add(rate);
                return rate;
            })
            .collect(Collectors.toList())
        );
        try {
            return fetchRates.get(waitTime.toMillis(), MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            return rates;
        }
    }

    private Optional<Rate> awaitForRate(Future<Rate> task) {
        try {
            return ofNullable(task.get(waitTime.toMillis(), MILLISECONDS));
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            return empty();
        }
    }

}
