/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.javascript;

import org.antlr.v4.Tool;
import org.antlr.v4.automata.ATNFactory;
import org.antlr.v4.automata.ATNPrinter;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.WritableToken;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.test.runtime.ErrorQueue;
import org.antlr.v4.test.runtime.RuntimeTestSupport;
import org.antlr.v4.test.runtime.StreamVacuum;
import org.antlr.v4.test.runtime.TestContext;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.DOTGenerator;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarSemanticsMessage;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BaseNodeTest implements RuntimeTestSupport {
	// -J-Dorg.antlr.v4.test.BaseTest.level=FINE
	// private static final Logger LOGGER =
	// Logger.getLogger(BaseTest.class.getName());

	public static final String newline = System.getProperty("line.separator");
	public static final String pathSep = System.getProperty("path.separator");

	public String tmpdir = null;

	/**
	 * If error during parser execution, store stderr here; can't return stdout
	 * and stderr. This doesn't trap errors from running antlr.
	 */
	protected String stderrDuringParse;

	/** Errors found while running antlr */
	protected StringBuilder antlrToolErrors;

	@Override
	public void testSetUp() throws Exception {
		// new output dir for each test
		String prop = System.getProperty("antlr-javascript-test-dir");
		if (prop != null && prop.length() > 0) {
			tmpdir = prop;
		}
		else {
			tmpdir = new File(System.getProperty("java.io.tmpdir"), getClass()
				.getSimpleName()+"-"+Thread.currentThread().getName()+"-"+System.currentTimeMillis())
				.getAbsolutePath();
		}
		File dir = new File(tmpdir);
		if (dir.exists())
			this.eraseFiles(dir);
		antlrToolErrors = new StringBuilder();
	}

	@Override
	public void testTearDown() throws Exception {
	}

	@Override
	public String getTmpDir() {
		return tmpdir;
	}

	@Override
	public String getStdout() {
		return null;
	}

	@Override
	public String getParseErrors() {
		return stderrDuringParse;
	}

	@Override
	public String getANTLRToolErrors() {
		if ( antlrToolErrors.length()==0 ) {
			return null;
		}
		return antlrToolErrors.toString();
	}

	protected ATN createATN(Grammar g, boolean useSerializer) {
		if (g.atn == null) {
			semanticProcess(g);
			assertEquals(0, g.tool.getNumErrors());

			ParserATNFactory f;
			if (g.isLexer()) {
				f = new LexerATNFactory((LexerGrammar) g);
			}
			else {
				f = new ParserATNFactory(g);
			}

			g.atn = f.createATN();
			assertEquals(0, g.tool.getNumErrors());
		}

		ATN atn = g.atn;
		if (useSerializer) {
			char[] serialized = ATNSerializer.getSerializedAsChars(atn);
			return new ATNDeserializer().deserialize(serialized);
		}

		return atn;
	}

	protected void semanticProcess(Grammar g) {
		if (g.ast != null && !g.ast.hasErrors) {
			System.out.println(g.ast.toStringTree());
			Tool antlr = new Tool();
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();
			if (g.getImportedGrammars() != null) { // process imported grammars
													// (if any)
				for (Grammar imp : g.getImportedGrammars()) {
					antlr.processNonCombinedGrammar(imp, false);
				}
			}
		}
	}

	protected String execLexer(String grammarFileName, String grammarStr,
	                           String lexerName, String input) {
		return execLexer(grammarFileName, grammarStr, lexerName, input, false);
	}

	@Override
	public  String execLexer(String grammarFileName, String grammarStr,
	                         String lexerName, String input, boolean showDFA) {
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
		                                                grammarStr, null, lexerName, "-no-listener");
		assertTrue(success);
		writeFile(tmpdir, "input", input);
		writeLexerTestFile(lexerName, showDFA);
		writeFile(tmpdir, "package.json", "{\"type\": \"module\"}");
		String output = execModule("Test.js");
		if ( output!=null && output.length()==0 ) {
			output = null;
		}
		return output;
	}

	@Override
	public String execParser(String grammarFileName, String grammarStr,
	                         String parserName, String lexerName, String listenerName,
	                         String visitorName, String startRuleName, String input,
	                         boolean showDiagnosticErrors)
	{
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
				grammarStr, parserName, lexerName, "-visitor");
		assertTrue(success);
		writeFile(tmpdir, "input", input);
		rawBuildRecognizerTestFile(parserName, lexerName, listenerName,
		                           visitorName, startRuleName, showDiagnosticErrors);
		writeFile(tmpdir, "package.json", "{\"type\": \"module\"}");
		return execRecognizer();
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
	                                                String grammarStr, String parserName, String lexerName,
	                                                String... extraOptions) {
		return rawGenerateAndBuildRecognizer(grammarFileName, grammarStr,
		                                     parserName, lexerName, false, extraOptions);
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
	                                                String grammarStr, String parserName, String lexerName,
	                                                boolean defaultListener, String... extraOptions) {
		ErrorQueue equeue = antlrOnString(getTmpDir(), "JavaScript", grammarFileName, grammarStr,
		                                  defaultListener, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}

		List<String> files = new ArrayList<String>();
		if (lexerName != null) {
			files.add(lexerName + ".js");
		}
		if (parserName != null) {
			files.add(parserName + ".js");
			Set<String> optionsSet = new HashSet<String>(
					Arrays.asList(extraOptions));
			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarFileName.substring(0,
						grammarFileName.lastIndexOf('.'))
						+ "Listener.js");
			}
			if (optionsSet.contains("-visitor")) {
				files.add(grammarFileName.substring(0,
						grammarFileName.lastIndexOf('.'))
						+ "Visitor.js");
			}
		}
		return true; // allIsWell: no compile
	}

	protected void rawBuildRecognizerTestFile(String parserName,
			String lexerName, String listenerName, String visitorName,
			String parserStartRuleName, boolean debug) {
		this.stderrDuringParse = null;
		if (parserName == null) {
			writeLexerTestFile(lexerName, false);
		}
		else {
			writeParserTestFile(parserName, lexerName, listenerName,
					visitorName, parserStartRuleName, debug);
		}
	}

	public String execRecognizer() {
		return execModule("Test.js");
	}

	public String execModule(String fileName) {
		try {
			String npmPath = locateNpm();
			if(!TestContext.isTravisCI()) {
				installRuntime(npmPath);
				registerRuntime(npmPath);
			}
			String modulePath = new File(new File(tmpdir), fileName)
					.getAbsolutePath();
			linkRuntime(npmPath);
			String nodejsPath = locateNodeJS();
			String inputPath = new File(new File(tmpdir), "input")
					.getAbsolutePath();
			ProcessBuilder builder = new ProcessBuilder(nodejsPath, modulePath,
					inputPath);
			builder.environment().put("NODE_PATH", tmpdir);
			builder.directory(new File(tmpdir));
			Process process = builder.start();
			StreamVacuum stdoutVacuum = new StreamVacuum(
					process.getInputStream());
			StreamVacuum stderrVacuum = new StreamVacuum(
					process.getErrorStream());
			stdoutVacuum.start();
			stderrVacuum.start();
			// TODO switch to jdk 8
			process.waitFor();
			// if(!process.waitFor(1L, TimeUnit.MINUTES))
			//	process.destroyForcibly();
			stdoutVacuum.join();
			stderrVacuum.join();
			String output = stdoutVacuum.toString();
			if ( output.length()==0 ) {
				output = null;
			}
			if (stderrVacuum.toString().length() > 0) {
				this.stderrDuringParse = stderrVacuum.toString();
			}
			return output;
		} catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
			System.err.println();
			return null;
		}
	}

	private void installRuntime(String npmPath) throws IOException, InterruptedException {
		String runtimePath = locateRuntime();
		ProcessBuilder builder = new ProcessBuilder(npmPath, "install");
		builder.directory(new File(runtimePath));
		builder.redirectError(new File(tmpdir, "error.txt"));
		builder.redirectOutput(new File(tmpdir, "output.txt"));
		Process process = builder.start();
		// TODO switch to jdk 8
		process.waitFor();
		// if(!process.waitFor(30L, TimeUnit.SECONDS))
		// 	process.destroyForcibly();
		int error = process.exitValue();
		if(error!=0)
			throw new IOException("'npm install' failed");
	}

	private void registerRuntime(String npmPath) throws IOException, InterruptedException {
		String runtimePath = locateRuntime();
		ProcessBuilder builder = new ProcessBuilder(npmPath, "link");
		builder.directory(new File(runtimePath));
		builder.redirectError(new File(tmpdir, "error.txt"));
		builder.redirectOutput(new File(tmpdir, "output.txt"));
		Process process = builder.start();
		// TODO switch to jdk 8
		process.waitFor();
		// if(!process.waitFor(30L, TimeUnit.SECONDS))
		//	process.destroyForcibly();
		int error = process.exitValue();
		if(error!=0)
			throw new IOException("'npm link' failed");
	}

	private void linkRuntime(String npmPath) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder(npmPath, "link", "antlr4");
		builder.directory(new File(tmpdir));
		builder.redirectError(new File(tmpdir, "error.txt"));
		builder.redirectOutput(new File(tmpdir, "output.txt"));
		Process process = builder.start();
		// TODO switch to jdk 8
		process.waitFor();
		// if(!process.waitFor(30L, TimeUnit.SECONDS))
		//	process.destroyForcibly();
		int error = process.exitValue();
		if(error!=0)
			throw new IOException("'npm link antlr4' failed");
	}

	private boolean canExecute(String tool) {
		try {
			ProcessBuilder builder = new ProcessBuilder(tool, "--version");
			builder.redirectErrorStream(true);
			Process process = builder.start();
			StreamVacuum vacuum = new StreamVacuum(process.getInputStream());
			vacuum.start();
			// TODO switch to jdk 8
			process.waitFor();
			// if(!process.waitFor(30L, TimeUnit.SECONDS))
			//	process.destroyForcibly();
			vacuum.join();
			return process.exitValue() == 0;
		} catch (Exception e) {
			return false;
		}
	}

	private String locateNpm() {
		// typically /usr/local/bin/npm
		String prop = System.getProperty("antlr-javascript-npm");
		if ( prop!=null && prop.length()!=0 ) {
			return prop;
		}
		return "npm"; // everywhere
	}

	private String locateNodeJS() {
		// typically /usr/local/bin/node
		String prop = System.getProperty("antlr-javascript-nodejs");
		if ( prop!=null && prop.length()!=0 ) {
			return prop;
		}
		if (canExecute("nodejs")) {
			return "nodejs"; // nodejs on Debian without node-legacy package
		}
		return "node"; // everywhere else
	}

	private String locateRuntime() {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		final URL runtimeSrc = loader.getResource("JavaScript");
		if ( runtimeSrc==null ) {
			throw new RuntimeException("Cannot find JavaScript runtime");
		}
		if(isWindows()){
			return runtimeSrc.getPath().replaceFirst("/", "");
		}
		return runtimeSrc.getPath();
	}

	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}


	protected void writeParserTestFile(String parserName, String lexerName,
			String listenerName, String visitorName,
			String parserStartRuleName, boolean debug) {
		ST outputFileST = new ST(
				"import antlr4 from 'antlr4';\n"
						+ "import <lexerName> from './<lexerName>.js';\n"
						+ "import <parserName> from './<parserName>.js';\n"
						+ "import <listenerName> from './<listenerName>.js';\n"
						+ "import <visitorName> from './<visitorName>.js';\n"
						+ "\n"
						+ "class TreeShapeListener extends antlr4.tree.ParseTreeListener {\n" +
						"    enterEveryRule(ctx) {\n" +
						"        for (let i = 0; i \\< ctx.getChildCount; i++) {\n" +
						"            const child = ctx.getChild(i)\n" +
						"            const parent = child.parentCtx\n" +
						"            if (parent.getRuleContext() !== ctx || !(parent instanceof antlr4.tree.RuleNode)) {\n" +
						"                throw `Invalid parse tree shape detected.`\n" +
						"            }\n" +
						"        }\n" +
						"    }\n" +
						"}\n"
						+ "\n"
						+ "function main(argv) {\n"
						+ "    var input = new antlr4.FileStream(argv[2], true);\n"
						+ "    var lexer = new <lexerName>(input);\n"
						+ "    var stream = new antlr4.CommonTokenStream(lexer);\n"
						+ "<createParser>"
						+ "    parser.buildParseTrees = true;\n"
						+ "	   const printer = function() {\n"
						+ "		this.println = function(s) { console.log(s); }\n"
						+ "		this.print = function(s) { process.stdout.write(s); }\n"
						+ "		return this;\n"
						+ "	 };\n"
						+ "    parser.printer = new printer();\n"
						+ "    var tree = parser.<parserStartRuleName>();\n"
						+ "    antlr4.tree.ParseTreeWalker.DEFAULT.walk(new TreeShapeListener(), tree);\n"
						+ "}\n" + "\n" + "main(process.argv);\n" + "\n");
		ST createParserST = new ST(
				"	var parser = new <parserName>(stream);\n");
		if (debug) {
			createParserST = new ST(
					"	var parser = new <parserName>(stream);\n"
							+ "	parser.addErrorListener(new antlr4.error.DiagnosticErrorListener());\n");
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("listenerName", listenerName);
		outputFileST.add("visitorName", visitorName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(tmpdir, "Test.js", outputFileST.render());
	}

	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
				"import antlr4 from 'antlr4';\n"
						+ "import <lexerName> from './<lexerName>.js';\n"
						+ "\n"
						+ "function main(argv) {\n"
						+ "    var input = new antlr4.FileStream(argv[2], true);\n"
						+ "    var lexer = new <lexerName>(input);\n"
						+ "    var stream = new antlr4.CommonTokenStream(lexer);\n"
						+ "    stream.fill();\n"
						+ "    for(var i=0; i\\<stream.tokens.length; i++) {\n"
						+ "		console.log(stream.tokens[i].toString());\n"
						+ "    }\n"
						+ (showDFA ? "    process.stdout.write(lexer._interp.decisionToDFA[antlr4.Lexer.DEFAULT_MODE].toLexerString());\n"
								: "") + "}\n" + "\n" + "main(process.argv);\n"
						+ "\n");
		outputFileST.add("lexerName", lexerName);
		writeFile(tmpdir, "Test.js", outputFileST.render());
	}

	protected void eraseFiles(File dir) {
		String[] files = dir.list();
		for (int i = 0; files != null && i < files.length; i++) {
			new File(dir, files[i]).delete();
		}
	}

	@Override
	public void eraseTempDir() {
		boolean doErase = true;
		String propName = "antlr-javascript-erase-test-dir";
		String prop = System.getProperty(propName);
		if (prop != null && prop.length() > 0)
			doErase = Boolean.getBoolean(prop);
		if (doErase) {
			File tmpdirF = new File(tmpdir);
			if (tmpdirF.exists()) {
				eraseFiles(tmpdirF);
				tmpdirF.delete();
			}
		}
	}


	/** Sort a list */
	public <T extends Comparable<? super T>> List<T> sort(List<T> data) {
		List<T> dup = new ArrayList<T>(data);
		Collections.sort(dup);
		return dup;
	}

	/** Return map sorted by key */
	public <K extends Comparable<? super K>, V> LinkedHashMap<K, V> sort(
			Map<K, V> data) {
		LinkedHashMap<K, V> dup = new LinkedHashMap<K, V>();
		List<K> keys = new ArrayList<K>(data.keySet());
		Collections.sort(keys);
		for (K k : keys) {
			dup.put(k, data.get(k));
		}
		return dup;
	}
}
