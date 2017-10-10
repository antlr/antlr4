package org.antlr.v4.benchmark.java;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CodePointCharStream;
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
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class LexerBenchmark {

	@State(Scope.Thread)
	public static class LegacyGraphemesState {
		@Param({
				"org/antlr/v4/benchmark/java/udhr_hin.txt",
				"org/antlr/v4/benchmark/java/udhr_kor.txt",
				// no emoji, legacy stream can't handle it
				})
		public String resourceName;

		@Param({"true", "false"})
		public String clearLexerDFACache;

		private GraphemesLexer lexer;

		@Setup(Level.Trial)
		public void setup() throws Exception {
			ClassLoader loader = LexerStreamLoadBenchmark.class.getClassLoader();
			try (InputStream is = loader.getResourceAsStream(resourceName);
			     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
				lexer = new GraphemesLexer(new ANTLRInputStream(isr));
			}
		}

		@Setup(Level.Invocation)
		public void setupInvocation() throws Exception {
			lexer.reset();
			if (Boolean.valueOf(clearLexerDFACache)) {
				lexer.getInterpreter().clearDFA();
			}
		}
	}

	@State(Scope.Thread)
	public static class NewGraphemesState {
		@Param({
				"org/antlr/v4/benchmark/java/udhr_hin.txt",
				"org/antlr/v4/benchmark/java/udhr_kor.txt",
				"org/antlr/v4/benchmark/java/emoji.txt",
				})
		public String resourceName;

		@Param({"true", "false"})
		public String clearLexerDFACache;

		private GraphemesLexer lexer;

		@Setup(Level.Trial)
		public void setup() throws Exception {
			ClassLoader loader = LexerStreamLoadBenchmark.class.getClassLoader();
			try (InputStream is = loader.getResourceAsStream(resourceName)) {
				lexer = new GraphemesLexer(CharStreams.fromStream(is));
			}
		}

		@Setup(Level.Invocation)
		public void setupInvocation() throws Exception {
			lexer.reset();
			if (Boolean.valueOf(clearLexerDFACache)) {
				lexer.getInterpreter().clearDFA();
			}
		}
	}

	@State(Scope.Thread)
	public static class LegacyJavaState {
		@Param({
				"org/antlr/v4/benchmark/java/Parser.java",
			})
		public String resourceName;

		@Param({"true", "false"})
		public String clearLexerDFACache;

		private JavaLexer lexer;

		@Setup(Level.Trial)
		public void setup() throws Exception {
			ClassLoader loader = LexerStreamLoadBenchmark.class.getClassLoader();
			try (InputStream is = loader.getResourceAsStream(resourceName);
			     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
				lexer = new JavaLexer(new ANTLRInputStream(isr));
			}
		}

		@Setup(Level.Invocation)
		public void setupInvocation() throws Exception {
			lexer.reset();
			if (Boolean.valueOf(clearLexerDFACache)) {
				lexer.getInterpreter().clearDFA();
			}
		}
	}

	@State(Scope.Thread)
	public static class NewJavaState {
		@Param({
				"org/antlr/v4/benchmark/java/Parser.java",
			})
		public String resourceName;

		@Param({"true", "false"})
		public String clearLexerDFACache;

		private JavaLexer lexer;

		@Setup(Level.Trial)
		public void setup() throws Exception {
			ClassLoader loader = LexerStreamLoadBenchmark.class.getClassLoader();
			try (InputStream is = loader.getResourceAsStream(resourceName)) {
				lexer = new JavaLexer(CharStreams.fromStream(is));
			}
		}

		@Setup(Level.Invocation)
		public void setupInvocation() throws Exception {
			lexer.reset();
			if (Boolean.valueOf(clearLexerDFACache)) {
				lexer.getInterpreter().clearDFA();
			}
		}
	}

	@Benchmark
	public CommonTokenStream legacyGraphemes(LegacyGraphemesState state) throws Exception {
		return tokenize(state.lexer);
	}

	@Benchmark
	public CommonTokenStream newGraphemes(NewGraphemesState state) throws Exception {
		return tokenize(state.lexer);
	}

	@Benchmark
	public CommonTokenStream legacyJava(LegacyJavaState state) throws Exception {
		return tokenize(state.lexer);
	}

	@Benchmark
	public CommonTokenStream newJava(NewJavaState state) throws Exception {
		return tokenize(state.lexer);
	}

	private static CommonTokenStream tokenize(Lexer lexer) {
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill(); // lex whole file.
		return tokens;
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(LexerStreamLoadBenchmark.class.getSimpleName())
				.build();

		new Runner(opt).run();
	}
}
