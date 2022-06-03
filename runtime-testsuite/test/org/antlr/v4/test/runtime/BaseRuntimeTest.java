/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.runtime.states.ExecutedState;
import org.antlr.v4.test.runtime.states.State;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;
import org.stringtemplate.v4.StringRenderer;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static junit.framework.TestCase.fail;
import static org.antlr.v4.test.runtime.FileUtils.eraseDirectory;
import static org.antlr.v4.test.runtime.FileUtils.writeFile;
import static org.junit.Assume.assumeFalse;

/** This class represents a single runtime test. It pulls data from
 *  a {@link RuntimeTestDescriptor} and uses junit to trigger a test.
 *  The only functionality needed to execute a test is defined in
 *  {@link BaseRuntimeTestSupport}. All of the various test rig classes
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

	protected BaseRuntimeTestSupport delegate;
	protected RuntimeTestDescriptor descriptor;

	public BaseRuntimeTest(RuntimeTestDescriptor descriptor, BaseRuntimeTestSupport delegate) {
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
			eraseDirectory(delegate.getTempTestDir());
		}
	};

	private final static StringRenderer rendered = new StringRenderer();

	@Test
	public void testOne() {
		// System.out.println(delegate.getTempDirPath());
		if (descriptor.ignore(descriptor.getTarget()) ) {
			System.out.println("Ignore " + descriptor);
			return;
		}

		FileUtils.mkdir(delegate.getTempDirPath());

		Pair<String, String> pair = descriptor.getGrammar();
		String sourceName = "org/antlr/v4/test/runtime/templates/" + descriptor.getTarget() + ".test.stg";
		String template = RuntimeTestUtils.getTextFromResource(sourceName);
		STGroup targetTemplates = new STGroupString(sourceName, template, '<', '>');
		targetTemplates.registerRenderer(String.class, rendered);

		// write out any slave grammars
		List<Pair<String, String>> slaveGrammars = descriptor.getSlaveGrammars();
		if ( slaveGrammars!=null ) {
			for (Pair<String, String> spair : slaveGrammars) {
				STGroup g = new STGroup('<', '>');
				g.registerRenderer(String.class, rendered);
				g.importTemplates(targetTemplates);
				ST grammarST = new ST(g, spair.b);
				writeFile(delegate.getTempDirPath(), spair.a+".g4", grammarST.render());
			}
		}

		String grammarName = pair.a;
		String grammar = pair.b;
		STGroup g = new STGroup('<', '>');
		g.importTemplates(targetTemplates);
		g.registerRenderer(String.class, rendered);
		ST grammarST = new ST(g, grammar);
		grammar = grammarST.render();

		String lexerName, parserName;
		if (descriptor.getTestType().contains("Parser") ) {
			lexerName = grammarName + "Lexer";
			parserName = grammarName + "Parser";
		}
		else {
			lexerName = grammarName;
			parserName = null;
		}

		RunOptions runOptions = new RunOptions(grammarName + ".g4",
				grammar,
				parserName,
				lexerName,
				true,
				true,
				descriptor.getStartRule(),
				descriptor.getInput(),
				false,
				descriptor.showDiagnosticErrors(),
				descriptor.showDFA(),
				Stage.Execute,
				false,
				delegate.getLanguage()
		);

		State result = delegate.run(runOptions);

		assertCorrectOutput(descriptor, result);
	}

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

		List<RuntimeTestDescriptor> descriptors = new ArrayList<>();
		for (String fname : descriptorFilenames) {
			String dtext = RuntimeTestUtils.getTextFromResource("org/antlr/v4/test/runtime/descriptors/" + group + "/" + fname);
			UniversalRuntimeTestDescriptor d = readDescriptor(dtext);
			if (!d.ignore(targetName)) {
				d.name = fname.replace(".txt", "");
				d.targetName = targetName;
				descriptors.add(d);
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
	public static UniversalRuntimeTestDescriptor readDescriptor(String dtext) throws RuntimeException {
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

	protected static void assertCorrectOutput(RuntimeTestDescriptor descriptor, State state) {
		ExecutedState executedState;
		if (state instanceof ExecutedState) {
			executedState = (ExecutedState)state;
			if (executedState.exception != null) {
				fail(state.getErrorMessage());
			}
		}
		else {
			fail(state.getErrorMessage());
			return;
		}

		String expectedOutput = descriptor.getOutput();
		String expectedParseErrors = descriptor.getErrors();

		if (executedState.output.equals(expectedOutput) &&
				executedState.errors.equals(expectedParseErrors)) {
			return;
		}

		if (executedState.output.equals(expectedOutput)) {
			fail("[" + descriptor.getTarget() + ":" + descriptor.getTestName() + "] " +
					"Parse output is as expected, but errors are not: " +
					"expectedParseErrors:<" + expectedParseErrors +
					">; actualParseErrors:<" + executedState.errors +
					">.");
		}
		else {
			fail("[" + descriptor.getTarget() + ":" + descriptor.getTestName() + "] " +
					"Parse output is incorrect: " +
					"expectedOutput:<" + expectedOutput +
					">; actualOutput:<" + executedState.output +
					">; expectedParseErrors:<" + expectedParseErrors +
					">; actualParseErrors:<" + executedState.errors +
					">.");
		}
	}
}
