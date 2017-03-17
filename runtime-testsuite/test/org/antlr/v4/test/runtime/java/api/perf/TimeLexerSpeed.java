package org.antlr.v4.test.runtime.java.api.perf;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.test.runtime.java.api.JavaLexer;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/** Test how fast we can lex Java and some unicode graphemes using old and
 *  new unicode stream mechanism.
 *
 *  Sample output on OS X with 4 GHz Intel Core i7 (us == microseconds, 1/1000 of a millisecond)
 *
	 legacy_java_ascii average time   336us over 1500 runs of 29038 symbols
	 legacy_java_ascii average time   828us over 1500 runs of 29038 symbols DFA cleared
	  legacy_java_utf8 average time   281us over 1500 runs of 29038 symbols
	  legacy_java_utf8 average time   815us over 1500 runs of 29038 symbols DFA cleared
	     new_java_utf8 average time   538us over 1500 runs of 29038 symbols
	     new_java_utf8 average time  1074us over 1500 runs of 29038 symbols DFA cleared
  legacy_grapheme_utf8 average time  6812us over  500 runs of  6614 symbols from udhr_kor.txt
  legacy_grapheme_utf8 average time  7046us over  500 runs of  6614 symbols from udhr_kor.txt DFA cleared
  legacy_grapheme_utf8 average time  6190us over  500 runs of 13379 symbols from udhr_hin.txt
  legacy_grapheme_utf8 average time  6379us over  500 runs of 13379 symbols from udhr_hin.txt DFA cleared
	 new_grapheme_utf8 average time  6898us over  500 runs of  6614 symbols from udhr_kor.txt
	 new_grapheme_utf8 average time  7033us over  500 runs of  6614 symbols from udhr_kor.txt DFA cleared
	 new_grapheme_utf8 average time  6606us over  500 runs of 13379 symbols from udhr_hin.txt
	 new_grapheme_utf8 average time  6651us over  500 runs of 13379 symbols from udhr_hin.txt DFA cleared
	 new_grapheme_utf8 average time   101us over  500 runs of    85 symbols from emoji.txt
	 new_grapheme_utf8 average time   113us over  500 runs of    85 symbols from emoji.txt DFA cleared
 *
 *  The "DFA cleared" indicates that the lexer was returned to initial conditions
 *  before the tokenizing of each file.  As the ALL(*) lexer encounters new input,
 *  it records how it tokenized the chars. The next time it sees that input,
 *  it will more quickly recognize the token.
 *
 *  @since 4.7
 */
public class TimeLexerSpeed { // don't call it Test else it'll run during "mvn test"
	public static final String Parser_java_file = "Java/src/org/antlr/v4/runtime/Parser.java";
	public static final String PerfDir = "org/antlr/v4/test/runtime/java/api/perf";

	public static void main(String[] args) throws Exception {
		TimeLexerSpeed tests = new TimeLexerSpeed();
		int n = 1500;
		tests.legacy_java_ascii(n, false);
		tests.legacy_java_ascii(n, true);
		tests.legacy_java_utf8(n, false);
		tests.legacy_java_utf8(n, true);
		tests.new_java_utf8(n, false);
		tests.new_java_utf8(n, true);

		n = 500;
		tests.legacy_grapheme_utf8("udhr_kor.txt", n, false);
		tests.legacy_grapheme_utf8("udhr_kor.txt", n, true);
		tests.legacy_grapheme_utf8("udhr_hin.txt", n, false);
		tests.legacy_grapheme_utf8("udhr_hin.txt", n, true);
		// legacy can't handle the emoji (32 bit stuff)

		tests.new_grapheme_utf8("udhr_kor.txt", n, false);
		tests.new_grapheme_utf8("udhr_kor.txt", n, true);
		tests.new_grapheme_utf8("udhr_hin.txt", n, false);
		tests.new_grapheme_utf8("udhr_hin.txt", n, true);
		tests.new_grapheme_utf8("emoji.txt", n, false);
		tests.new_grapheme_utf8("emoji.txt", n, true);
	}

	public void legacy_java_ascii(int n, boolean clearLexerDFACache) throws Exception {
		URL sampleJavaFile = TimeLexerSpeed.class.getClassLoader().getResource(Parser_java_file);
		CharStream input = new ANTLRFileStream(sampleJavaFile.getFile());
		JavaLexer lexer = new JavaLexer(input);
		double avg = tokenize(lexer, n, clearLexerDFACache);
		String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
		System.out.printf("%25s average time %5dus over %4d runs of %5d symbols%s\n",
		                  currentMethodName,
		                  (int)avg,
		                  n,
		                  input.size(),
		                  clearLexerDFACache ? " DFA cleared" : "");
	}

	public void legacy_java_utf8(int n, boolean clearLexerDFACache) throws Exception {
		URL sampleJavaFile = TimeLexerSpeed.class.getClassLoader().getResource(Parser_java_file);
		CharStream input = new ANTLRFileStream(sampleJavaFile.getFile(), "UTF-8");
		JavaLexer lexer = new JavaLexer(input);
		double avg = tokenize(lexer, n, clearLexerDFACache);
		String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
		System.out.printf("%25s average time %5dus over %4d runs of %5d symbols%s\n",
		                  currentMethodName,
		                  (int)avg,
		                  n,
		                  input.size(),
		                  clearLexerDFACache ? " DFA cleared" : "");
	}

	public void new_java_utf8(int n, boolean clearLexerDFACache) throws Exception {
		URL sampleJavaFile = TimeLexerSpeed.class.getClassLoader().getResource(Parser_java_file);
		CharStream input = CharStreams.fromPath(Paths.get(sampleJavaFile.getFile()));
		JavaLexer lexer = new JavaLexer(input);
		double avg = tokenize(lexer, n, clearLexerDFACache);
		String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
		System.out.printf("%25s average time %5dus over %4d runs of %5d symbols%s\n",
		                  currentMethodName,
		                  (int)avg,
		                  n,
		                  input.size(),
		                  clearLexerDFACache ? " DFA cleared" : "");
	}

	public void legacy_grapheme_utf8(String fileName, int n, boolean clearLexerDFACache) throws Exception {
		URL sampleJavaFile = TimeLexerSpeed.class.getClassLoader().getResource(PerfDir+"/"+fileName);
		CharStream input = new ANTLRFileStream(sampleJavaFile.getFile(), "UTF-8");
		graphemesLexer lexer = new graphemesLexer(input);
		double avg = tokenize(lexer, n, clearLexerDFACache);
		String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
		System.out.printf("%25s average time %5dus over %4d runs of %5d symbols from %s%s\n",
		                  currentMethodName,
		                  (int)avg,
		                  n,
		                  input.size(),
		                  fileName,
		                  clearLexerDFACache ? " DFA cleared" : "");
	}

	public void new_grapheme_utf8(String fileName, int n, boolean clearLexerDFACache) throws Exception {
		URL sampleJavaFile = TimeLexerSpeed.class.getClassLoader().getResource(PerfDir+"/"+fileName);
		CharStream input = CharStreams.fromPath(Paths.get(sampleJavaFile.getFile()));
		graphemesLexer lexer = new graphemesLexer(input);
		double avg = tokenize(lexer, n, clearLexerDFACache);
		String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
		System.out.printf("%25s average time %5dus over %4d runs of %5d symbols from %s%s\n",
		                  currentMethodName,
		                  (int)avg,
		                  n,
		                  input.size(),
		                  fileName,
		                  clearLexerDFACache ? " DFA cleared" : "");
	}

	public double tokenize(Lexer lexer, int n, boolean clearLexerDFACache) {
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
//			System.out.printf("Tokenized %d char in %dus\n", size, times[i]);
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
}
