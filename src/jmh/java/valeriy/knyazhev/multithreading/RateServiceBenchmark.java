package valeriy.knyazhev.multithreading;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.RateProvider;
import valeriy.knyazhev.multithreading.service.TestRateProvider;
import valeriy.knyazhev.multithreading.service.behaviour.RandomDelayGenerator;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static valeriy.knyazhev.multithreading.model.Rate.rate;

/**
 * @author Valeriy Knyazhev
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1)
@Measurement(iterations = 10, time = 10)
@Warmup(iterations = 3, time = 10)
public class RateServiceBenchmark {

    private static final String SYMBOL = "GBPUSD";
    private static final Rate RATE = rate().symbol(SYMBOL)
        .ask(new BigDecimal("1.20015"))
        .bid(new BigDecimal("1.20005"))
        .build();
    private static final Duration MIN_DELAY = Duration.of(1, ChronoUnit.MILLIS);
    private static final Duration MAX_DELAY = Duration.of(5, ChronoUnit.MILLIS);

    private RateService rateService;

    @Setup
    public void setup() {
        final var delayGenerator = new RandomDelayGenerator(MIN_DELAY, MAX_DELAY);
        final List<RateProvider> providers = List.of(
            new TestRateProvider(RATE, delayGenerator),
            new TestRateProvider(RATE, delayGenerator),
            new TestRateProvider(RATE, delayGenerator),
            new TestRateProvider(RATE, delayGenerator)
        );
        rateService = new RateService(providers);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
            .include(RateServiceBenchmark.class.getSimpleName())
            .forks(1)
            .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void fetch_best_rates() {
        rateService.bestRateFor(SYMBOL);
    }

}
