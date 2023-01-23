package valeriy.knyazhev.multithreading.behaviour;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.stubbing.Answer;
import valeriy.knyazhev.multithreading.RatesCollector;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static valeriy.knyazhev.multithreading.model.Rate.rate;

/**
 * @author Valeriy Knyazhev
 */
public class ParallelRatesCollectorTest {

    private static final String SYMBOL = "GBPUSD";
    private static final Duration WAIT_TIME = Duration.ofMillis(500);
    private static final Rate RATE_1 = rate()
        .symbol(SYMBOL)
        .ask(new BigDecimal("1.215"))
        .bid(new BigDecimal("1.205"))
        .build();
    private static final Rate RATE_2 = rate()
        .symbol(SYMBOL)
        .ask(new BigDecimal("1.212"))
        .bid(new BigDecimal("1.202"))
        .build();

    private final RateProvider firstProvider = mock(RateProvider.class);
    private final RateProvider secondProvider = mock(RateProvider.class);

    @BeforeEach
    public void setup() {
        when(firstProvider.rateFor(SYMBOL)).thenReturn(RATE_1);
        when(secondProvider.rateFor(SYMBOL)).thenReturn(RATE_2);
    }

    @ParameterizedTest
    @MethodSource("parallelCollectorImplementations")
    public void should_collect_rates_from_all_providers_sequentially(RatesCollector collector) {
        // when
        var result = collector.collectRates(SYMBOL, List.of(firstProvider, secondProvider));

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactlyInAnyOrder(RATE_1, RATE_2);

        // and
        verify(firstProvider).rateFor(SYMBOL);
        verify(secondProvider).rateFor(SYMBOL);
    }

    @ParameterizedTest
    @MethodSource("parallelCollectorImplementations")
    public void should_not_collect_rates_from_timed_out_providers(RatesCollector collector) {
        // given
        when(firstProvider.rateFor(SYMBOL)).thenAnswer((Answer<Rate>) invocation -> {
            sleepUninterruptibly(WAIT_TIME.multipliedBy(2));
            return RATE_1;
        });

        // when
        var result = collector.collectRates(SYMBOL, List.of(firstProvider, secondProvider));

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).containsOnly(RATE_2);

        // and
        verify(firstProvider).rateFor(SYMBOL);
        verify(secondProvider).rateFor(SYMBOL);
    }

    private static Stream<Arguments> parallelCollectorImplementations() {
        return Stream.of(
            Arguments.of(new ThreadsWithJoinRatesCollector(WAIT_TIME)),
            Arguments.of(new ThreadsWithCountDownLatchRatesCollector(WAIT_TIME))
        );
    }

}
