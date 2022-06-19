/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.runtime.java.JavaRuntimeTests;
import org.antlr.v4.test.runtime.states.ExecutedState;
import org.antlr.v4.test.runtime.states.State;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;
import org.stringtemplate.v4.StringRenderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.antlr.v4.test.runtime.FileUtils.writeFile;
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

	private final static StringRenderer rendered = new StringRenderer();

	private final static HashMap<String, RuntimeTestDescriptor[]> testDescriptors = new HashMap<>();

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
				descriptors.add(RuntimeTestDescriptorParser.parse(name, text));
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
				descriptorTests.add(dynamicTest(descriptor.name, () -> {
					try (RuntimeRunner runner = createRuntimeRunner()) {
						test(descriptor, runner);
					}
				}));
			}
			result.add(dynamicContainer(group, descriptorTests));
		}

		return result;
	}

	private static void test(RuntimeTestDescriptor descriptor, RuntimeRunner runner) {
		String targetName = runner.getLanguage();
		if (descriptor.ignore(targetName)) {
			System.out.println("Ignore " + descriptor);
			return;
		}

		FileUtils.mkdir(runner.getTempDirPath());

		String sourceName = "org/antlr/v4/test/runtime/templates/" + targetName + ".test.stg";
		String template = RuntimeTestUtils.getTextFromResource(sourceName);
		STGroup targetTemplates = new STGroupString(sourceName, template, '<', '>');
		targetTemplates.registerRenderer(String.class, rendered);

		// write out any slave grammars
		List<Pair<String, String>> slaveGrammars = descriptor.slaveGrammars;
		if ( slaveGrammars!=null ) {
			for (Pair<String, String> spair : slaveGrammars) {
				STGroup g = new STGroup('<', '>');
				g.registerRenderer(String.class, rendered);
				g.importTemplates(targetTemplates);
				ST grammarST = new ST(g, spair.b);
				writeFile(runner.getTempDirPath(), spair.a + ".g4", grammarST.render());
			}
		}

		String grammarName = descriptor.grammarName;
		String grammar =  descriptor.grammar;
		STGroup g = new STGroup('<', '>');
		g.importTemplates(targetTemplates);
		g.registerRenderer(String.class, rendered);
		ST grammarST = new ST(g, grammar);
		grammar = grammarST.render();

		String lexerName, parserName;
		boolean useListenerOrVisitor;
		if (descriptor.testType == GrammarType.Parser || descriptor.testType == GrammarType.CompositeParser) {
			lexerName = grammarName + "Lexer";
			parserName = grammarName + "Parser";
			useListenerOrVisitor = true;
		}
		else {
			lexerName = grammarName;
			parserName = null;
			useListenerOrVisitor = false;
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
				targetName
		);

		State result = runner.run(runOptions);

		assertCorrectOutput(descriptor, targetName, result);
	}

	private static void assertCorrectOutput(RuntimeTestDescriptor descriptor, String targetName, State state) {
		ExecutedState executedState;
		if (state instanceof ExecutedState) {
			executedState = (ExecutedState)state;
			if (executedState.exception != null) {
				fail(state.getErrorMessage());
				return;
			}
		}
		else {
			fail(state.getErrorMessage());
			return;
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

			fail("[" + targetName + ":" + descriptor.name + "] " +
					message +
					"expectedParseErrors:<" + expectedParseErrors + ">;" +
					"actualParseErrors:<" + executedState.errors + ">.");
		}
	}
}
