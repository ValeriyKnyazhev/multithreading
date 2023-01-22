package valeriy.knyazhev.multithreading;

import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.naturalOrder;
import static java.util.Optional.empty;
import static valeriy.knyazhev.multithreading.model.Rate.rate;

/**
 * @author Valeriy Knyazhev
 */
public class RateService {

    private final List<RateProvider> providers;

    public RateService(List<RateProvider> providers) {
        this.providers = requireNonEmpty(providers);
    }

    public Optional<Rate> bestRateFor(String symbol) {
        final var rates = providers.stream()
            .map(provider -> provider.rateFor(symbol))
            .collect(Collectors.toList());

        if (rates.isEmpty()) {
            return empty();
        }

        final var bestAsk = rates.stream()
            .map(rate -> rate.ask)
            .min(naturalOrder()).get();
        final var bestBid = rates.stream()
            .map(rate -> rate.bid)
            .max(naturalOrder())
            .get();
        return Optional.of(
            rate()
                .symbol(symbol)
                .ask(bestAsk)
                .bid(bestBid)
                .build()
        );
    }

    private static List<RateProvider> requireNonEmpty(List<RateProvider> providers) {
        if (providers == null || providers.isEmpty()) {
            throw new IllegalStateException("providers must be non empty");
        }
        return providers;
    }
}
