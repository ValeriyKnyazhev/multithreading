package valeriy.knyazhev.multithreading.service;

import valeriy.knyazhev.multithreading.model.Rate;

/**
 * @author Valeriy Knyazhev
 */
public interface RateProvider {

    Rate rateFor(String symbol);
}
