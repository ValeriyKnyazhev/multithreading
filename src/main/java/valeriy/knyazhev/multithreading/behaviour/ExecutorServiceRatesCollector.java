package valeriy.knyazhev.multithreading.behaviour;

import valeriy.knyazhev.multithreading.RatesCollector;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
public class ExecutorServiceRatesCollector implements RatesCollector {

    private final ExecutorService executor;
    private final Duration waitTime;

    public ExecutorServiceRatesCollector(ExecutorService executor, Duration waitTime) {
        this.executor = requireNonNull(executor);
        this.waitTime = requireNonNull(waitTime);
    }

    public Collection<Rate> collectRates(String symbol, List<RateProvider> providers) {
        final var tasks = providers.stream()
            .map(provider -> executor.submit(() -> provider.rateFor(symbol)))
            .collect(Collectors.toList());
        return tasks.stream()
            .flatMap(task -> awaitForRate(task).stream())
            .collect(Collectors.toList());
    }

    private Optional<Rate> awaitForRate(Future<Rate> task) {
        try {
            return ofNullable(task.get(waitTime.toMillis(), MILLISECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            return empty();
        }
    }

}
