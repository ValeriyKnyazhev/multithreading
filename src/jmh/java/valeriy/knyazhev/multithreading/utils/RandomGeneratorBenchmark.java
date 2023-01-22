package valeriy.knyazhev.multithreading.utils;

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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author Valeriy Knyazhev
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
@Measurement(iterations = 5, time = 5)
@Warmup(iterations = 1, time = 5)
public class RandomGeneratorBenchmark {

    private RandomGenerator randomGenerator;

    @Setup
    public void setup() {
        this.randomGenerator = new RandomGenerator();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
            .include(RandomGeneratorBenchmark.class.getSimpleName())
            .forks(1)
            .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void generate_custom_random(Blackhole blackhole) {
        blackhole.consume(randomGenerator.random());
    }

    @Benchmark
    public void generate_random_from_library(Blackhole blackhole) {
        blackhole.consume(Math.random());
    }

}
