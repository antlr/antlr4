package org.antlr.v4.test.runtime.java.api.perf;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.test.runtime.java.api.JavaLexer;

import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/** Test how fast we can lex Java and some unicode graphemes using old and
 *  new unicode stream mechanism. It also tests load time for unicode code points beyond 0xFFFF.
 *
 *  Sample output on Linux with Intel Xeon E5-2600 @ 2.20 GHz (us == microseconds, 1/1000 of a millisecond):
 *
Warming up Java compiler....
    load_legacy_java_utf8 average time   248us over 3500 loads of 29038 symbols from Parser.java
    load_legacy_java_utf8 average time   301us over 3500 loads of 13379 symbols from udhr_hin.txt
            load_new_utf8 average time   535us over 3500 loads of 29038 symbols from Parser.java
            load_new_utf8 average time   420us over 3500 loads of 13379 symbols from udhr_hin.txt

     lex_legacy_java_utf8 average time   625us over 2000 runs of 29038 symbols
     lex_legacy_java_utf8 average time  1678us over 2000 runs of 29038 symbols DFA cleared
        lex_new_java_utf8 average time   644us over 2000 runs of 29038 symbols
        lex_new_java_utf8 average time  1534us over 2000 runs of 29038 symbols DFA cleared

 lex_legacy_grapheme_utf8 average time 12950us over  400 runs of  6614 symbols from udhr_kor.txt
 lex_legacy_grapheme_utf8 average time 11953us over  400 runs of  6614 symbols from udhr_kor.txt DFA cleared
 lex_legacy_grapheme_utf8 average time 10144us over  400 runs of 13379 symbols from udhr_hin.txt
 lex_legacy_grapheme_utf8 average time 11146us over  400 runs of 13379 symbols from udhr_hin.txt DFA cleared
    lex_new_grapheme_utf8 average time 11914us over  400 runs of  6614 symbols from udhr_kor.txt
    lex_new_grapheme_utf8 average time 12030us over  400 runs of  6614 symbols from udhr_kor.txt DFA cleared
    lex_new_grapheme_utf8 average time 10400us over  400 runs of 13379 symbols from udhr_hin.txt
    lex_new_grapheme_utf8 average time 10365us over  400 runs of 13379 symbols from udhr_hin.txt DFA cleared
    lex_new_grapheme_utf8 average time   183us over  400 runs of    85 symbols from emoji.txt
    lex_new_grapheme_utf8 average time   192us over  400 runs of    85 symbols from emoji.txt DFA cleared
 *
 *  The "DFA cleared" indicates that the lexer was returned to initial conditions
 *  before the tokenizing of each file.  As the ALL(*) lexer encounters new input,
 *  it records how it tokenized the chars. The next time it sees that input,
 *  it will more quickly recognize the token.
 *
 *  Lexing times have the top 20% stripped off before doing the average
 *  to account for issues with the garbage collection and compilation pauses;
 *  other OS tasks could also pop in randomly.
 *
 *  Load times are too fast to measure with a microsecond clock using an SSD
 *  so the average load time is computed as the overall time to load
 *  n times divided by n (rather then summing up the individual times).
 *
 *  @since 4.7
 */
public class TimeLexerSpeed { // don't call it Test else it'll run during "mvn test"
	public static final String Parser_java_file = "Java/src/org/antlr/v4/runtime/Parser.java";
	public static final String PerfDir = "org/antlr/v4/test/runtime/java/api/perf";

	public boolean output = true;

	public static void main(String[] args) throws Exception {
		TimeLexerSpeed tests = new TimeLexerSpeed();

		tests.compilerWarmUp(100);

		int n = 3500;
		tests.load_legacy_java_utf8(Parser_java_file, n);
		tests.load_legacy_java_utf8(PerfDir+"/udhr_hin.txt", n);
		tests.load_new_utf8(Parser_java_file, n);
		tests.load_new_utf8(PerfDir+"/udhr_hin.txt", n);
		System.out.println();

		n = 2000;
		tests.lex_legacy_java_utf8(n, false);
		tests.lex_legacy_java_utf8(n, true);
		tests.lex_new_java_utf8(n, false);
		tests.lex_new_java_utf8(n, true);
		System.out.println();

		n = 400;
		tests.lex_legacy_grapheme_utf8("udhr_kor.txt", n, false);
		tests.lex_legacy_grapheme_utf8("udhr_kor.txt", n, true);
		tests.lex_legacy_grapheme_utf8("udhr_hin.txt", n, false);
		tests.lex_legacy_grapheme_utf8("udhr_hin.txt", n, true);
		// legacy can't handle the emoji (32 bit stuff)

		tests.lex_new_grapheme_utf8("udhr_kor.txt", n, false);
		tests.lex_new_grapheme_utf8("udhr_kor.txt", n, true);
		tests.lex_new_grapheme_utf8("udhr_hin.txt", n, false);
		tests.lex_new_grapheme_utf8("udhr_hin.txt", n, true);
		tests.lex_new_grapheme_utf8("emoji.txt", n, false);
		tests.lex_new_grapheme_utf8("emoji.txt", n, true);
	}

	public void compilerWarmUp(int n) throws Exception {
		System.out.print("Warming up Java compiler");
		output = false;
		lex_new_java_utf8(n, false);
		System.out.print('.');
		lex_legacy_java_utf8(n, false);
		System.out.print('.');
		System.out.print('.');
		lex_legacy_grapheme_utf8("udhr_hin.txt", n, false);
		System.out.print('.');
		lex_new_grapheme_utf8("udhr_hin.txt", n, false);
		System.out.println();
		output = true;
	}

	public void load_legacy_java_utf8(String resourceName, int n) throws Exception {
		long start = System.nanoTime();
		CharStream input = null;
		for (int i = 0; i<n; i++) {
			try (InputStream is = TimeLexerSpeed.class.getClassLoader().getResourceAsStream(resourceName);
			     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
			     BufferedReader br = new BufferedReader(isr)) {
				input = new ANTLRInputStream(br);
			}
		}
		long stop = System.nanoTime();
		long tus = (stop-start)/1000;
		int size = input.size();
		String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
		if ( output ) System.out.printf("%25s average time %5dus over %4d loads of %5d symbols from %s\n",
						currentMethodName,
						tus/n,
						n,
						size,
						basename(resourceName));
	}

	public void load_new_utf8(String fileName, int n) throws Exception {
		long start = System.nanoTime();
		CharStream input = null;
		for (int i = 0; i<n; i++) {
			try (InputStream is = TimeLexerSpeed.class.getClassLoader().getResourceAsStream(fileName)) {
				input = CharStreams.fromStream(is);
			}
		}
		long stop = System.nanoTime();
		long tus = (stop-start)/1000;
		int size = input.size();
		String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
		if ( output ) System.out.printf("%25s average time %5dus over %4d loads of %5d symbols from %s\n",
						currentMethodName,
						tus/n,
						n,
						size,
						basename(fileName));
	}

	public void lex_legacy_java_utf8(int n, boolean clearLexerDFACache) throws Exception {
		try (InputStream is = TimeLexerSpeed.class.getClassLoader().getResourceAsStream(Parser_java_file);
		     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
		     BufferedReader br = new BufferedReader(isr)) {
			CharStream input = new ANTLRInputStream(br);
			JavaLexer lexer = new JavaLexer(input);
			double avg = tokenize(lexer, n, clearLexerDFACache);
			String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
			if ( output ) System.out.printf("%25s average time %5dus over %4d runs of %5d symbols%s\n",
							currentMethodName,
							(int)avg,
							n,
							input.size(),
							clearLexerDFACache ? " DFA cleared" : "");
		}
	}

	public void lex_new_java_utf8(int n, boolean clearLexerDFACache) throws Exception {
		try (InputStream is = TimeLexerSpeed.class.getClassLoader().getResourceAsStream(Parser_java_file);) {
			CharStream input = CharStreams.fromStream(is);
			JavaLexer lexer = new JavaLexer(input);
			double avg = tokenize(lexer, n, clearLexerDFACache);
			String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
			if ( output ) System.out.printf("%25s average time %5dus over %4d runs of %5d symbols%s\n",
							currentMethodName,
							(int)avg,
							n,
							input.size(),
							clearLexerDFACache ? " DFA cleared" : "");
		}
	}

	public void lex_legacy_grapheme_utf8(String fileName, int n, boolean clearLexerDFACache) throws Exception {
		try (InputStream is = TimeLexerSpeed.class.getClassLoader().getResourceAsStream(PerfDir+"/"+fileName);
		     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
		     BufferedReader br = new BufferedReader(isr)) {
			CharStream input = new ANTLRInputStream(br);
			graphemesLexer lexer = new graphemesLexer(input);
			double avg = tokenize(lexer, n, clearLexerDFACache);
			String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
			if ( output ) System.out.printf("%25s average time %5dus over %4d runs of %5d symbols from %s%s\n",
							currentMethodName,
							(int)avg,
							n,
							input.size(),
							fileName,
							clearLexerDFACache ? " DFA cleared" : "");
		}
	}

	public void lex_new_grapheme_utf8(String fileName, int n, boolean clearLexerDFACache) throws Exception {
		try (InputStream is = TimeLexerSpeed.class.getClassLoader().getResourceAsStream(PerfDir+"/"+fileName)) {
			CharStream input = CharStreams.fromStream(is);
			graphemesLexer lexer = new graphemesLexer(input);
			double avg = tokenize(lexer, n, clearLexerDFACache);
			String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
			if ( output ) System.out.printf("%25s average time %5dus over %4d runs of %5d symbols from %s%s\n",
							currentMethodName,
							(int)avg,
							n,
							input.size(),
							fileName,
							clearLexerDFACache ? " DFA cleared" : "");
		}
	}

	public double tokenize(Lexer lexer, int n, boolean clearLexerDFACache) {
		// always wipe the DFA before we begin tests so previous tests
		// don't affect this run!
		lexer.getInterpreter().clearDFA();
		long[] times = new long[n];
		for (int i = 0; i<n; i++) {
			lexer.reset();
			if ( clearLexerDFACache ) {
				lexer.getInterpreter().clearDFA();
			}
			long start = System.nanoTime();
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill(); // lex whole file.
//			int size = lexer.getInputStream().size();
			long stop = System.nanoTime();
			times[i] = (stop-start)/1000;
//			if ( output ) System.out.printf("Tokenized %d char in %dus\n", size, times[i]);
		}
		Arrays.sort(times);
		times = Arrays.copyOfRange(times, 0, times.length-(int)(n*.2)); // drop highest 20% of times
		return avg(times);
	}

	public double avg(long[] values) {
		double sum = 0.0;
		for (Long v : values) {
			sum += v;
		}
		return sum / values.length;
	}

	public double std(double mean, List<Long> values) { // unbiased std dev
		double sum = 0.0;
		for (Long v : values) {
			sum += (v-mean)*(v-mean);
		}
		return Math.sqrt(sum / (values.size() - 1));
	}

	public static String basename(String fullyQualifiedFileName) {
		Path path = Paths.get(fullyQualifiedFileName);
		return basename(path);
	}

	public static String dirname(String fullyQualifiedFileName) {
		Path path = Paths.get(fullyQualifiedFileName);
		return dirname(path);
	}

	public static String basename(Path path) {
		return path.getName(path.getNameCount()-1).toString();
	}

	public static String dirname(Path path) {
		return path.getName(0).toString();
	}
}
