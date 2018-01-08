/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.javascript.node;

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

	protected org.antlr.v4.Tool newTool(String[] args) {
		Tool tool = new Tool(args);
		return tool;
	}

	protected Tool newTool() {
		org.antlr.v4.Tool tool = new Tool(new String[] { "-o", tmpdir });
		return tool;
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

	IntegerList getTypesFromString(Grammar g, String expecting) {
		IntegerList expectingTokenTypes = new IntegerList();
		if (expecting != null && !expecting.trim().isEmpty()) {
			for (String tname : expecting.replace(" ", "").split(",")) {
				int ttype = g.getTokenType(tname);
				expectingTokenTypes.add(ttype);
			}
		}
		return expectingTokenTypes;
	}

	public IntegerList getTokenTypesViaATN(String input,
			LexerATNSimulator lexerATN) {
		ANTLRInputStream in = new ANTLRInputStream(input);
		IntegerList tokenTypes = new IntegerList();
		int ttype;
		do {
			ttype = lexerATN.match(in, Lexer.DEFAULT_MODE);
			tokenTypes.add(ttype);
		} while (ttype != Token.EOF);
		return tokenTypes;
	}

	public List<String> getTokenTypes(LexerGrammar lg, ATN atn, CharStream input) {
		LexerATNSimulator interp = new LexerATNSimulator(atn,
				new DFA[] { new DFA(
						atn.modeToStartState.get(Lexer.DEFAULT_MODE)) }, null);
		List<String> tokenTypes = new ArrayList<String>();
		int ttype;
		boolean hitEOF = false;
		do {
			if (hitEOF) {
				tokenTypes.add("EOF");
				break;
			}
			int t = input.LA(1);
			ttype = interp.match(input, Lexer.DEFAULT_MODE);
			if (ttype == Token.EOF) {
				tokenTypes.add("EOF");
			}
			else {
				tokenTypes.add(lg.typeToTokenList.get(ttype));
			}

			if (t == IntStream.EOF) {
				hitEOF = true;
			}
		} while (ttype != Token.EOF);
		return tokenTypes;
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
		String output = execModule("Test.js");
		if ( output.length()==0 ) {
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
		String nodejsPath = locateNodeJS();
		String runtimePath = locateRuntime();
		String modulePath = new File(new File(tmpdir), fileName)
				.getAbsolutePath();
		String inputPath = new File(new File(tmpdir), "input")
				.getAbsolutePath();
		try {
			ProcessBuilder builder = new ProcessBuilder(nodejsPath, modulePath,
					inputPath);
			builder.environment().put("NODE_PATH",
					runtimePath + File.pathSeparator + tmpdir);
			builder.directory(new File(tmpdir));
			Process process = builder.start();
			StreamVacuum stdoutVacuum = new StreamVacuum(
					process.getInputStream());
			StreamVacuum stderrVacuum = new StreamVacuum(
					process.getErrorStream());
			stdoutVacuum.start();
			stderrVacuum.start();
			process.waitFor();
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
		}
		catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

	private String locateTool(String tool) {
		String[] roots = { "/usr/bin/", "/usr/local/bin/" };
		for (String root : roots) {
			if (new File(root + tool).exists()) {
				return root + tool;
			}
		}
		return null;
	}

	private boolean canExecute(String tool) {
		try {
			ProcessBuilder builder = new ProcessBuilder(tool, "--version");
			builder.redirectErrorStream(true);
			Process process = builder.start();
			StreamVacuum vacuum = new StreamVacuum(process.getInputStream());
			vacuum.start();
			process.waitFor();
			vacuum.join();
			return process.exitValue() == 0;
		}
		catch (Exception e) {
			;
		}
		return false;
	}

	private String locateNodeJS() {
		// typically /usr/local/bin/node
		String propName = "antlr-javascript-nodejs";
		String prop = System.getProperty(propName);

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
		final URL runtimeSrc = loader.getResource("JavaScript/src");
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

	// void ambig(List<Message> msgs, int[] expectedAmbigAlts, String
	// expectedAmbigInput)
	// throws Exception
	// {
	// ambig(msgs, 0, expectedAmbigAlts, expectedAmbigInput);
	// }

	// void ambig(List<Message> msgs, int i, int[] expectedAmbigAlts, String
	// expectedAmbigInput)
	// throws Exception
	// {
	// List<Message> amsgs = getMessagesOfType(msgs, AmbiguityMessage.class);
	// AmbiguityMessage a = (AmbiguityMessage)amsgs.get(i);
	// if ( a==null ) assertNull(expectedAmbigAlts);
	// else {
	// assertEquals(a.conflictingAlts.toString(),
	// Arrays.toString(expectedAmbigAlts));
	// }
	// assertEquals(expectedAmbigInput, a.input);
	// }

	// void unreachable(List<Message> msgs, int[] expectedUnreachableAlts)
	// throws Exception
	// {
	// unreachable(msgs, 0, expectedUnreachableAlts);
	// }

	// void unreachable(List<Message> msgs, int i, int[]
	// expectedUnreachableAlts)
	// throws Exception
	// {
	// List<Message> amsgs = getMessagesOfType(msgs,
	// UnreachableAltsMessage.class);
	// UnreachableAltsMessage u = (UnreachableAltsMessage)amsgs.get(i);
	// if ( u==null ) assertNull(expectedUnreachableAlts);
	// else {
	// assertEquals(u.conflictingAlts.toString(),
	// Arrays.toString(expectedUnreachableAlts));
	// }
	// }

	List<ANTLRMessage> getMessagesOfType(List<ANTLRMessage> msgs,
			Class<? extends ANTLRMessage> c) {
		List<ANTLRMessage> filtered = new ArrayList<ANTLRMessage>();
		for (ANTLRMessage m : msgs) {
			if (m.getClass() == c)
				filtered.add(m);
		}
		return filtered;
	}

	void checkRuleATN(Grammar g, String ruleName, String expecting) {
		ParserATNFactory f = new ParserATNFactory(g);
		ATN atn = f.createATN();

		DOTGenerator dot = new DOTGenerator(g);
		System.out
				.println(dot.getDOT(atn.ruleToStartState[g.getRule(ruleName).index]));

		Rule r = g.getRule(ruleName);
		ATNState startState = atn.ruleToStartState[r.index];
		ATNPrinter serializer = new ATNPrinter(g, startState);
		String result = serializer.asString();

		// System.out.print(result);
		assertEquals(expecting, result);
	}

	public void testActions(String templates, String actionName, String action,
			String expected) throws org.antlr.runtime.RecognitionException {
		int lp = templates.indexOf('(');
		String name = templates.substring(0, lp);
		STGroup group = new STGroupString(templates);
		ST st = group.getInstanceOf(name);
		st.add(actionName, action);
		String grammar = st.render();
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(grammar, equeue);
		if (g.ast != null && !g.ast.hasErrors) {
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();

			ATNFactory factory = new ParserATNFactory(g);
			if (g.isLexer())
				factory = new LexerATNFactory((LexerGrammar) g);
			g.atn = factory.createATN();

			CodeGenerator gen = new CodeGenerator(g);
			ST outputFileST = gen.generateParser();
			String output = outputFileST.render();
			// System.out.println(output);
			String b = "#" + actionName + "#";
			int start = output.indexOf(b);
			String e = "#end-" + actionName + "#";
			int end = output.indexOf(e);
			String snippet = output.substring(start + b.length(), end);
			assertEquals(expected, snippet);
		}
		if (equeue.size() > 0) {
			System.err.println(equeue.toString());
		}
	}

	protected void checkGrammarSemanticsError(ErrorQueue equeue,
			GrammarSemanticsMessage expectedMessage) throws Exception {
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.errors.size(); i++) {
			ANTLRMessage m = equeue.errors.get(i);
			if (m.getErrorType() == expectedMessage.getErrorType()) {
				foundMsg = m;
			}
		}
		assertNotNull("no error; " + expectedMessage.getErrorType()
				+ " expected", foundMsg);
		assertTrue("error is not a GrammarSemanticsMessage",
				foundMsg instanceof GrammarSemanticsMessage);
		assertEquals(Arrays.toString(expectedMessage.getArgs()),
				Arrays.toString(foundMsg.getArgs()));
		if (equeue.size() != 1) {
			System.err.println(equeue);
		}
	}

	protected void checkGrammarSemanticsWarning(ErrorQueue equeue,
			GrammarSemanticsMessage expectedMessage) throws Exception {
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.warnings.size(); i++) {
			ANTLRMessage m = equeue.warnings.get(i);
			if (m.getErrorType() == expectedMessage.getErrorType()) {
				foundMsg = m;
			}
		}
		assertNotNull("no error; " + expectedMessage.getErrorType()
				+ " expected", foundMsg);
		assertTrue("error is not a GrammarSemanticsMessage",
				foundMsg instanceof GrammarSemanticsMessage);
		assertEquals(Arrays.toString(expectedMessage.getArgs()),
				Arrays.toString(foundMsg.getArgs()));
		if (equeue.size() != 1) {
			System.err.println(equeue);
		}
	}

	protected void checkError(ErrorQueue equeue, ANTLRMessage expectedMessage)
			throws Exception {
		// System.out.println("errors="+equeue);
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.errors.size(); i++) {
			ANTLRMessage m = equeue.errors.get(i);
			if (m.getErrorType() == expectedMessage.getErrorType()) {
				foundMsg = m;
			}
		}
		assertTrue("no error; " + expectedMessage.getErrorType() + " expected",
				!equeue.errors.isEmpty());
		assertTrue("too many errors; " + equeue.errors,
				equeue.errors.size() <= 1);
		assertNotNull(
				"couldn't find expected error: "
						+ expectedMessage.getErrorType(), foundMsg);
		/*
		 * assertTrue("error is not a GrammarSemanticsMessage", foundMsg
		 * instanceof GrammarSemanticsMessage);
		 */
		assertArrayEquals(expectedMessage.getArgs(), foundMsg.getArgs());
	}

	public static class FilteringTokenStream extends CommonTokenStream {
		public FilteringTokenStream(TokenSource src) {
			super(src);
		}

		Set<Integer> hide = new HashSet<Integer>();

		@Override
		protected boolean sync(int i) {
			if (!super.sync(i)) {
				return false;
			}

			Token t = get(i);
			if (hide.contains(t.getType())) {
				((WritableToken) t).setChannel(Token.HIDDEN_CHANNEL);
			}

			return true;
		}

		public void setTokenTypeChannel(int ttype, int channel) {
			hide.add(ttype);
		}
	}

	protected void mkdir(String dir) {
		File f = new File(dir);
		f.mkdirs();
	}

	protected void writeParserTestFile(String parserName, String lexerName,
			String listenerName, String visitorName,
			String parserStartRuleName, boolean debug) {
		ST outputFileST = new ST(
				"var antlr4 = require('antlr4');\n"
						+ "var <lexerName> = require('./<lexerName>');\n"
						+ "var <parserName> = require('./<parserName>');\n"
						+ "var <listenerName> = require('./<listenerName>').<listenerName>;\n"
						+ "var <visitorName> = require('./<visitorName>').<visitorName>;\n"
						+ "\n"
						+ "function TreeShapeListener() {\n"
						+ "	antlr4.tree.ParseTreeListener.call(this);\n"
						+ "	return this;\n"
						+ "}\n"
						+ "\n"
						+ "TreeShapeListener.prototype = Object.create(antlr4.tree.ParseTreeListener.prototype);\n"
						+ "TreeShapeListener.prototype.constructor = TreeShapeListener;\n"
						+ "\n"
						+ "TreeShapeListener.prototype.enterEveryRule = function(ctx) {\n"
						+ "	for(var i=0;i\\<ctx.getChildCount; i++) {\n"
						+ "		var child = ctx.getChild(i);\n"
						+ "       var parent = child.parentCtx;\n"
						+ "       if(parent.getRuleContext() !== ctx || !(parent instanceof antlr4.tree.RuleNode)) {\n"
						+ "           throw \"Invalid parse tree shape detected.\";\n"
						+ "		}\n"
						+ "	}\n"
						+ "};\n"
						+ "\n"
						+ "function main(argv) {\n"
						+ "    var input = new antlr4.FileStream(argv[2], true);\n"
						+ "    var lexer = new <lexerName>.<lexerName>(input);\n"
						+ "    var stream = new antlr4.CommonTokenStream(lexer);\n"
						+ "<createParser>"
						+ "    parser.buildParseTrees = true;\n"
						+ "	 printer = function() {\n"
						+ "		this.println = function(s) { console.log(s); }\n"
						+ "		this.print = function(s) { process.stdout.write(s); }\n"
						+ "		return this;\n"
						+ "	 };\n"
						+ "    parser.printer = new printer();\n"
						+ "    var tree = parser.<parserStartRuleName>();\n"
						+ "    antlr4.tree.ParseTreeWalker.DEFAULT.walk(new TreeShapeListener(), tree);\n"
						+ "}\n" + "\n" + "main(process.argv);\n" + "\n");
		ST createParserST = new ST(
				"	var parser = new <parserName>.<parserName>(stream);\n");
		if (debug) {
			createParserST = new ST(
					"	var parser = new <parserName>.<parserName>(stream);\n"
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
				"var antlr4 = require('antlr4');\n"
						+ "var <lexerName> = require('./<lexerName>');\n"
						+ "\n"
						+ "function main(argv) {\n"
						+ "    var input = new antlr4.FileStream(argv[2], true);\n"
						+ "    var lexer = new <lexerName>.<lexerName>(input);\n"
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

	public void writeRecognizer(String parserName, String lexerName,
			String listenerName, String visitorName,
			String parserStartRuleName, boolean debug) {
		if (parserName == null) {
			writeLexerTestFile(lexerName, debug);
		}
		else {
			writeParserTestFile(parserName, lexerName, listenerName,
					visitorName, parserStartRuleName, debug);
		}
	}

	protected void eraseFiles(final String filesEndingWith) {
		File tmpdirF = new File(tmpdir);
		String[] files = tmpdirF.list();
		for (int i = 0; files != null && i < files.length; i++) {
			if (files[i].endsWith(filesEndingWith)) {
				new File(tmpdir + "/" + files[i]).delete();
			}
		}
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

	public String getFirstLineOfException() {
		if (this.stderrDuringParse == null) {
			return null;
		}
		String[] lines = this.stderrDuringParse.split("\n");
		String prefix = "Exception in thread \"main\" ";
		return lines[0].substring(prefix.length(), lines[0].length());
	}

	/**
	 * When looking at a result set that consists of a Map/HashTable we cannot
	 * rely on the output order, as the hashing algorithm or other aspects of
	 * the implementation may be different on differnt JDKs or platforms. Hence
	 * we take the Map, convert the keys to a List, sort them and Stringify the
	 * Map, which is a bit of a hack, but guarantees that we get the same order
	 * on all systems. We assume that the keys are strings.
	 *
	 * @param m
	 *            The Map that contains keys we wish to return in sorted order
	 * @return A string that represents all the keys in sorted order.
	 */
	public <K, V> String sortMapToString(Map<K, V> m) {
		// Pass in crap, and get nothing back
		//
		if (m == null) {
			return null;
		}

		System.out.println("Map toString looks like: " + m.toString());

		// Sort the keys in the Map
		//
		TreeMap<K, V> nset = new TreeMap<K, V>(m);

		System.out.println("Tree map looks like: " + nset.toString());
		return nset.toString();
	}

	public List<String> realElements(List<String> elements) {
		return elements.subList(Token.MIN_USER_TOKEN_TYPE, elements.size());
	}

	public void assertNotNullOrEmpty(String message, String text) {
		assertNotNull(message, text);
		assertFalse(message, text.isEmpty());
	}

	public void assertNotNullOrEmpty(String text) {
		assertNotNull(text);
		assertFalse(text.isEmpty());
	}

	public static class IntTokenStream implements TokenStream {
		IntegerList types;
		int p = 0;

		public IntTokenStream(IntegerList types) {
			this.types = types;
		}

		@Override
		public void consume() {
			p++;
		}

		@Override
		public int LA(int i) {
			return LT(i).getType();
		}

		@Override
		public int mark() {
			return index();
		}

		@Override
		public int index() {
			return p;
		}

		@Override
		public void release(int marker) {
			seek(marker);
		}

		@Override
		public void seek(int index) {
			p = index;
		}

		@Override
		public int size() {
			return types.size();
		}

		@Override
		public String getSourceName() {
			return null;
		}

		@Override
		public Token LT(int i) {
			CommonToken t;
			int rawIndex = p + i - 1;
			if (rawIndex >= types.size())
				t = new CommonToken(Token.EOF);
			else
				t = new CommonToken(types.get(rawIndex));
			t.setTokenIndex(rawIndex);
			return t;
		}

		@Override
		public Token get(int i) {
			return new org.antlr.v4.runtime.CommonToken(types.get(i));
		}

		@Override
		public TokenSource getTokenSource() {
			return null;
		}

		@Override
		public String getText() {
			throw new UnsupportedOperationException("can't give strings");
		}

		@Override
		public String getText(Interval interval) {
			throw new UnsupportedOperationException("can't give strings");
		}

		@Override
		public String getText(RuleContext ctx) {
			throw new UnsupportedOperationException("can't give strings");
		}

		@Override
		public String getText(Token start, Token stop) {
			throw new UnsupportedOperationException("can't give strings");
		}
	}

	/** Sort a list */
	public <T extends Comparable<? super T>> List<T> sort(List<T> data) {
		List<T> dup = new ArrayList<T>();
		dup.addAll(data);
		Collections.sort(dup);
		return dup;
	}

	/** Return map sorted by key */
	public <K extends Comparable<? super K>, V> LinkedHashMap<K, V> sort(
			Map<K, V> data) {
		LinkedHashMap<K, V> dup = new LinkedHashMap<K, V>();
		List<K> keys = new ArrayList<K>();
		keys.addAll(data.keySet());
		Collections.sort(keys);
		for (K k : keys) {
			dup.put(k, data.get(k));
		}
		return dup;
	}
}
