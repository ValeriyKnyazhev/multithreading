package valeriy.knyazhev.multithreading;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import valeriy.knyazhev.multithreading.behaviour.CompletableFutureRatesCollector;
import valeriy.knyazhev.multithreading.behaviour.ExecutorServiceRatesCollector;
import valeriy.knyazhev.multithreading.behaviour.ParallelStreamRatesCollector;
import valeriy.knyazhev.multithreading.behaviour.SequentialRatesCollector;
import valeriy.knyazhev.multithreading.behaviour.ThreadsWithCountDownLatchRatesCollector;
import valeriy.knyazhev.multithreading.behaviour.ThreadsWithJoinRatesCollector;
import valeriy.knyazhev.multithreading.model.Rate;
import valeriy.knyazhev.multithreading.service.DelayGenerator;
import valeriy.knyazhev.multithreading.service.RateProvider;
import valeriy.knyazhev.multithreading.service.TestRateProvider;
import valeriy.knyazhev.multithreading.service.behaviour.RandomDelayGenerator;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newWorkStealingPool;
import static valeriy.knyazhev.multithreading.model.Rate.rate;

/**
 * @author Valeriy Knyazhev
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1)
@Measurement(iterations = 6, time = 10)
@Warmup(iterations = 2, time = 10)
public class RateServiceBenchmark {

    private static final int THREADS_NUM = 4;
    private static final String SYMBOL = "GBPUSD";
    private static final Rate RATE = rate().symbol(SYMBOL)
        .ask(new BigDecimal("1.20015"))
        .bid(new BigDecimal("1.20005"))
        .build();
    private static final Duration MIN_DELAY = Duration.of(1, ChronoUnit.MILLIS);
    private static final Duration MAX_DELAY = Duration.of(5, ChronoUnit.MILLIS);
    static final DelayGenerator DELAY_GENERATOR = new RandomDelayGenerator(MIN_DELAY, MAX_DELAY);
    static final List<RateProvider> PROVIDERS = List.of(
        new TestRateProvider(RATE, DELAY_GENERATOR),
        new TestRateProvider(RATE, DELAY_GENERATOR),
        new TestRateProvider(RATE, DELAY_GENERATOR),
        new TestRateProvider(RATE, DELAY_GENERATOR)
    );
    private static final Duration WAIT_TIME = Duration.ofMillis(100);

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
            .include(RateServiceBenchmark.class.getSimpleName())
            .forks(1)
            .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void fetch_best_rate_in_sequential_mode(SequentialExecutionState execution,
                                                   Blackhole blackhole) {
        blackhole.consume(execution.service.bestRateFor(SYMBOL));
    }

    @Benchmark
    public void fetch_best_rate_in_threads_with_join_mode(ThreadsWithJoinExecutionState execution,
                                                          Blackhole blackhole) {
        blackhole.consume(execution.service.bestRateFor(SYMBOL));
    }

    @Benchmark
    public void fetch_best_rate_in_threads_with_count_down_latch_mode(ThreadsWithCountDownLatchExecutionState execution,
                                                                      Blackhole blackhole) {
        blackhole.consume(execution.service.bestRateFor(SYMBOL));
    }

    @Benchmark
    public void fetch_best_rate_in_fixed_thread_pool_executor_service_mode(FixedThreadPoolExecutorServiceExecutionState execution,
                                                                           Blackhole blackhole) {
        blackhole.consume(execution.service.bestRateFor(SYMBOL));
    }

    @Benchmark
    public void fetch_best_rate_in_fork_join_pool_parallel_stream_mode(ForkJoinPoolParallelStreamExecutionState execution,
                                                                       Blackhole blackhole) {
        blackhole.consume(execution.service.bestRateFor(SYMBOL));
    }

    @Benchmark
    public void fetch_best_rate_in_fixed_thread_pool_completable_future_mode(FixedThreadPoolCompletableFutureExecutionState execution,
                                                                              Blackhole blackhole) {
        blackhole.consume(execution.service.bestRateFor(SYMBOL));
    }

    @State(Scope.Benchmark)
    public static class SequentialExecutionState {

        RateService service;

        @Setup(Level.Iteration)
        public void setUp() {
            service = new RateService(
                PROVIDERS, new SequentialRatesCollector()
            );
        }

    }

    @State(Scope.Benchmark)
    public static class ThreadsWithJoinExecutionState {

        RateService service;

        @Setup
        public void setUp() {
            service = new RateService(
                PROVIDERS, new ThreadsWithJoinRatesCollector(WAIT_TIME)
            );
        }

    }

    @State(Scope.Benchmark)
    public static class ThreadsWithCountDownLatchExecutionState {

        RateService service;

        @Setup
        public void setUp() {
            service = new RateService(
                PROVIDERS, new ThreadsWithCountDownLatchRatesCollector(WAIT_TIME)
            );
        }

    }

    @State(Scope.Benchmark)
    public static class FixedThreadPoolExecutorServiceExecutionState {

        ExecutorService executor;
        RateService service;

        @Setup
        public void setUp() {
            executor = newFixedThreadPool(THREADS_NUM);
            service = new RateService(
                PROVIDERS, new ExecutorServiceRatesCollector(executor, WAIT_TIME)
            );
        }

        @TearDown
        public void tearDown() {
            executor.shutdown();
        }

    }

    @State(Scope.Benchmark)
    public static class ForkJoinPoolParallelStreamExecutionState {

        ExecutorService executor;
        RateService service;

        @Setup
        public void setUp() {
            executor = newWorkStealingPool(4);
            service = new RateService(
                PROVIDERS, new ParallelStreamRatesCollector(executor, WAIT_TIME)
            );
        }

        @TearDown
        public void tearDown() {
            executor.shutdown();
        }

    }

    @State(Scope.Benchmark)
    public static class FixedThreadPoolCompletableFutureExecutionState {

        ExecutorService executor;
        RateService service;

        @Setup
        public void setUp() {
            executor = newFixedThreadPool(4);
            service = new RateService(
                PROVIDERS, new CompletableFutureRatesCollector(executor, WAIT_TIME)
            );
        }

        @TearDown
        public void tearDown() {
            executor.shutdown();
        }

    }

}
