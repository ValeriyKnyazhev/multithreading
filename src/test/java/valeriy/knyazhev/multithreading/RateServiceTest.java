package valeriy.knyazhev.multithreading;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
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

    private final RateProvider firstRateProvider = mock(RateProvider.class);
    private final RateProvider secondRateProvider = mock(RateProvider.class);

    private final RateService service = new RateService(List.of(firstRateProvider, secondRateProvider));

    @BeforeEach
    public void setup() {
        when(firstRateProvider.rateFor(SYMBOL)).thenReturn(RATE_1);
        when(secondRateProvider.rateFor(SYMBOL)).thenReturn(RATE_2);
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
        verify(firstRateProvider).rateFor(SYMBOL);
        verify(secondRateProvider).rateFor(SYMBOL);
    }

}
