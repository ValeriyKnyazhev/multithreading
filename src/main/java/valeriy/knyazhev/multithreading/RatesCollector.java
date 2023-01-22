package valeriy.knyazhev.multithreading;

import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;

import java.util.Collection;
import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
public interface RatesCollector {

     Collection<Rate> collectRates(String symbol, List<RateProvider> providers);

}
