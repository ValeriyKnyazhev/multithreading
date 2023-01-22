package valeriy.knyazhev.multithreading.utils;

import java.util.Random;

/**
 * @author Valeriy Knyazhev
 */
public class RandomGenerator {

    private static final int DEFAULT_PRECISION = 100000;

    private final Random random;
    private final int precision;

    public RandomGenerator() {
        this(DEFAULT_PRECISION);
    }

    public RandomGenerator(int precision) {
        this.random = new Random();
        this.precision = precision;
    }

    public double random() {
        return 1.0 * random.nextInt(precision) / precision;
    }

}
