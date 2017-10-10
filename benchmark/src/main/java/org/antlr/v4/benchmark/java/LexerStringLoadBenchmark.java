package org.antlr.v4.benchmark.java;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Thread)
public class LexerStringLoadBenchmark {

	@State(Scope.Thread)
	public static class StringState {
		@Param({
				"org/antlr/v4/benchmark/java/Parser.java",
				"org/antlr/v4/benchmark/java/udhr_hin.txt",
				"org/antlr/v4/benchmark/java/emoji.txt",
				})
		public String resourceName;
		private String input;

		@Setup(Level.Trial)
		public void setup() throws Exception {
			input = Resources.getResourceAsString(LexerStringLoadBenchmark.class.getClassLoader(), resourceName);
		}
	}

	@Benchmark
	public CharStream legacyStringLoad(StringState state) throws Exception {
		return new ANTLRInputStream(state.input);
	}

	@Benchmark
	public CharStream newStringLoad(StringState state) throws Exception {
		return CharStreams.fromString(state.input);
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(LexerStreamLoadBenchmark.class.getSimpleName())
				.build();

		new Runner(opt).run();
	}
}
