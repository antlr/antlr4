package org.antlr.v4.test.runtime.java.api.perf;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.test.runtime.java.api.JavaLexer;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/** Test how fast we can lex Java and some unicode graphemes using old and
 *  new unicode stream mechanism. It also tests load time for ASCII
 *  and unicode code points beyond 0xFFFF.
 *
 *  Sample output on OS X with 4 GHz Intel Core i7 (us == microseconds, 1/1000 of a millisecond):
 *
 Warming up Java compiler....
    load_legacy_java_ascii average time    53us over 3500 loads of 29038 symbols from Parser.java
     load_legacy_java_utf8 average time    42us over 3500 loads of 29038 symbols from Parser.java
     load_legacy_java_utf8 average time   121us over 3500 loads of 13379 symbols from udhr_hin.txt
             load_new_utf8 average time   193us over 3500 loads of 29038 symbols from Parser.java
             load_new_utf8 average time   199us over 3500 loads of 13379 symbols from udhr_hin.txt

     lex_legacy_java_ascii average time   399us over 2000 runs of 29038 symbols
     lex_legacy_java_ascii average time   918us over 2000 runs of 29038 symbols DFA cleared
      lex_legacy_java_utf8 average time   377us over 2000 runs of 29038 symbols
      lex_legacy_java_utf8 average time   925us over 2000 runs of 29038 symbols DFA cleared
         lex_new_java_utf8 average time   460us over 2000 runs of 29038 symbols
         lex_new_java_utf8 average time   989us over 2000 runs of 29038 symbols DFA cleared

  lex_legacy_grapheme_utf8 average time  6862us over  400 runs of  6614 symbols from udhr_kor.txt
  lex_legacy_grapheme_utf8 average time  7023us over  400 runs of  6614 symbols from udhr_kor.txt DFA cleared
  lex_legacy_grapheme_utf8 average time  6290us over  400 runs of 13379 symbols from udhr_hin.txt
  lex_legacy_grapheme_utf8 average time  6238us over  400 runs of 13379 symbols from udhr_hin.txt DFA cleared
     lex_new_grapheme_utf8 average time  6863us over  400 runs of  6614 symbols from udhr_kor.txt
     lex_new_grapheme_utf8 average time  7014us over  400 runs of  6614 symbols from udhr_kor.txt DFA cleared
     lex_new_grapheme_utf8 average time  6187us over  400 runs of 13379 symbols from udhr_hin.txt
     lex_new_grapheme_utf8 average time  6284us over  400 runs of 13379 symbols from udhr_hin.txt DFA cleared
     lex_new_grapheme_utf8 average time    99us over  400 runs of    85 symbols from emoji.txt
     lex_new_grapheme_utf8 average time   111us over  400 runs of    85 symbols from emoji.txt DFA cleared
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
		URL sampleJavaFile = TimeLexerSpeed.class.getClassLoader().getResource(Parser_java_file);
		URL sampleFile = TimeLexerSpeed.class.getClassLoader().getResource(PerfDir+"/udhr_hin.txt");
		tests.load_legacy_java_ascii(n);
		tests.load_legacy_java_utf8(sampleJavaFile.getFile(), n);
		tests.load_legacy_java_utf8(sampleFile.getFile(), n);
		tests.load_new_utf8(sampleJavaFile.getFile(), n);
		tests.load_new_utf8(sampleFile.getFile(), n);
		System.out.println();

		n = 2000;
		tests.lex_legacy_java_ascii(n, false);
		tests.lex_legacy_java_ascii(n, true);
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
		lex_legacy_java_ascii(n, false);
		System.out.print('.');
		lex_legacy_grapheme_utf8("udhr_hin.txt", n, false);
		System.out.print('.');
		lex_new_grapheme_utf8("udhr_hin.txt", n, false);
		System.out.println();
		output = true;
	}

	public void load_legacy_java_ascii(int n) throws Exception {
		URL sampleJavaFile = TimeLexerSpeed.class.getClassLoader().getResource(Parser_java_file);
		long start = System.nanoTime();
		CharStream input = null;
		for (int i = 0; i<n; i++) {
			input = new ANTLRFileStream(sampleJavaFile.getFile());
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
		                                basename(sampleJavaFile.getFile()));
	}

	public void load_legacy_java_utf8(String fileName, int n) throws Exception {
		long start = System.nanoTime();
		CharStream input = null;
		for (int i = 0; i<n; i++) {
			input = new ANTLRFileStream(fileName, "UTF-8");
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

	public void load_new_utf8(String fileName, int n) throws Exception {
		long start = System.nanoTime();
		CharStream input = null;
		for (int i = 0; i<n; i++) {
			input = CharStreams.fromFileName(fileName);
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

	public void lex_legacy_java_ascii(int n, boolean clearLexerDFACache) throws Exception {
		URL sampleJavaFile = TimeLexerSpeed.class.getClassLoader().getResource(Parser_java_file);
		CharStream input = new ANTLRFileStream(sampleJavaFile.getFile());
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

	public void lex_legacy_java_utf8(int n, boolean clearLexerDFACache) throws Exception {
		URL sampleJavaFile = TimeLexerSpeed.class.getClassLoader().getResource(Parser_java_file);
		CharStream input = new ANTLRFileStream(sampleJavaFile.getFile(), "UTF-8");
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

	public void lex_new_java_utf8(int n, boolean clearLexerDFACache) throws Exception {
		URL sampleJavaFile = TimeLexerSpeed.class.getClassLoader().getResource(Parser_java_file);
		CharStream input = CharStreams.fromPath(Paths.get(sampleJavaFile.getFile()));
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

	public void lex_legacy_grapheme_utf8(String fileName, int n, boolean clearLexerDFACache) throws Exception {
		URL sampleFile = TimeLexerSpeed.class.getClassLoader().getResource(PerfDir+"/"+fileName);
		CharStream input = new ANTLRFileStream(sampleFile.getFile(), "UTF-8");
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

	public void lex_new_grapheme_utf8(String fileName, int n, boolean clearLexerDFACache) throws Exception {
		URL sampleJavaFile = TimeLexerSpeed.class.getClassLoader().getResource(PerfDir+"/"+fileName);
		CharStream input = CharStreams.fromPath(Paths.get(sampleJavaFile.getFile()));
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
