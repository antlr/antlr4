/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.java.api.perf;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.test.runtime.java.api.JavaLexer;
import org.openjdk.jol.info.GraphLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Test how fast we can lex Java and some unicode graphemes using old and
 *  new unicode stream mechanism. It also tests load time for unicode code points beyond 0xFFFF.
 *
 *  Sample output on Linux with Intel Xeon E5-2600 @ 2.20 GHz (us == microseconds, 1/1000 of a millisecond):
 *
Java VM args:
Warming up Java compiler....
    load_legacy_java_utf8 average time   273us size 132266b over 3500 loads of 29038 symbols from Parser.java
    load_legacy_java_utf8 average time   299us size 128386b over 3500 loads of 13379 symbols from udhr_hin.txt
            load_new_utf8 average time   535us size 284788b over 3500 loads of 29038 symbols from Parser.java
            load_new_utf8 average time   439us size 153150b over 3500 loads of 13379 symbols from udhr_hin.txt

     lex_legacy_java_utf8 average time   624us over 2000 runs of 29038 symbols
     lex_legacy_java_utf8 average time  1530us over 2000 runs of 29038 symbols DFA cleared
        lex_new_java_utf8 average time   672us over 2000 runs of 29038 symbols
        lex_new_java_utf8 average time  1671us over 2000 runs of 29038 symbols DFA cleared

 lex_legacy_grapheme_utf8 average time 11942us over  400 runs of  6614 symbols from udhr_kor.txt
 lex_legacy_grapheme_utf8 average time 12075us over  400 runs of  6614 symbols from udhr_kor.txt DFA cleared
 lex_legacy_grapheme_utf8 average time 10040us over  400 runs of 13379 symbols from udhr_hin.txt
 lex_legacy_grapheme_utf8 average time 10221us over  400 runs of 13379 symbols from udhr_hin.txt DFA cleared
 *
 *  Sample output on OS X with 4 GHz Intel Core i7 (us == microseconds, 1/1000 of a millisecond):
 *
 Java VM args: -Xms2G -Xmx2G
 Warming up Java compiler....
 load_legacy_java_ascii_file average time    53us size  58384b over 3500 loads of 29038 symbols from Parser.java
 load_legacy_java_ascii_file average time    27us size  15568b over 3500 loads of  7625 symbols from RuleContext.java
      load_legacy_java_ascii average time    53us size  65584b over 3500 loads of 29038 symbols from Parser.java
      load_legacy_java_ascii average time    13us size  32816b over 3500 loads of  7625 symbols from RuleContext.java
       load_legacy_java_utf8 average time    54us size  65584b over 3500 loads of 29038 symbols from Parser.java
       load_legacy_java_utf8 average time   118us size  32816b over 3500 loads of 13379 symbols from udhr_hin.txt
               load_new_utf8 average time   232us size 131232b over 3500 loads of 29038 symbols from Parser.java
               load_new_utf8 average time    69us size  32928b over 3500 loads of  7625 symbols from RuleContext.java
               load_new_utf8 average time   210us size  65696b over 3500 loads of 13379 symbols from udhr_hin.txt

        lex_legacy_java_utf8 average time   342us over 2000 runs of 29038 symbols
        lex_legacy_java_utf8 average time   890us over 2000 runs of 29038 symbols DFA cleared
           lex_new_java_utf8 average time   439us over 2000 runs of 29038 symbols
           lex_new_java_utf8 average time   969us over 2000 runs of 29038 symbols DFA cleared

    lex_legacy_grapheme_utf8 average time  3971us over  400 runs of  6614 symbols from udhr_kor.txt
    lex_legacy_grapheme_utf8 average time  4084us over  400 runs of  6614 symbols from udhr_kor.txt DFA cleared
    lex_legacy_grapheme_utf8 average time  7542us over  400 runs of 13379 symbols from udhr_hin.txt
    lex_legacy_grapheme_utf8 average time  7666us over  400 runs of 13379 symbols from udhr_hin.txt DFA cleared
       lex_new_grapheme_utf8 average time  4034us over  400 runs of  6614 symbols from udhr_kor.txt
       lex_new_grapheme_utf8 average time  4173us over  400 runs of  6614 symbols from udhr_kor.txt DFA cleared
       lex_new_grapheme_utf8 average time  7680us over  400 runs of 13379 symbols from udhr_hin.txt
       lex_new_grapheme_utf8 average time  7946us over  400 runs of 13379 symbols from udhr_hin.txt DFA cleared
       lex_new_grapheme_utf8 average time    70us over  400 runs of    85 symbols from emoji.txt
       lex_new_grapheme_utf8 average time    82us over  400 runs of    85 symbols from emoji.txt DFA cleared
 *
 *  I dump footprint now too (this is 64-bit HotSpot VM):
 *
 Parser.java (29038 char): org.antlr.v4.runtime.ANTLRFileStream@6b8e0782d footprint:
      COUNT       AVG       SUM   DESCRIPTION
          2     29164     58328   [C
          1        24        24   java.lang.String
          1        32        32   org.antlr.v4.runtime.ANTLRFileStream
          4               58384   (total)

 RuleContext.java (7625 char): org.antlr.v4.runtime.ANTLRFileStream@76fb7505d footprint:
      COUNT       AVG       SUM   DESCRIPTION
          2      7756     15512   [C
          1        24        24   java.lang.String
          1        32        32   org.antlr.v4.runtime.ANTLRFileStream
          4               15568   (total)

 Parser.java (29038 char): org.antlr.v4.runtime.ANTLRInputStream@1fc1cb1d footprint:
      COUNT       AVG       SUM   DESCRIPTION
          1     65552     65552   [C
          1        32        32   org.antlr.v4.runtime.ANTLRInputStream
          2               65584   (total)

 RuleContext.java (7625 char): org.antlr.v4.runtime.ANTLRInputStream@2c6aa25dd footprint:
      COUNT       AVG       SUM   DESCRIPTION
          1     32784     32784   [C
          1        32        32   org.antlr.v4.runtime.ANTLRInputStream
          2               32816   (total)

 Parser.java (29038 char): org.antlr.v4.runtime.ANTLRInputStream@3d08db0bd footprint:
      COUNT       AVG       SUM   DESCRIPTION
          1     65552     65552   [C
          1        32        32   org.antlr.v4.runtime.ANTLRInputStream
          2               65584   (total)

 udhr_hin.txt (13379 char): org.antlr.v4.runtime.ANTLRInputStream@486dc6f3d footprint:
      COUNT       AVG       SUM   DESCRIPTION
          1     32784     32784   [C
          1        32        32   org.antlr.v4.runtime.ANTLRInputStream
          2               32816   (total)

 Parser.java (29038 char): org.antlr.v4.runtime.CodePointCharStream@798fe5a1d footprint:
      COUNT       AVG       SUM   DESCRIPTION
          1        40        40   [C
          1    131088    131088   [I
          1        24        24   java.lang.String
          1        48        48   java.nio.HeapIntBuffer
          1        32        32   org.antlr.v4.runtime.CodePointCharStream
          5              131232   (total)

 RuleContext.java (7625 char): org.antlr.v4.runtime.CodePointCharStream@29cf5a20d footprint:
      COUNT       AVG       SUM   DESCRIPTION
          1        40        40   [C
          1     32784     32784   [I
          1        24        24   java.lang.String
          1        48        48   java.nio.HeapIntBuffer
          1        32        32   org.antlr.v4.runtime.CodePointCharStream
          5               32928   (total)

 udhr_hin.txt (13379 char): org.antlr.v4.runtime.CodePointCharStream@1adb8a22d footprint:
      COUNT       AVG       SUM   DESCRIPTION
          1        40        40   [C
          1     65552     65552   [I
          1        24        24   java.lang.String
          1        48        48   java.nio.HeapIntBuffer
          1        32        32   org.antlr.v4.runtime.CodePointCharStream
          5               65696   (total)
 *
 *  The "DFA cleared" indicates that the lexer was returned to initial conditions
 *  before the tokenizing of each file.	 As the ALL(*) lexer encounters new input,
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
	public static final String RuleContext_java_file = "Java/src/org/antlr/v4/runtime/RuleContext.java";
	public static final String PerfDir = "org/antlr/v4/test/runtime/java/api/perf";

	public boolean output = true;

	public List<String> streamFootprints = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> vmArgs = runtimeMxBean.getInputArguments();
		System.out.print("Java VM args: ");
		for (String vmArg : vmArgs) {
			if ( !vmArg.startsWith("-D") ) {
				System.out.print(vmArg+" ");
			}
		}
		System.out.println();
//		System.out.println(VM.current().details());

		TimeLexerSpeed tests = new TimeLexerSpeed();

		tests.compilerWarmUp(100);

		int n = 3500;
		tests.load_legacy_java_ascii_file(Parser_java_file, n);
		tests.load_legacy_java_ascii_file(RuleContext_java_file, n);
		tests.load_legacy_java_ascii(Parser_java_file, n);
		tests.load_legacy_java_ascii(RuleContext_java_file, n);
		tests.load_legacy_java_utf8(Parser_java_file, n);
		tests.load_legacy_java_utf8(PerfDir+"/udhr_hin.txt", n);
		tests.load_new_utf8(Parser_java_file, n);
		tests.load_new_utf8(RuleContext_java_file, n);
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

		for (String streamFootprint : tests.streamFootprints) {
			System.out.print(streamFootprint);
		}
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

	public void load_legacy_java_ascii_file(String resourceName, int n) throws Exception {
		URL sampleJavaFile = TimeLexerSpeed.class.getClassLoader().getResource(resourceName);
		if ( sampleJavaFile==null ) {
			System.err.println("Can't run load_legacy_java_ascii_file from jar (or can't find "+resourceName+")");
			return; // cannot find resource
		}
		if ( !new File(sampleJavaFile.getFile()).exists() ) {
			System.err.println("Can't run load_legacy_java_ascii_file from jar (or can't find "+resourceName+")");
			return;
		}
		long start = System.nanoTime();
		CharStream[] input = new CharStream[n]; // keep refs around so we can average memory
		for (int i = 0; i<n; i++) {
			input[i] = new ANTLRFileStream(sampleJavaFile.getFile());
		}
		long stop = System.nanoTime();
		long tus = (stop-start)/1000;
		int size = input[0].size();
		String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
		GraphLayout olayout = GraphLayout.parseInstance((Object) input[0]);
		long streamSize = olayout.totalSize();
		streamFootprints.add(basename(resourceName)+" ("+size+" char): "+olayout.toFootprint());
		if ( output ) System.out.printf("%27s average time %5dus size %6db over %4d loads of %5d symbols from %s\n",
		                                currentMethodName,
		                                tus/n,
		                                streamSize,
		                                n,
		                                size,
		                                basename(resourceName));
	}

	public void load_legacy_java_ascii(String resourceName, int n) throws Exception {
		CharStream[] input = new CharStream[n]; // keep refs around so we can average memory
		ClassLoader loader = TimeLexerSpeed.class.getClassLoader();
		InputStream[] streams = new InputStream[n];
		for (int i = 0; i<n; i++) {
			streams[i] = loader.getResourceAsStream(resourceName);
		}
		long start = System.nanoTime(); // track only time to suck data out of stream
		for (int i = 0; i<n; i++) {
			try (InputStream is = streams[i]) {
				input[i] = new ANTLRInputStream(is);
			}
		}
		long stop = System.nanoTime();
		long tus = (stop-start)/1000;
		int size = input[0].size();
		long streamSize = GraphLayout.parseInstance((Object)input[0]).totalSize();
		streamFootprints.add(basename(resourceName)+" ("+size+" char): "+GraphLayout.parseInstance((Object)input[0]).toFootprint());
		String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
		if ( output ) System.out.printf("%27s average time %5dus size %6db over %4d loads of %5d symbols from %s\n",
		                                currentMethodName,
		                                tus/n,
		                                streamSize,
		                                n,
		                                size,
		                                basename(resourceName));
	}

	public void load_legacy_java_utf8(String resourceName, int n) throws Exception {
		CharStream[] input = new CharStream[n]; // keep refs around so we can average memory
		ClassLoader loader = TimeLexerSpeed.class.getClassLoader();
		InputStream[] streams = new InputStream[n];
		for (int i = 0; i<n; i++) {
			streams[i] = loader.getResourceAsStream(resourceName);
		}
		long start = System.nanoTime(); // track only time to suck data out of stream
		for (int i = 0; i<n; i++) {
			try (InputStream is = streams[i];
			     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
			     BufferedReader br = new BufferedReader(isr)) {
				input[i] = new ANTLRInputStream(br);
			}
		}
		long stop = System.nanoTime();
		long tus = (stop-start)/1000;
		int size = input[0].size();
		long streamSize = GraphLayout.parseInstance((Object)input[0]).totalSize();
		streamFootprints.add(basename(resourceName)+" ("+size+" char): "+GraphLayout.parseInstance((Object)input[0]).toFootprint());
		String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
		if ( output ) System.out.printf("%27s average time %5dus size %6db over %4d loads of %5d symbols from %s\n",
		                                currentMethodName,
		                                tus/n,
		                                streamSize,
		                                n,
		                                size,
		                                basename(resourceName));
	}

	public void load_new_utf8(String resourceName, int n) throws Exception {
		CharStream[] input = new CharStream[n]; // keep refs around so we can average memory
		ClassLoader loader = TimeLexerSpeed.class.getClassLoader();
		InputStream[] streams = new InputStream[n];
		for (int i = 0; i<n; i++) {
			streams[i] = loader.getResourceAsStream(resourceName);
		}
		URLConnection uc = null;
		long streamLength = getResourceSize(loader, resourceName);
		long start = System.nanoTime(); // track only time to suck data out of stream
		for (int i = 0; i<n; i++) {
			try (InputStream is = streams[i]) {
				input[i] = CharStreams.fromStream(is, StandardCharsets.UTF_8, streamLength);
			}
		}
		long stop = System.nanoTime();
		long tus = (stop-start)/1000;
		int size = input[0].size();
		long streamSize = GraphLayout.parseInstance((Object)input[0]).totalSize();
		streamFootprints.add(basename(resourceName)+" ("+size+" char): "+GraphLayout.parseInstance((Object)input[0]).toFootprint());
		String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
		if ( output ) System.out.printf("%27s average time %5dus size %6db over %4d loads of %5d symbols from %s\n",
						currentMethodName,
						tus/n,
						streamSize,
						n,
						size,
						basename(resourceName));
	}

	public void lex_legacy_java_utf8(int n, boolean clearLexerDFACache) throws Exception {
		try (InputStream is = TimeLexerSpeed.class.getClassLoader().getResourceAsStream(Parser_java_file);
		     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
		     BufferedReader br = new BufferedReader(isr)) {
			CharStream input = new ANTLRInputStream(br);
			JavaLexer lexer = new JavaLexer(input);
			double avg = tokenize(lexer, n, clearLexerDFACache);
			String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
			if ( output ) System.out.printf("%27s average time %5dus over %4d runs of %5d symbols%s\n",
							currentMethodName,
							(int)avg,
							n,
							input.size(),
							clearLexerDFACache ? " DFA cleared" : "");
		}
	}

	public void lex_new_java_utf8(int n, boolean clearLexerDFACache) throws Exception {
		ClassLoader loader = TimeLexerSpeed.class.getClassLoader();
		try (InputStream is = loader.getResourceAsStream(Parser_java_file);) {
			long size = getResourceSize(loader, Parser_java_file);
			CharStream input = CharStreams.fromStream(is, StandardCharsets.UTF_8, size);
			JavaLexer lexer = new JavaLexer(input);
			double avg = tokenize(lexer, n, clearLexerDFACache);
			String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
			if ( output ) System.out.printf("%27s average time %5dus over %4d runs of %5d symbols%s\n",
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
			if ( output ) System.out.printf("%27s average time %5dus over %4d runs of %5d symbols from %s%s\n",
							currentMethodName,
							(int)avg,
							n,
							input.size(),
							fileName,
							clearLexerDFACache ? " DFA cleared" : "");
		}
	}

	public void lex_new_grapheme_utf8(String fileName, int n, boolean clearLexerDFACache) throws Exception {
		String resourceName = PerfDir+"/"+fileName;
		ClassLoader loader = TimeLexerSpeed.class.getClassLoader();
		try (InputStream is = loader.getResourceAsStream(resourceName)) {
			long size = getResourceSize(loader, resourceName);
			CharStream input = CharStreams.fromStream(is, StandardCharsets.UTF_8, size);
			graphemesLexer lexer = new graphemesLexer(input);
			double avg = tokenize(lexer, n, clearLexerDFACache);
			String currentMethodName = new Exception().getStackTrace()[0].getMethodName();
			if ( output ) System.out.printf("%27s average time %5dus over %4d runs of %5d symbols from %s%s\n",
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

	public static final long getResourceSize(ClassLoader loader, String resourceName) throws IOException {
		URLConnection uc = null;
		try {
			// Sadly, URLConnection is not AutoCloseable, but it leaks resources if
			// we don't close its stream.
			uc = loader.getResource(resourceName).openConnection();
			return uc.getContentLengthLong();
		} finally {
			if (uc != null) {
				uc.getInputStream().close();
			}
		}
	}
}
