/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.test.runtime.javascript.safari;

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
import org.antlr.v4.test.runtime.java.ErrorQueue;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.DOTGenerator;
import org.antlr.v4.tool.DefaultToolListener;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarSemanticsMessage;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.WebDriver;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class BaseTest {
	// -J-Dorg.antlr.v4.test.BaseTest.level=FINE
	// private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());

	public static final String newline = System.getProperty("line.separator");
	public static final String pathSep = System.getProperty("path.separator");

	public String httpdir = null;
	public String tmpdir = null;

	/** If error during parser execution, store stderr here; can't return
     *  stdout and stderr.  This doesn't trap errors from running antlr.
     */
	protected String stderrDuringParse;

	@org.junit.Rule
	public final TestRule testWatcher = new TestWatcher() {

		@Override
		protected void succeeded(Description description) {
			// remove tmpdir if no error.
			eraseTempDir();
		}

	};

	static WebDriver driver;

	@BeforeClass
	public static void initWebDriver() {
		driver = SharedWebDriver.init();
	}

	@AfterClass
	public static void closeWebDriver() {
		SharedWebDriver.close();
	}

    @Before
	public void setUp() throws Exception {
        // new output dir for each test
    	String prop = System.getProperty("antlr-javascript-test-dir");
    	if(prop!=null && prop.length()>0)
    		httpdir = prop;
    	else
    		httpdir = new File(System.getProperty("java.io.tmpdir"), getClass().getSimpleName()+"-"+System.currentTimeMillis()).getAbsolutePath();
    	File dir = new File(httpdir);
    	if(dir.exists())
    		this.eraseFiles(dir);
    	tmpdir = new File(httpdir, "parser").getAbsolutePath();
    }

    protected org.antlr.v4.Tool newTool(String[] args) {
		Tool tool = new Tool(args);
		return tool;
	}

	protected Tool newTool() {
		org.antlr.v4.Tool tool = new Tool(new String[] {"-o", tmpdir});
		return tool;
	}

	protected ATN createATN(Grammar g, boolean useSerializer) {
		if ( g.atn==null ) {
			semanticProcess(g);
			assertEquals(0, g.tool.getNumErrors());

			ParserATNFactory f;
			if ( g.isLexer() ) {
				f = new LexerATNFactory((LexerGrammar)g);
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
		if ( g.ast!=null && !g.ast.hasErrors ) {
			System.out.println(g.ast.toStringTree());
			Tool antlr = new Tool();
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();
			if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
				for (Grammar imp : g.getImportedGrammars()) {
					antlr.processNonCombinedGrammar(imp, false);
				}
			}
		}
	}


	IntegerList getTypesFromString(Grammar g, String expecting) {
		IntegerList expectingTokenTypes = new IntegerList();
		if ( expecting!=null && !expecting.trim().isEmpty() ) {
			for (String tname : expecting.replace(" ", "").split(",")) {
				int ttype = g.getTokenType(tname);
				expectingTokenTypes.add(ttype);
			}
		}
		return expectingTokenTypes;
	}

	public IntegerList getTokenTypesViaATN(String input, LexerATNSimulator lexerATN) {
		ANTLRInputStream in = new ANTLRInputStream(input);
		IntegerList tokenTypes = new IntegerList();
		int ttype;
		do {
			ttype = lexerATN.match(in, Lexer.DEFAULT_MODE);
			tokenTypes.add(ttype);
		} while ( ttype!= Token.EOF );
		return tokenTypes;
	}

	public List<String> getTokenTypes(LexerGrammar lg,
									  ATN atn,
									  CharStream input)
	{
		LexerATNSimulator interp = new LexerATNSimulator(atn,new DFA[] { new DFA(atn.modeToStartState.get(Lexer.DEFAULT_MODE)) },null);
		List<String> tokenTypes = new ArrayList<String>();
		int ttype;
		boolean hitEOF = false;
		do {
			if ( hitEOF ) {
				tokenTypes.add("EOF");
				break;
			}
			int t = input.LA(1);
			ttype = interp.match(input, Lexer.DEFAULT_MODE);
			if ( ttype == Token.EOF ) {
				tokenTypes.add("EOF");
			}
			else {
				tokenTypes.add(lg.typeToTokenList.get(ttype));
			}

			if ( t==IntStream.EOF ) {
				hitEOF = true;
			}
		} while ( ttype!=Token.EOF );
		return tokenTypes;
	}


	/** Return true if all is ok, no errors */
	protected ErrorQueue antlr(String fileName, String grammarFileName, String grammarStr, boolean defaultListener, String... extraOptions) {
		System.out.println("dir "+tmpdir);
		mkdir(tmpdir);
		writeFile(tmpdir, fileName, grammarStr);
		final List<String> options = new ArrayList<String>();
		Collections.addAll(options, extraOptions);
		options.add("-Dlanguage=JavaScript");
		options.add("-o");
		options.add(tmpdir);
		options.add("-lib");
		options.add(tmpdir);
		options.add(new File(tmpdir,grammarFileName).toString());

		final String[] optionsA = new String[options.size()];
		options.toArray(optionsA);
		Tool antlr = newTool(optionsA);
		ErrorQueue equeue = new ErrorQueue(antlr);
		antlr.addListener(equeue);
		if (defaultListener) {
			antlr.addListener(new DefaultToolListener(antlr));
		}
		antlr.processGrammarsOnCommandLine();

		if ( !defaultListener && !equeue.errors.isEmpty() ) {
			System.err.println("antlr reports errors from "+options);
			for (int i = 0; i < equeue.errors.size(); i++) {
				ANTLRMessage msg = equeue.errors.get(i);
				System.err.println(msg);
			}
			System.out.println("!!!\ngrammar:");
			System.out.println(grammarStr);
			System.out.println("###");
		}
		if ( !defaultListener && !equeue.warnings.isEmpty() ) {
			System.err.println("antlr reports warnings from "+options);
			for (int i = 0; i < equeue.warnings.size(); i++) {
				ANTLRMessage msg = equeue.warnings.get(i);
				System.err.println(msg);
			}
		}

		return equeue;
	}

	protected String execLexer(String grammarFileName,
							   String grammarStr,
							   String lexerName,
							   String input, boolean showDFA) throws Exception
	{
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
									  grammarStr,
									  null,
									  lexerName,"-no-listener");
		assertTrue(success);
		writeLexerTestFile(lexerName, showDFA);
		String output = execHtmlPage("Test.html", input);
		if ( stderrDuringParse!=null && stderrDuringParse.length()>0 ) {
			System.err.println(stderrDuringParse);
		}
		return output;
	}

	protected String execParser(String grammarFileName,
								String grammarStr,
								String parserName,
								String lexerName,
								String listenerName,
								String visitorName,
								String startRuleName,
								String input,
								boolean debug) throws Exception
	{
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
														grammarStr,
														parserName,
														lexerName,
														"-visitor");
		assertTrue(success);
		rawBuildRecognizerTestFile(parserName,
								 lexerName,
								 listenerName,
								 visitorName,
								 startRuleName,
								 debug);
		return execRecognizer(input);
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													String parserName,
													String lexerName,
													String... extraOptions)
	{
		return rawGenerateAndBuildRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													String parserName,
													String lexerName,
													boolean defaultListener,
													String... extraOptions)
	{
		ErrorQueue equeue =
			antlr(grammarFileName, grammarFileName, grammarStr, defaultListener, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}

		List<String> files = new ArrayList<String>();
		if ( lexerName!=null ) {
			files.add(lexerName+".js");
		}
		if ( parserName!=null ) {
			files.add(parserName+".js");
			Set<String> optionsSet = new HashSet<String>(Arrays.asList(extraOptions));
			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.'))+"Listener.js");
			}
			if (optionsSet.contains("-visitor")) {
				files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.'))+"Visitor.js");
			}
		}
		return true; // allIsWell: no compile
	}

	protected void rawBuildRecognizerTestFile(String parserName,
									   String lexerName,
									   String listenerName,
									   String visitorName,
									   String parserStartRuleName, boolean debug)
	{
        this.stderrDuringParse = null;
		if ( parserName==null ) {
			writeLexerTestFile(lexerName, false);
		}
		else {
			writeParserTestFile(parserName,
						  lexerName,
						  listenerName,
						  visitorName,
						  parserStartRuleName,
						  debug);
		}
	}

	public String execRecognizer(String input) throws Exception {
		return execHtmlPage("Test.html", input);
	}

	static int httpPort = 8080;

	class ServerThread extends Thread {

		Server server;
		String runtimePath;
		String fileName;
		Exception ex;

		public ServerThread(String fileName) {
			this.runtimePath = locateRuntime();
			this.fileName = fileName;
		}

		@Override
		public void run() {
			try {
				Server server = new Server(httpPort);
				ResourceHandler rh1 = new ResourceHandler();
				rh1.setDirectoriesListed(false);
				rh1.setResourceBase(httpdir);
				rh1.setWelcomeFiles(new String[] { fileName });
				ResourceHandler rh2 = new ResourceHandler();
				rh2.setDirectoriesListed(false);
				rh2.setResourceBase(runtimePath);
				HandlerList handlers = new HandlerList();
				handlers.setHandlers(new Handler[] { rh1, rh2, new DefaultHandler() });
				server.setHandler(handlers);
				server.start();
				this.server = server;
				this.server.join();
			} catch(BindException e) {
				httpPort++;
				run();
			} catch (Exception e) {
				ex = e;
			}
		}
	}

	public String execHtmlPage(String fileName, String input) throws Exception {
		// 'file' protocol is not supported by Selenium Safari driver
		// so we run an embedded Jetty server
		ServerThread thread = new ServerThread(fileName);
		thread.start();
		try {
			while(thread.server==null && thread.ex==null)
				Thread.sleep(10);
			if(thread.ex!=null)
				throw thread.ex;
			while(thread.server.isStarting())
				Thread.sleep(10);
			Thread.sleep(400); // despite all the above precautions, driver.get often fails if you don't give time to Jetty
			driver.get("http://localhost:" + httpPort + "/" + fileName);
			driver.findElement(new ById("input")).clear();
			driver.findElement(new ById("output")).clear();
			driver.findElement(new ById("errors")).clear();
			driver.navigate().refresh();
			driver.findElement(new ById("input")).sendKeys(input);
			driver.findElement(new ById("load")).click();
			driver.findElement(new ById("submit")).click();
			String errors = driver.findElement(new ById("errors")).getAttribute("value");
			if(errors!=null && errors.length()>0) {
				this.stderrDuringParse = errors;
				System.err.print(errors);
			}
			String value = driver.findElement(new ById("output")).getAttribute("value");
			// mimic stdout which adds a NL
			if(value.length()>0 && !value.endsWith("\n"))
				value = value + "\n";
			return value;
		}
		catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		} finally {
			if(thread.server!=null) {
				thread.server.stop();
				while(!thread.server.isStopped())
					Thread.sleep(10);
				Thread.sleep(100); // ensure the port is freed
			}
		}
		return null;
	}

	private String locateRuntime() {
		String propName = "antlr-javascript-runtime";
		String prop = System.getProperty(propName);
		if(prop==null || prop.length()==0)
			prop = "../../antlr4-javascript/src";
		File file = new File(prop);
		if(!file.exists())
			throw new RuntimeException("Missing system property:" + propName);
		return file.getAbsolutePath();
	}

	public void testErrors(String[] pairs, boolean printTree) {
        for (int i = 0; i < pairs.length; i+=2) {
            String input = pairs[i];
            String expect = pairs[i+1];

			String[] lines = input.split("\n");
			String fileName = getFilenameFromFirstLineOfGrammar(lines[0]);
			ErrorQueue equeue = antlr(fileName, fileName, input, false);

			String actual = equeue.toString(true);
			actual = actual.replace(tmpdir + File.separator, "");
			System.err.println(actual);
			String msg = input;
			msg = msg.replace("\n","\\n");
			msg = msg.replace("\r","\\r");
			msg = msg.replace("\t","\\t");

            assertEquals("error in: "+msg,expect,actual);
        }
    }

	public String getFilenameFromFirstLineOfGrammar(String line) {
		String fileName = "A" + Tool.GRAMMAR_EXTENSION;
		int grIndex = line.lastIndexOf("grammar");
		int semi = line.lastIndexOf(';');
		if ( grIndex>=0 && semi>=0 ) {
			int space = line.indexOf(' ', grIndex);
			fileName = line.substring(space+1, semi)+Tool.GRAMMAR_EXTENSION;
		}
		if ( fileName.length()==Tool.GRAMMAR_EXTENSION.length() ) fileName = "A" + Tool.GRAMMAR_EXTENSION;
		return fileName;
	}

//	void ambig(List<Message> msgs, int[] expectedAmbigAlts, String expectedAmbigInput)
//		throws Exception
//	{
//		ambig(msgs, 0, expectedAmbigAlts, expectedAmbigInput);
//	}

//	void ambig(List<Message> msgs, int i, int[] expectedAmbigAlts, String expectedAmbigInput)
//		throws Exception
//	{
//		List<Message> amsgs = getMessagesOfType(msgs, AmbiguityMessage.class);
//		AmbiguityMessage a = (AmbiguityMessage)amsgs.get(i);
//		if ( a==null ) assertNull(expectedAmbigAlts);
//		else {
//			assertEquals(a.conflictingAlts.toString(), Arrays.toString(expectedAmbigAlts));
//		}
//		assertEquals(expectedAmbigInput, a.input);
//	}

//	void unreachable(List<Message> msgs, int[] expectedUnreachableAlts)
//		throws Exception
//	{
//		unreachable(msgs, 0, expectedUnreachableAlts);
//	}

//	void unreachable(List<Message> msgs, int i, int[] expectedUnreachableAlts)
//		throws Exception
//	{
//		List<Message> amsgs = getMessagesOfType(msgs, UnreachableAltsMessage.class);
//		UnreachableAltsMessage u = (UnreachableAltsMessage)amsgs.get(i);
//		if ( u==null ) assertNull(expectedUnreachableAlts);
//		else {
//			assertEquals(u.conflictingAlts.toString(), Arrays.toString(expectedUnreachableAlts));
//		}
//	}

	List<ANTLRMessage> getMessagesOfType(List<ANTLRMessage> msgs, Class<? extends ANTLRMessage> c) {
		List<ANTLRMessage> filtered = new ArrayList<ANTLRMessage>();
		for (ANTLRMessage m : msgs) {
			if ( m.getClass() == c ) filtered.add(m);
		}
		return filtered;
	}

	void checkRuleATN(Grammar g, String ruleName, String expecting) {
		ParserATNFactory f = new ParserATNFactory(g);
		ATN atn = f.createATN();

		DOTGenerator dot = new DOTGenerator(g);
		System.out.println(dot.getDOT(atn.ruleToStartState[g.getRule(ruleName).index]));

		Rule r = g.getRule(ruleName);
		ATNState startState = atn.ruleToStartState[r.index];
		ATNPrinter serializer = new ATNPrinter(g, startState);
		String result = serializer.asString();

		//System.out.print(result);
		assertEquals(expecting, result);
	}

	public void testActions(String templates, String actionName, String action, String expected) throws org.antlr.runtime.RecognitionException {
		int lp = templates.indexOf('(');
		String name = templates.substring(0, lp);
		STGroup group = new STGroupString(templates);
		ST st = group.getInstanceOf(name);
		st.add(actionName, action);
		String grammar = st.render();
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(grammar, equeue);
		if ( g.ast!=null && !g.ast.hasErrors ) {
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();

			ATNFactory factory = new ParserATNFactory(g);
			if ( g.isLexer() ) factory = new LexerATNFactory((LexerGrammar)g);
			g.atn = factory.createATN();

			CodeGenerator gen = new CodeGenerator(g);
			ST outputFileST = gen.generateParser();
			String output = outputFileST.render();
			//System.out.println(output);
			String b = "#" + actionName + "#";
			int start = output.indexOf(b);
			String e = "#end-" + actionName + "#";
			int end = output.indexOf(e);
			String snippet = output.substring(start+b.length(),end);
			assertEquals(expected, snippet);
		}
		if ( equeue.size()>0 ) {
			System.err.println(equeue.toString());
		}
	}

	public static class StreamVacuum implements Runnable {
		StringBuilder buf = new StringBuilder();
		BufferedReader in;
		Thread sucker;
		public StreamVacuum(InputStream in) {
			this.in = new BufferedReader( new InputStreamReader(in) );
		}
		public void start() {
			sucker = new Thread(this);
			sucker.start();
		}
		@Override
		public void run() {
			try {
				String line = in.readLine();
				while (line!=null) {
					buf.append(line);
					buf.append('\n');
					line = in.readLine();
				}
			}
			catch (IOException ioe) {
				System.err.println("can't read output from process");
			}
		}
		/** wait for the thread to finish */
		public void join() throws InterruptedException {
			sucker.join();
		}
		@Override
		public String toString() {
			return buf.toString();
		}
	}

	protected void checkGrammarSemanticsError(ErrorQueue equeue,
											  GrammarSemanticsMessage expectedMessage)
		throws Exception
	{
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.errors.size(); i++) {
			ANTLRMessage m = equeue.errors.get(i);
			if (m.getErrorType()==expectedMessage.getErrorType() ) {
				foundMsg = m;
			}
		}
		assertNotNull("no error; "+expectedMessage.getErrorType()+" expected", foundMsg);
		assertTrue("error is not a GrammarSemanticsMessage",
				   foundMsg instanceof GrammarSemanticsMessage);
		assertEquals(Arrays.toString(expectedMessage.getArgs()), Arrays.toString(foundMsg.getArgs()));
		if ( equeue.size()!=1 ) {
			System.err.println(equeue);
		}
	}

	protected void checkGrammarSemanticsWarning(ErrorQueue equeue,
											    GrammarSemanticsMessage expectedMessage)
		throws Exception
	{
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.warnings.size(); i++) {
			ANTLRMessage m = equeue.warnings.get(i);
			if (m.getErrorType()==expectedMessage.getErrorType() ) {
				foundMsg = m;
			}
		}
		assertNotNull("no error; "+expectedMessage.getErrorType()+" expected", foundMsg);
		assertTrue("error is not a GrammarSemanticsMessage",
				   foundMsg instanceof GrammarSemanticsMessage);
		assertEquals(Arrays.toString(expectedMessage.getArgs()), Arrays.toString(foundMsg.getArgs()));
		if ( equeue.size()!=1 ) {
			System.err.println(equeue);
		}
	}

	protected void checkError(ErrorQueue equeue,
							  ANTLRMessage expectedMessage)
		throws Exception
	{
		//System.out.println("errors="+equeue);
		ANTLRMessage foundMsg = null;
		for (int i = 0; i < equeue.errors.size(); i++) {
			ANTLRMessage m = equeue.errors.get(i);
			if (m.getErrorType()==expectedMessage.getErrorType() ) {
				foundMsg = m;
			}
		}
		assertTrue("no error; "+expectedMessage.getErrorType()+" expected", !equeue.errors.isEmpty());
		assertTrue("too many errors; "+equeue.errors, equeue.errors.size()<=1);
		assertNotNull("couldn't find expected error: "+expectedMessage.getErrorType(), foundMsg);
		/*
		assertTrue("error is not a GrammarSemanticsMessage",
				   foundMsg instanceof GrammarSemanticsMessage);
		 */
		assertArrayEquals(expectedMessage.getArgs(), foundMsg.getArgs());
	}

    public static class FilteringTokenStream extends CommonTokenStream {
        public FilteringTokenStream(TokenSource src) { super(src); }
        Set<Integer> hide = new HashSet<Integer>();
        @Override
        protected boolean sync(int i) {
            if (!super.sync(i)) {
				return false;
			}

			Token t = get(i);
			if ( hide.contains(t.getType()) ) {
				((WritableToken)t).setChannel(Token.HIDDEN_CHANNEL);
			}

			return true;
        }
        public void setTokenTypeChannel(int ttype, int channel) {
            hide.add(ttype);
        }
    }

	public static void writeFile(String dir, String fileName, String content) {
		try {
			File f = new File(dir, fileName);
			FileWriter w = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(w);
			bw.write(content);
			bw.close();
			w.close();
		}
		catch (IOException ioe) {
			System.err.println("can't write file");
			ioe.printStackTrace(System.err);
		}
	}

	protected void mkdir(String dir) {
		File f = new File(dir);
		f.mkdirs();
	}

	protected void writeParserTestFile(String parserName,
			 String lexerName,
			 String listenerName,
			 String visitorName,
			 String parserStartRuleName, boolean debug) {
		String html = "<!DOCTYPE html>\r\n" +
		"<html>\r\n" +
	    "	<head>\r\n" +
        "		<script src='lib/require.js'></script>\r\n" +
        "		<script>\r\n" +
        "			antlr4 = null;\r\n" +
        "			listener = null;\r\n" +
        "			TreeShapeListener = null;\r\n" +
        "			" + lexerName + " = null;\r\n" +
        "			" + parserName + " = null;\r\n" +
        "			" + listenerName + " = null;\r\n" +
        "			" + visitorName + " = null;\r\n" +
        "			printer = function() {\r\n" +
        "				this.println = function(s) { document.getElementById('output').value += s + '\\n'; }\r\n" +
        "				this.print = function(s) { document.getElementById('output').value += s; }\r\n" +
        "				return this;\r\n" +
        "			};\r\n" +
		"\r\n" +
		"			loadParser = function() {\r\n" +
        "				try {\r\n" +
        "					antlr4 = require('antlr4/index');\r\n" +
		"					" + lexerName + " = require('./parser/" + lexerName + "');\n" +
		"					" + parserName + " = require('./parser/" + parserName + "');\n" +
		"					" + listenerName + " = require('./parser/" + listenerName + "');\n" +
		"					" + visitorName + " = require('./parser/" + visitorName + "');\n" +
        "				} catch (ex) {\r\n" +
        "					document.getElementById('errors').value = ex.toString();\r\n" +
        "				}\r\n" +
		"\r\n" +
		"				listener = function() {\r\n" +
		"					antlr4.error.ErrorListener.call(this);\r\n" +
		"					return this;\r\n" +
		"				}\r\n" +
		"				listener.prototype = Object.create(antlr4.error.ErrorListener.prototype);\r\n" +
		"				listener.prototype.constructor = listener;\r\n" +
		"				listener.prototype.syntaxError = function(recognizer, offendingSymbol, line, column, msg, e) {\r\n" +
		"    				document.getElementById('errors').value += 'line ' + line + ':' + column + ' ' + msg + '\\r\\n';\r\n" +
		"				};\r\n" +
		"\r\n" +
        "				TreeShapeListener = function() {\r\n" +
        "					antlr4.tree.ParseTreeListener.call(this);\r\n" +
        "					return this;\r\n" +
        "				};\r\n" +
		"\r\n" +
		"				TreeShapeListener.prototype = Object.create(antlr4.tree.ParseTreeListener.prototype);\r\n" +
		"				TreeShapeListener.prototype.constructor = TreeShapeListener;\r\n" +
		"\r\n" +
		"				TreeShapeListener.prototype.enterEveryRule = function(ctx) {\r\n" +
		"					for(var i=0;i<ctx.getChildCount; i++) {\r\n" +
		"						var child = ctx.getChild(i);\r\n" +
		"						var parent = child.parentCtx;\r\n" +
		"						if(parent.getRuleContext() !== ctx || !(parent instanceof antlr4.tree.RuleNode)) {\r\n" +
		"							throw 'Invalid parse tree shape detected.';\r\n" +
		"						}\r\n" +
		"					}\r\n" +
		"				};\r\n" +
		"			}\r\n" +
		"\r\n" +
		"			test = function() {\r\n" +
		"				document.getElementById('output').value = ''\r\n" +
		"				var input = document.getElementById('input').value;\r\n" +
		"    			var stream = new antlr4.InputStream(input);\n" +
		"    			var lexer = new " + lexerName + "." + lexerName + "(stream);\n" +
		"				lexer._listeners = [new listener()];\r\n" +
	    "    			var tokens = new antlr4.CommonTokenStream(lexer);\n" +
	    "				var parser = new " + parserName + "." + parserName + "(tokens);\n" +
	    "				parser._listeners.push(new listener());\n" +
	    (debug ?
	    "				parser._listeners.push(new antlr4.error.DiagnosticErrorListener());\n" : "") +
	    "    			parser.buildParseTrees = true;\n" +
	    "    			parser.printer = new printer();\n" +
		"    			var tree = parser." + parserStartRuleName + "();\n" +
		"    			antlr4.tree.ParseTreeWalker.DEFAULT.walk(new TreeShapeListener(), tree);\n" +
		"			};\r\n" +
		"\r\n" +
        "		</script>\r\n" +
	    "	</head>\r\n" +
	    "	<body>\r\n" +
	    "		<textarea id='input'></textarea><br>\r\n" +
	    "		<button id='load' type='button' onclick='loadParser()'>Load</button><br>\r\n" +
	    "		<button id='submit' type='button' onclick='test()'>Test</button><br>\r\n" +
	    "		<textarea id='output'></textarea><br>\r\n" +
	    "		<textarea id='errors'></textarea><br>\r\n" +
	    "	</body>\r\n" +
	    "</html>\r\n";
		writeFile(httpdir, "Test.html", html);
	};


	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		String html = "<!DOCTYPE html>\r\n" +
		"<html>\r\n" +
	    "	<head>\r\n" +
        "		<script src='lib/require.js'></script>\r\n" +
        "		<script>\r\n" +
        "			antlr4 = null;\r\n" +
        "			listener = null;\r\n" +
        "			" + lexerName + " = null;\r\n" +
 		"\r\n" +
		"			loadLexer = function() {\r\n" +
        "				try {\r\n" +
        "					antlr4 = require('antlr4/index');\r\n" +
		"					" + lexerName + " = require('./parser/" + lexerName + "');\r\n" +
        "				} catch (ex) {\r\n" +
        "					document.getElementById('errors').value = ex.toString();\r\n" +
        "				}\r\n" +
		"				listener = function() {\r\n" +
		"					antlr4.error.ErrorListener.call(this);\r\n" +
		"					return this;\r\n" +
		"				}\r\n" +
		"				listener.prototype = Object.create(antlr4.error.ErrorListener.prototype);\r\n" +
		"				listener.prototype.constructor = listener;\r\n" +
		"				listener.prototype.syntaxError = function(recognizer, offendingSymbol, line, column, msg, e) {\r\n" +
		"    				document.getElementById('errors').value += 'line ' + line + ':' + column + ' ' + msg + '\\r\\n';\r\n" +
		"				};\r\n" +
		"			}\r\n" +
		"\r\n" +
		"			test = function() {\r\n" +
		"				document.getElementById('output').value = ''\r\n" +
		"				var input = document.getElementById('input').value;\r\n" +
		"    			var chars = new antlr4.InputStream(input);\r\n" +
		"    			var lexer = new " + lexerName + "." + lexerName + "(chars);\r\n" +
		"				lexer._listeners = [new listener()];\r\n" +
	    "    			var stream = new antlr4.CommonTokenStream(lexer);\r\n" +
		"    			stream.fill();\r\n" +
	    "    			for(var i=0; i<stream.tokens.length; i++) {\r\n" +
	    "					document.getElementById('output').value += stream.tokens[i].toString() + '\\r\\n';\r\n" +
		"    			}\n" +
		(showDFA ?
		"    			document.getElementById('output').value += lexer._interp.decisionToDFA[antlr4.Lexer.DEFAULT_MODE].toLexerString();\r\n"
						:"") +
		"			};\r\n" +
		"\r\n" +
        "		</script>\r\n" +
	    "	</head>\r\n" +
	    "	<body>\r\n" +
	    "		<textarea id='input'></textarea><br>\r\n" +
	    "		<button id='load' type='button' onclick='loadLexer()'>Load</button><br>\r\n" +
	    "		<button id='submit' type='button' onclick='test()'>Test</button><br>\r\n" +
	    "		<textarea id='output'></textarea><br>\r\n" +
	    "		<textarea id='errors'></textarea><br>\r\n" +
	    "	</body>\r\n" +
	    "</html>\r\n";
		writeFile(httpdir, "Test.html", html);
	}

	public void writeRecognizer(String parserName, String lexerName,
								String listenerName, String visitorName,
								String parserStartRuleName, boolean debug) {
		if ( parserName==null )
			writeLexerTestFile(lexerName, debug);
		else
			writeParserTestFile(parserName,
						  lexerName,
						  listenerName,
						  visitorName,
						  parserStartRuleName,
						  debug);
	}


    protected void eraseFiles(final String filesEndingWith) {
        File tmpdirF = new File(httpdir);
        String[] files = tmpdirF.list();
        for(int i = 0; files!=null && i < files.length; i++) {
            if ( files[i].endsWith(filesEndingWith) ) {
                new File(httpdir+"/"+files[i]).delete();
            }
        }
    }

    protected void eraseFiles(File dir) {
       String[] files = dir.list();
        for(int i = 0; files!=null && i < files.length; i++) {
            new File(dir,files[i]).delete();
        }
    }

    protected void eraseTempDir() {
       	boolean doErase = true;
    	String propName = "antlr-javascript-erase-test-dir";
    	String prop = System.getProperty(propName);
    	if(prop!=null && prop.length()>0)
    		doErase = Boolean.getBoolean(prop);
        if(doErase) {
	        File tmpdirF = new File(httpdir);
	        if ( tmpdirF.exists() ) {
	            eraseFiles(tmpdirF);
	            tmpdirF.delete();
	        }
        }
    }

   public String getFirstLineOfException() {
		if ( this.stderrDuringParse ==null ) {
			return null;
		}
		String[] lines = this.stderrDuringParse.split("\n");
		String prefix="Exception in thread \"main\" ";
		return lines[0].substring(prefix.length(),lines[0].length());
	}

    /**
     * When looking at a result set that consists of a Map/HashTable
     * we cannot rely on the output order, as the hashing algorithm or other aspects
     * of the implementation may be different on differnt JDKs or platforms. Hence
     * we take the Map, convert the keys to a List, sort them and Stringify the Map, which is a
     * bit of a hack, but guarantees that we get the same order on all systems. We assume that
     * the keys are strings.
     *
     * @param m The Map that contains keys we wish to return in sorted order
     * @return A string that represents all the keys in sorted order.
     */
    public <K, V> String sortMapToString(Map<K, V> m) {
        // Pass in crap, and get nothing back
        //
        if  (m == null) {
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
		int p=0;
		public IntTokenStream(IntegerList types) { this.types = types; }

		@Override
		public void consume() { p++; }

		@Override
		public int LA(int i) { return LT(i).getType(); }

		@Override
		public int mark() {
			return index();
		}

		@Override
		public int index() { return p; }

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
			if ( rawIndex>=types.size() ) t = new CommonToken(Token.EOF);
			else t = new CommonToken(types.get(rawIndex));
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
	public <K extends Comparable<? super K>,V> LinkedHashMap<K,V> sort(Map<K,V> data) {
		LinkedHashMap<K,V> dup = new LinkedHashMap<K, V>();
		List<K> keys = new ArrayList<K>();
		keys.addAll(data.keySet());
		Collections.sort(keys);
		for (K k : keys) {
			dup.put(k, data.get(k));
		}
		return dup;
	}
}
