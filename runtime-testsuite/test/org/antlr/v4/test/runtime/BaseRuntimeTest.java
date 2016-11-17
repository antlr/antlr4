package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Utils;
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
import java.util.List;

import static junit.framework.TestCase.assertEquals;
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

	protected RuntimeTestSupport delegate;
	protected RuntimeTestDescriptor descriptor;

	public BaseRuntimeTest(RuntimeTestDescriptor descriptor, RuntimeTestSupport delegate) {
		this.descriptor = descriptor;
		this.delegate = delegate;
	}

//	@org.junit.Rule
//	public final Timeout eachTimeout = new Timeout(60000);

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
		if ( delegate instanceof RuntimeTestAssert ) {
			((RuntimeTestAssert)delegate).assertEqualStrings(descriptor.getErrors(), delegate.getParseErrors());
			((RuntimeTestAssert)delegate).assertEqualStrings(descriptor.getOutput(), found);
		}
		else {
			assertEquals(descriptor.getErrors(), delegate.getParseErrors());
			assertEquals(descriptor.getOutput(), found);
		}
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
		if ( delegate instanceof RuntimeTestAssert ) {
			((RuntimeTestAssert)delegate).assertEqualStrings(descriptor.getOutput(), found);
			((RuntimeTestAssert)delegate).assertEqualStrings(descriptor.getANTLRToolErrors(), delegate.getANTLRToolErrors());
			((RuntimeTestAssert)delegate).assertEqualStrings(descriptor.getErrors(), delegate.getParseErrors());
		}
		else {
			assertEquals(descriptor.getOutput(), found);
			assertEquals(descriptor.getANTLRToolErrors(), delegate.getANTLRToolErrors());
			assertEquals(descriptor.getErrors(), delegate.getParseErrors());
		}
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

	protected void mkdir(String dir) {
		File f = new File(dir);
		f.mkdirs();
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
}
