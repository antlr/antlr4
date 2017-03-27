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

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class LexerStreamLoadBenchmark {

	@State(Scope.Thread)
	public static class LegacyStreamState {
		@Param({
				"org/antlr/v4/benchmark/java/Parser.java",
				"org/antlr/v4/benchmark/java/udhr_hin.txt",
				// no emoji, legacy stream can't handle it
				})
		public String resourceName;
		private InputStream is;

		@Setup(Level.Invocation)
		public void setup() throws Exception {
			ClassLoader loader = LexerStreamLoadBenchmark.class.getClassLoader();
			is = loader.getResourceAsStream(resourceName);
		}

		@TearDown(Level.Invocation)
		public void teardown() throws Exception {
			is.close();
			is = null;
		}
	}

	@State(Scope.Thread)
	public static class NewStreamState {
		@Param({
				"org/antlr/v4/benchmark/java/Parser.java",
				"org/antlr/v4/benchmark/java/udhr_hin.txt",
				"org/antlr/v4/benchmark/java/emoji.txt",
				})
		public String resourceName;
		private InputStream is;

		@Setup(Level.Invocation)
		public void setup() throws Exception {
			ClassLoader loader = LexerStreamLoadBenchmark.class.getClassLoader();
			is = loader.getResourceAsStream(resourceName);
		}

		@TearDown(Level.Invocation)
		public void teardown() throws Exception {
			is.close();
			is = null;
		}
	}

	@Benchmark
	public CharStream legacyStreamLoad(LegacyStreamState state) throws Exception {
		return new ANTLRInputStream(state.is);
	}

	@Benchmark
	public CharStream newStreamLoad(NewStreamState state) throws Exception {
		return CharStreams.fromStream(state.is);
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(LexerStreamLoadBenchmark.class.getSimpleName())
				.build();

		new Runner(opt).run();
	}
}
