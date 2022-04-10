/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.DefaultToolListener;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static junit.framework.TestCase.fail;
import static junit.framework.TestCase.failNotEquals;
import static org.junit.Assume.assumeFalse;

/** This class represents a single runtime test. It pulls data from
 *  a {@link RuntimeTestDescriptor} and uses junit to trigger a test.
 *  The only functionality needed to execute a test is defined in
 *  {@link RuntimeTestSupport}. All of the various test rig classes
 *  derived from this one. E.g., see {@link org.antlr.v4.test.runtime.java.TestParserExec}.
 *
 *  @since 4.6.
 */
public abstract class BaseRuntimeTest {
	final static Set<String> sections = new HashSet<>(Arrays.asList(
		"notes", "type", "grammar", "slaveGrammar", "start", "input", "output", "errors", "flags", "skip"
	));

	@BeforeClass
	public static void startHeartbeatToAvoidTimeout() {
		if(requiresHeartbeat()) {
			startHeartbeat();
		}
	}

	private static boolean requiresHeartbeat() {
		return isTravisCI()
				|| isAppVeyorCI()
				|| (isCPP() && isRecursion())
				|| (isCircleCI() && isGo())
				|| (isCircleCI() && isDotNet() && isRecursion());
	}

	@AfterClass
	public static void stopHeartbeat() {
		heartbeat = false;
	}

	private static boolean isRecursion() {
		String s = System.getenv("GROUP");
		return "recursion".equalsIgnoreCase(s);
	}

	private static boolean isGo() {
		String s = System.getenv("TARGET");
		return "go".equalsIgnoreCase(s);
	}

	private static boolean isCPP() {
		String s = System.getenv("TARGET");
		return "cpp".equalsIgnoreCase(s);
	}

	private static boolean isDotNet() {
		String s = System.getenv("TARGET");
		return "dotnet".equalsIgnoreCase(s);
	}

	private static boolean isCircleCI() {
		// see https://circleci.com/docs/2.0/env-vars/#built-in-environment-variables
		String s = System.getenv("CIRCLECI");
		return "true".equalsIgnoreCase(s);
	}

	private static boolean isAppVeyorCI() {
		// see https://www.appveyor.com/docs/environment-variables/
		String s = System.getenv("APPVEYOR");
		return "true".equalsIgnoreCase(s);
	}

	private static boolean isTravisCI() {
		// see https://docs.travis-ci.com/user/environment-variables/#default-environment-variables
		String s = System.getenv("TRAVIS");
		return "true".equalsIgnoreCase(s);
	}

	static boolean heartbeat = false;

	private static void startHeartbeat() {
		// Add heartbeat thread to gen minimal output for travis, appveyor to avoid timeout.
		Thread t = new Thread("heartbeat") {
			@Override
			public void run() {
				heartbeat = true;
				while (heartbeat) {
					try {
						//noinspection BusyWait
						Thread.sleep(10000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.print('.');
				}
			}
		};
		t.start();
	}

	/** ANTLR isn't thread-safe to process grammars so we use a global lock for testing */
	public static final Object antlrLock = new Object();

	protected RuntimeTestSupport delegate;
	protected RuntimeTestDescriptor descriptor;

	public BaseRuntimeTest(RuntimeTestDescriptor descriptor, RuntimeTestSupport delegate) {
		this.descriptor = descriptor;
		this.delegate = delegate;
	}

	@Before
	public void setUp() throws Exception {
		// From http://junit.sourceforge.net/javadoc/org/junit/Assume.html
		// "The default JUnit runner treats tests with failing assumptions as ignored"
		assumeFalse(checkIgnored());
		delegate.testSetUp();
	}

	public boolean checkIgnored() {
		boolean ignored = !TestContext.isSupportedTarget(descriptor.getTarget()) || descriptor.ignore(descriptor.getTarget());
		if (ignored) {
			System.out.println("Ignore " + descriptor);
		}
		return ignored;
	}

	@Rule
	public final TestRule testWatcher = new TestWatcher() {
		@Override
		protected void succeeded(Description description) {
			// remove tmpdir if no error.
			delegate.eraseTempDir();
		}
	};

	@Test
	public void testOne() throws Exception {
		// System.out.println(descriptor.getTestName());
		// System.out.println(delegate.getTmpDir());
		if (descriptor.ignore(descriptor.getTarget()) ) {
			System.out.println("Ignore " + descriptor);
			return;
		}
		delegate.beforeTest(descriptor);
		if (descriptor.getTestType().contains("Parser") ) {
			testParser(descriptor);
		}
		else {
			testLexer(descriptor);
		}
		delegate.afterTest(descriptor);
	}

	public void testParser(RuntimeTestDescriptor descriptor) {
		RuntimeTestUtils.mkdir(delegate.getTempParserDirPath());

		Pair<String, String> pair = descriptor.getGrammar();

		ClassLoader cloader = getClass().getClassLoader();
		URL templates = cloader.getResource("org/antlr/v4/test/runtime/templates/"+descriptor.getTarget()+".test.stg");
		STGroupFile targetTemplates = new STGroupFile(templates, "UTF-8", '<', '>');
		targetTemplates.registerRenderer(String.class, new StringRenderer());

		// write out any slave grammars
		List<Pair<String, String>> slaveGrammars = descriptor.getSlaveGrammars();
		if ( slaveGrammars!=null ) {
			for (Pair<String, String> spair : slaveGrammars) {
				STGroup g = new STGroup('<', '>');
				g.registerRenderer(String.class, new StringRenderer());
				g.importTemplates(targetTemplates);
				ST grammarST = new ST(g, spair.b);
				writeFile(delegate.getTempParserDirPath(), spair.a+".g4", grammarST.render());
			}
		}

		String grammarName = pair.a;
		String grammar = pair.b;
		STGroup g = new STGroup('<', '>');
		g.importTemplates(targetTemplates);
		g.registerRenderer(String.class, new StringRenderer());
		ST grammarST = new ST(g, grammar);
		grammar = grammarST.render();

		String found = delegate.execParser(grammarName+".g4", grammar,
		                                   grammarName+"Parser",
		                                   grammarName+"Lexer",
		                                   grammarName+"Listener",
		                                   grammarName+"Visitor",
		                                   descriptor.getStartRule(),
		                                   descriptor.getInput(),
		                                   descriptor.showDiagnosticErrors()
		                                  );
		assertCorrectOutput(descriptor, delegate, found);
	}

	public void testLexer(RuntimeTestDescriptor descriptor) throws Exception {
		RuntimeTestUtils.mkdir(delegate.getTempParserDirPath());

		Pair<String, String> pair = descriptor.getGrammar();

		ClassLoader cloader = getClass().getClassLoader();
		URL templates = cloader.getResource("org/antlr/v4/test/runtime/templates/"+descriptor.getTarget()+".test.stg");
		STGroupFile targetTemplates = new STGroupFile(templates, "UTF-8", '<', '>');
		targetTemplates.registerRenderer(String.class, new StringRenderer());

		// write out any slave grammars
		List<Pair<String, String>> slaveGrammars = descriptor.getSlaveGrammars();
		if ( slaveGrammars!=null ) {
			for (Pair<String, String> spair : slaveGrammars) {
				STGroup g = new STGroup('<', '>');
				g.registerRenderer(String.class, new StringRenderer());
				g.importTemplates(targetTemplates);
				ST grammarST = new ST(g, spair.b);
				writeFile(delegate.getTempParserDirPath(), spair.a+".g4", grammarST.render());
			}
		}

		String grammarName = pair.a;
		String grammar = pair.b;
		STGroup g = new STGroup('<', '>');
		g.registerRenderer(String.class, new StringRenderer());
		g.importTemplates(targetTemplates);
		ST grammarST = new ST(g, grammar);
		grammar = grammarST.render();

		String found = delegate.execLexer(grammarName+".g4", grammar, grammarName, descriptor.getInput(), descriptor.showDFA());
		assertCorrectOutput(descriptor, delegate, found);
	}

	/** Write a grammar to tmpdir and run antlr */
	public static ErrorQueue antlrOnString(String workdir,
	                                       String targetName,
	                                       String grammarFileName,
	                                       String grammarStr,
	                                       boolean defaultListener,
	                                       String... extraOptions)
	{
		RuntimeTestUtils.mkdir(workdir);
		writeFile(workdir, grammarFileName, grammarStr);
		return antlrOnString(workdir, targetName, grammarFileName, defaultListener, extraOptions);
	}

	/** Run ANTLR on stuff in workdir and error queue back */
	public static ErrorQueue antlrOnString(String workdir,
	                                       String targetName,
	                                       String grammarFileName,
	                                       boolean defaultListener,
	                                       String... extraOptions)
	{
		final List<String> options = new ArrayList<>();
		Collections.addAll(options, extraOptions);
		if ( targetName!=null ) {
			options.add("-Dlanguage="+targetName);
		}
		if ( !options.contains("-o") ) {
			options.add("-o");
			options.add(workdir);
		}
		if ( !options.contains("-lib") ) {
			options.add("-lib");
			options.add(workdir);
		}
		if ( !options.contains("-encoding") ) {
			options.add("-encoding");
			options.add("UTF-8");
		}
		options.add(new File(workdir,grammarFileName).toString());

		final String[] optionsA = new String[options.size()];
		options.toArray(optionsA);
		Tool antlr = new Tool(optionsA);
		ErrorQueue equeue = new ErrorQueue(antlr);
		antlr.addListener(equeue);
		if (defaultListener) {
			antlr.addListener(new DefaultToolListener(antlr));
		}
		synchronized (antlrLock) {
			antlr.processGrammarsOnCommandLine();
		}

		List<String> errors = new ArrayList<>();

		if ( !defaultListener && !equeue.errors.isEmpty() ) {
			for (int i = 0; i < equeue.errors.size(); i++) {
				ANTLRMessage msg = equeue.errors.get(i);
				ST msgST = antlr.errMgr.getMessageTemplate(msg);
				errors.add(msgST.render());
			}
		}
		if ( !defaultListener && !equeue.warnings.isEmpty() ) {
			for (int i = 0; i < equeue.warnings.size(); i++) {
				ANTLRMessage msg = equeue.warnings.get(i);
				// antlrToolErrors.append(msg); warnings are hushed
			}
		}

		return equeue;
	}

	// ---- support ----

	public static RuntimeTestDescriptor[] getRuntimeTestDescriptors(String group, String targetName) {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		final URL descrURL = loader.getResource("org/antlr/v4/test/runtime/descriptors/" +group);
		String[] descriptorFilenames = null;
		try {
			descriptorFilenames = new File(descrURL.toURI()).list();
		}
		catch (URISyntaxException e) {
			System.err.println("Bad URL:"+descrURL);
		}

//		String[] descriptorFilenames = new File("/tmp/descriptors/"+group).list();
		List<RuntimeTestDescriptor> descriptors = new ArrayList<>();
		for (String fname : descriptorFilenames) {
			try {
//				String dtext = Files.readString(Path.of("/tmp/descriptors",group,fname));
				final URL dURL = loader.getResource("org/antlr/v4/test/runtime/descriptors/" +group+"/"+fname);
				String dtext = null;
				try {
					URI uri = dURL.toURI();
					dtext = new String(Files.readAllBytes(Paths.get(uri)));
				}
				catch (URISyntaxException e) {
					System.err.println("Bad URL:"+dURL);
				}
				UniversalRuntimeTestDescriptor d = readDescriptor(dtext);
				if ( !d.ignore(targetName) ) {
					d.name = fname.replace(".txt", "");
					d.targetName = targetName;
					descriptors.add(d);
				}
			}
			catch (IOException ioe) {
				System.err.println("Can't read descriptor file "+fname);
			}
		}

		if (group.equals("LexerExec")) {
			descriptors.add(GeneratedLexerDescriptors.getLineSeparatorLfDescriptor(targetName));
			descriptors.add(GeneratedLexerDescriptors.getLineSeparatorCrLfDescriptor(targetName));
			descriptors.add(GeneratedLexerDescriptors.getLargeLexerDescriptor(targetName));
			descriptors.add(GeneratedLexerDescriptors.getAtnStatesSizeMoreThan65535Descriptor(targetName));
		}

		return descriptors.toArray(new RuntimeTestDescriptor[0]);
	}

	/**  Read stuff like:
	 [grammar]
	 grammar T;
	 s @after {<DumpDFA()>}
	 : ID | ID {} ;
	 ID : 'a'..'z'+;
	 WS : (' '|'\t'|'\n')+ -> skip ;

	 [grammarName]
	 T

	 [start]
	 s

	 [input]
	 abc

	 [output]
	 Decision 0:
	 s0-ID->:s1^=>1

	 [errors]
	 """line 1:0 reportAttemptingFullContext d=0 (s), input='abc'
	 """

	 Some can be missing like [errors].

	 Get gr names automatically "lexer grammar Unicode;" "grammar T;" "parser grammar S;"

	 Also handle slave grammars:

	 [grammar]
	 grammar M;
	 import S,T;
	 s : a ;
	 B : 'b' ; // defines B from inherited token space
	 WS : (' '|'\n') -> skip ;

	 [slaveGrammar]
	 parser grammar T;
	 a : B {<writeln("\"T.a\"")>};<! hidden by S.a !>

	 [slaveGrammar]
	 parser grammar S;
	 a : b {<writeln("\"S.a\"")>};
	 b : B;
	 */
	public static UniversalRuntimeTestDescriptor readDescriptor(String dtext)
			throws RuntimeException
	{
		String currentField = null;
		StringBuilder currentValue = new StringBuilder();

		List<Pair<String, String>> pairs = new ArrayList<>();
		String[] lines = dtext.split("\r?\n");

		for (String line : lines) {
			boolean newSection = false;
			String sectionName = null;
			if (line.startsWith("[") && line.length() > 2) {
				sectionName = line.substring(1, line.length() - 1);
				newSection = sections.contains(sectionName);
			}

			if (newSection) {
				if (currentField != null) {
					pairs.add(new Pair<>(currentField, currentValue.toString()));
				}
				currentField = sectionName;
				currentValue.setLength(0);
			}
			else {
				currentValue.append(line);
				currentValue.append("\n");
			}
		}
		pairs.add(new Pair<>(currentField, currentValue.toString()));

		UniversalRuntimeTestDescriptor d = new UniversalRuntimeTestDescriptor();
		for (Pair<String,String> p : pairs) {
			String section = p.a;
			String value = "";
			if ( p.b!=null ) {
				value = p.b.trim();
			}
			if ( value.startsWith("\"\"\"") ) {
				value = value.replace("\"\"\"", "");
			}
			else if ( value.indexOf('\n')>=0 ) {
				value = value + "\n"; // if multi line and not quoted, leave \n on end.
			}
			switch (section) {
				case "notes":
					d.notes = value;
					break;
				case "type":
					d.testType = value;
					break;
				case "grammar":
					d.grammarName = getGrammarName(value.split("\n")[0]);
					d.grammar = value;
					break;
				case "slaveGrammar":
					String gname = getGrammarName(value.split("\n")[0]);
					d.slaveGrammars.add(new Pair<>(gname, value));
				case "start":
					d.startRule = value;
					break;
				case "input":
					d.input = value;
					break;
				case "output":
					d.output = value;
					break;
				case "errors":
					d.errors = value;
					break;
				case "flags":
					String[] flags = value.split("\n");
					for (String f : flags) {
						switch (f) {
							case "showDFA":
								d.showDFA = true;
								break;
							case "showDiagnosticErrors":
								d.showDiagnosticErrors = true;
								break;
						}
					}
					break;
				case "skip":
					d.skipTargets = Arrays.asList(value.split("\n"));
					break;
				default:
					throw new RuntimeException("Unknown descriptor section ignored: "+section);
			}
		}
		return d;
	}

	/** Get A, B, or C from:
	 * "lexer grammar A;" "grammar B;" "parser grammar C;"
	 */
	public static String getGrammarName(String grammarDeclLine) {
		int gi = grammarDeclLine.indexOf("grammar ");
		if ( gi<0 ) {
			return "<unknown grammar name>";
		}
		gi += "grammar ".length();
		int gsemi = grammarDeclLine.indexOf(';');
		return grammarDeclLine.substring(gi, gsemi);
	}

	public static void writeFile(String dir, String fileName, String content) {
		try {
			Utils.writeFile(dir+"/"+fileName, content, "UTF-8");
		}
		catch (IOException ioe) {
			System.err.println("can't write file");
			ioe.printStackTrace(System.err);
		}
	}

	public static String readFile(String dir, String fileName) {
		try {
			return String.copyValueOf(Utils.readFile(dir+"/"+fileName, "UTF-8"));
		}
		catch (IOException ioe) {
			System.err.println("can't read file");
			ioe.printStackTrace(System.err);
		}
		return null;
	}

	protected static void assertCorrectOutput(RuntimeTestDescriptor descriptor, RuntimeTestSupport delegate, String actualOutput) {
		String actualParseErrors = delegate.getParseErrors();
		String actualToolErrors = delegate.getANTLRToolErrors();
		String expectedOutput = descriptor.getOutput();
		String expectedParseErrors = descriptor.getErrors();
		String expectedToolErrors = descriptor.getANTLRToolErrors();

		if (actualOutput == null) {
			actualOutput = "";
		}
		if (actualParseErrors == null) {
			actualParseErrors = "";
		}
		if (actualToolErrors == null) {
			actualToolErrors = "";
		}
		if (expectedOutput == null) {
			expectedOutput = "";
		}
		if (expectedParseErrors == null) {
			expectedParseErrors = "";
		}
		if (expectedToolErrors == null) {
			expectedToolErrors = "";
		}

		if (actualOutput.equals(expectedOutput) &&
				actualParseErrors.equals(expectedParseErrors) &&
				actualToolErrors.equals(expectedToolErrors)) {
			return;
		}

		if (actualOutput.equals(expectedOutput)) {
			if (actualParseErrors.equals(expectedParseErrors)) {
				failNotEquals("[" + descriptor.getTarget() + ":" + descriptor.getTestName() + "] " +
								"Parse output and parse errors are as expected, but tool errors are incorrect",
						expectedToolErrors, actualToolErrors);
			}
			else {
				fail("[" + descriptor.getTarget() + ":" + descriptor.getTestName() + "] " +
						"Parse output is as expected, but errors are not: " +
						"expectedParseErrors:<" + expectedParseErrors +
						">; actualParseErrors:<" + actualParseErrors +
						">; expectedToolErrors:<" + expectedToolErrors +
						">; actualToolErrors:<" + actualToolErrors +
						">.");
			}
		}
		else {
			fail("[" + descriptor.getTarget() + ":" + descriptor.getTestName() + "] " +
					"Parse output is incorrect: " +
					"expectedOutput:<" + expectedOutput +
					">; actualOutput:<" + actualOutput +
					">; expectedParseErrors:<" + expectedParseErrors +
					">; actualParseErrors:<" + actualParseErrors +
					">; expectedToolErrors:<" + expectedToolErrors +
					">; actualToolErrors:<" + actualToolErrors +
					">.");
		}
	}

	// ----------------------------------------------------------------------------
	// stuff used during conversion that I don't want to throw away yet and we might lose if
	// I squash this branch unless I keep it around in a comment or something
	// ----------------------------------------------------------------------------

//	public static RuntimeTestDescriptor[] OLD_getRuntimeTestDescriptors(Class<?> clazz, String targetName) {
//		if(!TestContext.isSupportedTarget(targetName))
//			return new RuntimeTestDescriptor[0];
//		Class<?>[] nestedClasses = clazz.getClasses();
//		List<RuntimeTestDescriptor> descriptors = new ArrayList<RuntimeTestDescriptor>();
//		for (Class<?> nestedClass : nestedClasses) {
//			int modifiers = nestedClass.getModifiers();
//			if ( RuntimeTestDescriptor.class.isAssignableFrom(nestedClass) && !Modifier.isAbstract(modifiers) ) {
//				try {
//					RuntimeTestDescriptor d = (RuntimeTestDescriptor) nestedClass.newInstance();
//					if(!d.ignore(targetName)) {
//						d.setTarget(targetName);
//						descriptors.add(d);
//					}
//				} catch (Exception e) {
//					e.printStackTrace(System.err);
//				}
//			}
//		}
//		writeDescriptors(clazz, descriptors);
//		return descriptors.toArray(new RuntimeTestDescriptor[0]);
//	}


	/** Write descriptor files. */
//	private static void writeDescriptors(Class<?> clazz, List<RuntimeTestDescriptor> descriptors) {
//		String descrRootDir = "/Users/parrt/antlr/code/antlr4/runtime-testsuite/resources/org/antlr/v4/test/runtime/new_descriptors";
//		new File(descrRootDir).mkdir();
//		String groupName = clazz.getSimpleName();
//		groupName = groupName.replace("Descriptors", "");
//		String groupDir = descrRootDir + "/" + groupName;
//		new File(groupDir).mkdir();
//
//		for (RuntimeTestDescriptor d : descriptors) {
//			try {
//				Pair<String,String> g = d.getGrammar();
//				String gname = g.a;
//				String grammar = g.b;
//				String filename = d.getTestName()+".txt";
//				String content = "";
//				String input = quoteForDescriptorFile(d.getInput());
//				String output = quoteForDescriptorFile(d.getOutput());
//				String errors = quoteForDescriptorFile(d.getErrors());
//				content += "[type]\n";
//				content += d.getTestType();
//				content += "\n\n";
//				content += "[grammar]\n";
//				content += grammar;
//				if ( !content.endsWith("\n\n") ) content += "\n";
//				if ( d.getSlaveGrammars()!=null ) {
//					for (Pair<String, String> slaveG : d.getSlaveGrammars()) {
//						String sg = quoteForDescriptorFile(slaveG.b);
//						content += "[slaveGrammar]\n";
//						content += sg;
//						content += "\n";
//					}
//				}
//				if ( d.getStartRule()!=null && d.getStartRule().length()>0 ) {
//					content += "[start]\n";
//					content += d.getStartRule();
//					content += "\n\n";
//				}
//				if ( input!=null ) {
//					content += "[input]\n";
//					content += input;
//					content += "\n";
//				}
//				if ( output!=null ) {
//					content += "[output]\n";
//					content += output;
//					content += "\n";
//				}
//				if ( errors!=null ) {
//					content += "[errors]\n";
//					content += errors;
//					content += "\n";
//				}
//				if ( d.showDFA() || d.showDiagnosticErrors() ) {
//					content += "[flags]\n";
//					if (d.showDFA()) {
//						content += "showDFA\n";
//					}
//					if (d.showDiagnosticErrors()) {
//						content += "showDiagnosticErrors\n";
//					}
//					content += '\n';
//				}
//				List<String> skip = new ArrayList<>();
//				for (String target : Targets) {
//					if ( d.ignore(target) ) {
//						skip.add(target);
//					}
//				}
//				if ( skip.size()>0 ) {
//					content += "[skip]\n";
//					for (String sk : skip) {
//						content += sk+"\n";
//					}
//					content += '\n';
//				}
//				Files.write(Paths.get(groupDir + "/" + filename), content.getBytes());
//			}
//			catch (IOException e) {
//				//exception handling left as an exercise for the reader
//				System.err.println(e.getMessage());
//			}
//		}
//	}
//
//	/** Rules for strings look like this:
//	 *
//	 * [input]  if one line, remove all WS before/after
//	 * a b
//	 *
//	 * [input] need whitespace
//	 * """34
//	 *  34"""
//	 *
//	 * [input] single quote char, remove all WS before/after
//	 * "
//	 *
//	 * [input]  same as "b = 6\n" in java
//	 * """b = 6
//	 * """
//	 *
//	 * [input]
//	 * """a """ space and no newline inside
//	 *
//	 * [input]  same as java string "\"aaa"
//	 * "aaa
//	 *
//	 * [input] ignore front/back \n except leave last \n
//	 * a
//	 * b
//	 * c
//	 * d
//	 */
//	private static String quoteForDescriptorFile(String s) {
//		if ( s==null ) {
//			return null;
//		}
//		long nnl = s.chars().filter(ch -> ch == '\n').count();
//
//		if ( s.endsWith(" ") ||            // whitespace matters
//				(nnl==1&&s.endsWith("\n")) || // "b = 6\n"
//				s.startsWith("\n") ) {        // whitespace matters
//			return "\"\"\"" + s + "\"\"\"\n";
//		}
//		if ( s.endsWith(" \n") || s.endsWith("\n\n") ) {
//			return "\"\"\"" + s + "\"\"\"\n";
//		}
//		if ( nnl==0 ) { // one line input
//			return s + "\n";
//		}
//		if ( nnl>1 && s.endsWith("\n") ) {
//			return s;
//		}
//		if ( !s.endsWith("\n") ) { // "a\n b"
//			return "\"\"\"" + s + "\"\"\"\n";
//		}
//
//		return s;
//	}

}
