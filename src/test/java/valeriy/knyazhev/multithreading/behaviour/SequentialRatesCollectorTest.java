package valeriy.knyazhev.multithreading.behaviour;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import valeriy.knyazhev.multithreading.RatesCollector;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static valeriy.knyazhev.multithreading.model.Rate.rate;

/**
 * @author Valeriy Knyazhev
 */
public class SequentialRatesCollectorTest {

    private static final String SYMBOL = "GBPUSD";
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
    private final RatesCollector collector = new SequentialRatesCollector();

    @BeforeEach
    public void setup() {
        when(firstProvider.rateFor(SYMBOL)).thenReturn(RATE_1);
        when(secondProvider.rateFor(SYMBOL)).thenReturn(RATE_2);
    }

    @Test
    public void should_collect_rates_from_all_providers_sequentially() {
        // when
        var result = collector.collectRates(SYMBOL, List.of(firstProvider, secondProvider));

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly(RATE_1, RATE_2);

        // and
        verify(firstProvider).rateFor(SYMBOL);
        verify(secondProvider).rateFor(SYMBOL);
    }

    @Test
    public void should_collect_all_rates_even_if_one_provider_responding_slowly() {
        // given
        when(firstProvider.rateFor(SYMBOL)).thenAnswer((Answer<Rate>) invocation -> {
            sleepUninterruptibly(ofSeconds(1));
            return RATE_1;
        });

        // when
        var result = collector.collectRates(SYMBOL, List.of(firstProvider, secondProvider));

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly(RATE_1, RATE_2);

        // and
        verify(firstProvider).rateFor(SYMBOL);
        verify(secondProvider).rateFor(SYMBOL);
    }

}
