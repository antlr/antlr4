/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.runtime.java.JavaRunner;
import org.antlr.v4.test.runtime.java.JavaRuntimeTests;
import org.antlr.v4.test.runtime.states.ExecutedState;
import org.antlr.v4.test.runtime.states.State;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.stringtemplate.v4.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.antlr.v4.test.runtime.FileUtils.writeFile;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.joinLines;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/** This class represents runtime tests for specified runtime.
 *  It pulls data from {@link RuntimeTestDescriptor} and uses junit to trigger tests.
 *  The only functionality needed to execute a test is defined in {@link RuntimeRunner}.
 *  All the various test rig classes derived from this one.
 *  E.g., see {@link JavaRuntimeTests}.
 */
public abstract class RuntimeTests {
	protected abstract RuntimeRunner createRuntimeRunner();

	private final static HashMap<String, RuntimeTestDescriptor[]> testDescriptors = new HashMap<>();
	private final static Map<String, STGroup> cachedTargetTemplates = new HashMap<>();
	private final static StringRenderer rendered = new StringRenderer();

	static {
		File descriptorsDir = new File(Paths.get(RuntimeTestUtils.resourcePath.toString(), "org/antlr/v4/test/runtime/descriptors").toString());
		File[] directoryListing = descriptorsDir.listFiles();
		assert directoryListing != null;
		for (File directory : directoryListing) {
			String groupName = directory.getName();
			if (groupName.startsWith(".")) {
				continue; // Ignore service directories (like .DS_Store in Mac)
			}

			List<RuntimeTestDescriptor> descriptors = new ArrayList<>();

			File[] descriptorFiles = directory.listFiles();
			assert descriptorFiles != null;
			for (File descriptorFile : descriptorFiles) {
				String name = descriptorFile.getName().replace(".txt", "");
				if (name.startsWith(".")) {
					continue;
				}

				String text;
				try {
					text = new String(Files.readAllBytes(descriptorFile.toPath()));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				descriptors.add(RuntimeTestDescriptorParser.parse(name, text, descriptorFile.toURI()));
			}

			testDescriptors.put(groupName, descriptors.toArray(new RuntimeTestDescriptor[0]));
		}

		for (String key : CustomDescriptors.descriptors.keySet()) {
			RuntimeTestDescriptor[] descriptors = CustomDescriptors.descriptors.get(key);
			RuntimeTestDescriptor[] existedDescriptors = testDescriptors.putIfAbsent(key, descriptors);
			if (existedDescriptors != null) {
				testDescriptors.put(key, Stream.concat(Arrays.stream(existedDescriptors), Arrays.stream(descriptors))
						.toArray(RuntimeTestDescriptor[]::new));
			}
		}
	}

	@TestFactory
	@Execution(ExecutionMode.CONCURRENT)
	public List<DynamicNode> runtimeTests() {
		List<DynamicNode> result = new ArrayList<>();

		for (String group : testDescriptors.keySet()) {
			ArrayList<DynamicNode> descriptorTests = new ArrayList<>();
			RuntimeTestDescriptor[] descriptors = testDescriptors.get(group);
			for (RuntimeTestDescriptor descriptor : descriptors) {
				descriptorTests.add(dynamicTest(descriptor.name, descriptor.uri, () -> {
					try (RuntimeRunner runner = createRuntimeRunner()) {
						String errorMessage = test(descriptor, runner);
						if (errorMessage != null) {
							runner.setSaveTestDir(true);
							fail(joinLines("Test: " + descriptor.name + "; " + errorMessage, "Test directory: " + runner.getTempDirPath()));
						}
					}
				}));
			}

			Path descriptorGroupPath = Paths.get(RuntimeTestUtils.resourcePath.toString(), "descriptors", group);
			result.add(dynamicContainer(group, descriptorGroupPath.toUri(), Arrays.stream(descriptorTests.toArray(new DynamicNode[0]))));
		}

		return result;
	}

	private static String test(RuntimeTestDescriptor descriptor, RuntimeRunner runner) {
		String targetName = runner.getLanguage();
		if (descriptor.ignore(targetName)) {
			System.out.println("Ignore " + descriptor);
			return null;
		}

		FileUtils.mkdir(runner.getTempDirPath());

		String grammarName = descriptor.grammarName;
		String grammar = prepareGrammars(descriptor, runner);

		String lexerName, parserName;
		boolean useListenerOrVisitor;
		String superClass;
		if (descriptor.testType == GrammarType.Parser || descriptor.testType == GrammarType.CompositeParser) {
			lexerName = grammarName + "Lexer";
			parserName = grammarName + "Parser";
			useListenerOrVisitor = true;
			if (targetName.equals("Java")) {
				superClass = JavaRunner.runtimeTestParserName;
			}
			else {
				superClass = null;
			}
		}
		else {
			lexerName = grammarName;
			parserName = null;
			useListenerOrVisitor = false;
			if (targetName.equals("Java")) {
				superClass = JavaRunner.runtimeTestLexerName;
			}
			else {
				superClass = null;
			}
		}

		RunOptions runOptions = new RunOptions(grammarName + ".g4",
				grammar,
				parserName,
				lexerName,
				useListenerOrVisitor,
				useListenerOrVisitor,
				descriptor.startRule,
				descriptor.input,
				false,
				descriptor.showDiagnosticErrors,
				descriptor.showDFA,
				Stage.Execute,
				false,
				targetName,
				superClass
		);

		State result = runner.run(runOptions);

		return assertCorrectOutput(descriptor, targetName, result);
	}

	private static String prepareGrammars(RuntimeTestDescriptor descriptor, RuntimeRunner runner) {
		String targetName = runner.getLanguage();

		STGroup targetTemplates;
		synchronized (cachedTargetTemplates) {
			targetTemplates = cachedTargetTemplates.get(targetName);
			if (targetTemplates == null) {
				ClassLoader classLoader = RuntimeTests.class.getClassLoader();
				URL templates = classLoader.getResource("org/antlr/v4/test/runtime/templates/" + targetName + ".test.stg");
				assert templates != null;
				targetTemplates = new STGroupFile(templates, "UTF-8", '<', '>');
				targetTemplates.registerRenderer(String.class, rendered);
				cachedTargetTemplates.put(targetName, targetTemplates);
			}
		}

		// write out any slave grammars
		List<Pair<String, String>> slaveGrammars = descriptor.slaveGrammars;
		if (slaveGrammars != null) {
			for (Pair<String, String> spair : slaveGrammars) {
				STGroup g = new STGroup('<', '>');
				g.registerRenderer(String.class, rendered);
				g.importTemplates(targetTemplates);
				ST grammarST = new ST(g, spair.b);
				writeFile(runner.getTempDirPath(), spair.a + ".g4", grammarST.render());
			}
		}

		STGroup g = new STGroup('<', '>');
		g.importTemplates(targetTemplates);
		g.registerRenderer(String.class, rendered);
		ST grammarST = new ST(g, descriptor.grammar);
		return grammarST.render();
	}

	private static String assertCorrectOutput(RuntimeTestDescriptor descriptor, String targetName, State state) {
		ExecutedState executedState;
		if (state instanceof ExecutedState) {
			executedState = (ExecutedState)state;
			if (executedState.exception != null) {
				return state.getErrorMessage();
			}
		}
		else {
			return state.getErrorMessage();
		}

		String expectedOutput = descriptor.output;
		String expectedParseErrors = descriptor.errors;

		boolean doesOutputEqualToExpected = executedState.output.equals(expectedOutput);
		if (!doesOutputEqualToExpected || !executedState.errors.equals(expectedParseErrors)) {
			String message;
			if (doesOutputEqualToExpected) {
				message = "Parse output is as expected, but errors are not: ";
			}
			else {
				message = "Parse output is incorrect: " +
						"expectedOutput:<" + expectedOutput + ">; actualOutput:<" + executedState.output + ">; ";
			}

			return "[" + targetName + ":" + descriptor.name + "] " +
					message +
					"expectedParseErrors:<" + expectedParseErrors + ">;" +
					"actualParseErrors:<" + executedState.errors + ">.";
		}

		return null;
	}
}
