package valeriy.knyazhev.multithreading.behaviour;

import valeriy.knyazhev.multithreading.RatesCollector;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Valeriy Knyazhev
 */
public class SequentialRatesCollector implements RatesCollector {

     public Collection<Rate> collectRates(String symbol, List<RateProvider> providers) {
          return providers.stream()
              .map(provider -> provider.rateFor(symbol))
              .collect(Collectors.toList());
     }

}
