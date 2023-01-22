package valeriy.knyazhev.multithreading;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static valeriy.knyazhev.multithreading.model.Rate.rate;

/**
 * @author Valeriy Knyazhev
 */
public class RateServiceTest {

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

    private final List<RateProvider> providers = List.of(
        mock(RateProvider.class), mock(RateProvider.class)
    );
    private final RatesCollector collector = mock(RatesCollector.class);

    private final RateService service = new RateService(providers, collector);

    @BeforeEach
    public void setup() {
        when(collector.collectRates(SYMBOL, providers)).thenReturn(List.of(RATE_1, RATE_2));
    }

    @Test
    public void should_return_best_rate_as_a_combination_of_best_bid_and_ask_from_different_providers() {
        // when
        var result = service.bestRateFor(SYMBOL);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(rate()
            .symbol(SYMBOL)
            .ask(new BigDecimal("1.212"))
            .bid(new BigDecimal("1.205"))
            .build()
        );

        // and
        verify(collector).collectRates(eq(SYMBOL), any());
    }

}
