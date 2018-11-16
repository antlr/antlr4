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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
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
	public final static String[] Targets = {
		"Cpp",
		"Java",
		"Go",
		"CSharp",
		"Python2", "Python3",
		"Node", "Safari", "Firefox", "Explorer", "Chrome"
	};
	public final static String[] JavaScriptTargets = {
		"Node", "Safari", "Firefox", "Explorer", "Chrome"
	};

	static {
		// Add heartbeat thread to gen minimal output for travis, appveyor to
		// avoid timeout.
		Thread t = new Thread("heartbeat") {
			@Override
			public void run() {
				while (true) {
					System.out.print('.');
					try {
						Thread.sleep(5000);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
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

	public static void mkdir(String dir) {
		File f = new File(dir);
		f.mkdirs();
	}

	@Before
	public void setUp() throws Exception {
		// From http://junit.sourceforge.net/javadoc/org/junit/Assume.html
		// "The default JUnit runner treats tests with failing assumptions as ignored"
		assumeFalse(descriptor.ignore(descriptor.getTarget()));
		delegate.testSetUp();
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
		// System.out.println(delegate.getTmpDir());
		if ( descriptor.ignore(descriptor.getTarget()) ) {
			System.out.printf("Ignore "+descriptor);
			return;
		}
		if ( descriptor.getTestType().contains("Parser") ) {
			testParser(descriptor);
		}
		else {
			testLexer(descriptor);
		}
	}

	public void testParser(RuntimeTestDescriptor descriptor) throws Exception {
		mkdir(delegate.getTmpDir());

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
				writeFile(delegate.getTmpDir(), spair.a+".g4", grammarST.render());
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
		mkdir(delegate.getTmpDir());

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
				writeFile(delegate.getTmpDir(), spair.a+".g4", grammarST.render());
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
		mkdir(workdir);
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

	public static RuntimeTestDescriptor[] getRuntimeTestDescriptors(Class<?> clazz, String targetName) {
		Class<?>[] nestedClasses = clazz.getClasses();
		List<RuntimeTestDescriptor> descriptors = new ArrayList<RuntimeTestDescriptor>();
		for (Class<?> nestedClass : nestedClasses) {
			int modifiers = nestedClass.getModifiers();
			if ( RuntimeTestDescriptor.class.isAssignableFrom(nestedClass) && !Modifier.isAbstract(modifiers) ) {
				try {
					RuntimeTestDescriptor d = (RuntimeTestDescriptor) nestedClass.newInstance();
					d.setTarget(targetName);
					descriptors.add(d);
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return descriptors.toArray(new RuntimeTestDescriptor[0]);
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
}
