package valeriy.knyazhev.multithreading.behaviour;

import valeriy.knyazhev.multithreading.RatesCollector;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Valeriy Knyazhev
 */
public class CompletableFutureRatesCollector implements RatesCollector {

    private final ExecutorService executor;
    private final Duration waitTime;

    public CompletableFutureRatesCollector(ExecutorService executor, Duration waitTime) {
        this.executor = requireNonNull(executor);
        this.waitTime = requireNonNull(waitTime);
    }

    public Collection<Rate> collectRates(String symbol, List<RateProvider> providers) {
        final var tasks = providers.stream()
            .map(provider -> CompletableFuture.supplyAsync(() -> provider.rateFor(symbol), executor))
            .collect(Collectors.toList());
        final var fetchRates = CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new));
        try {
            fetchRates.get(waitTime.toMillis(), MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException ignored) {
        }
        return tasks.stream()
            .filter(task -> task.isDone() && !task.isCompletedExceptionally())
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
    }

}
