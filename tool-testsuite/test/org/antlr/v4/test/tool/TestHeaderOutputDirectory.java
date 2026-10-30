/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.Tool;
import org.antlr.v4.test.runtime.ErrorQueue;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.BuildDependencyGenerator;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestHeaderOutputDirectory {
	private static final String GRAMMAR =
		"grammar T;\n" +
		"s : ID ;\n" +
		"ID : [a-z]+ ;\n" +
		"WS : [ \\t\\r\\n]+ -> skip ;\n";

	@TempDir
	Path tempDir;

	Path grammarFile;
	Path gen;
	Path inc;

	@BeforeEach
	public void beforeEach() throws IOException {
		grammarFile = tempDir.resolve("T.g4");
		Files.write(grammarFile, GRAMMAR.getBytes());
		gen = tempDir.resolve("gen");
		inc = tempDir.resolve("inc");
	}

	@Test
	public void testOutputDirectoryOnlyKeepsLayout() throws IOException {
		ErrorQueue errors = new ErrorQueue();
		generate(errors, "-Dlanguage=Cpp", "-o", gen.toString(), grammarFile.toString());

		assertEquals(0, errors.errors.size(), errors.toString());
		assertEquals(set(
				"gen/T.interp", "gen/T.tokens",
				"gen/TBaseListener.cpp", "gen/TBaseListener.h",
				"gen/TLexer.cpp", "gen/TLexer.h", "gen/TLexer.interp", "gen/TLexer.tokens",
				"gen/TListener.cpp", "gen/TListener.h",
				"gen/TParser.cpp", "gen/TParser.h"),
			generatedFiles());
	}

	@Test
	public void testHeadersGoToHeaderDirectory() throws IOException {
		ErrorQueue errors = new ErrorQueue();
		generate(errors, "-Dlanguage=Cpp", "-o", gen.toString(), "-header-dir", inc.toString(), grammarFile.toString());

		assertEquals(0, errors.errors.size(), errors.toString());
		assertEquals(set(
				"gen/T.interp", "gen/T.tokens",
				"gen/TBaseListener.cpp", "gen/TLexer.cpp", "gen/TLexer.interp", "gen/TLexer.tokens",
				"gen/TListener.cpp", "gen/TParser.cpp",
				"inc/TBaseListener.h", "inc/TLexer.h", "inc/TListener.h", "inc/TParser.h"),
			generatedFiles());
	}

	@Test
	public void testListenerAndVisitorHeadersGoToHeaderDirectory() throws IOException {
		ErrorQueue errors = new ErrorQueue();
		generate(errors, "-Dlanguage=Cpp", "-visitor", "-listener",
			"-o", gen.toString(), "-header-dir", inc.toString(), grammarFile.toString());

		assertEquals(0, errors.errors.size(), errors.toString());
		assertEquals(set(
				"gen/T.interp", "gen/T.tokens",
				"gen/TBaseListener.cpp", "gen/TBaseVisitor.cpp", "gen/TLexer.cpp", "gen/TLexer.interp",
				"gen/TLexer.tokens", "gen/TListener.cpp", "gen/TParser.cpp", "gen/TVisitor.cpp",
				"inc/TBaseListener.h", "inc/TBaseVisitor.h", "inc/TLexer.h", "inc/TListener.h",
				"inc/TParser.h", "inc/TVisitor.h"),
			generatedFiles());
	}

	@Test
	public void testTargetWithoutHeadersIgnoresHeaderDirectory() throws IOException {
		Path plain = tempDir.resolve("plain");
		ErrorQueue errors = new ErrorQueue();
		generate(errors, "-o", plain.toString(), grammarFile.toString());
		generate(errors, "-o", gen.toString(), "-header-dir", inc.toString(), grammarFile.toString());

		assertEquals(0, errors.errors.size(), errors.toString());
		assertFalse(Files.exists(inc));
		Set<String> generated = generatedFiles();
		Set<String> withOption = new TreeSet<>();
		Set<String> withoutOption = new TreeSet<>();
		for (String name : generated) {
			if (name.startsWith("gen/")) withOption.add(name.substring("gen/".length()));
			else if (name.startsWith("plain/")) withoutOption.add(name.substring("plain/".length()));
		}
		assertEquals(withoutOption, withOption);
		assertTrue(withOption.contains("TParser.java"), withOption.toString());
		assertEquals(generated.size(), withOption.size() + withoutOption.size(), generated.toString());
	}

	/**
	 * One row per placement case: -o (null when absent), -header-dir, -Xexact-output-dir,
	 * grammar path, expected source directory and expected header directory.
	 */
	static Stream<Arguments> placements() {
		String sep = File.separator;
		String sub = "sub" + sep + "T.g4";
		String absolute = new File(System.getProperty("java.io.tmpdir"), "T.g4").getAbsolutePath();
		String home = "~" + sep + "x" + sep + "T.g4";
		return Stream.of(
			Arguments.of("gen", "inc", false, "T.g4", "gen" + sep + ".", "inc" + sep + "."),
			Arguments.of("gen", "inc", false, sub, "gen" + sep + "sub", "inc" + sep + "sub"),
			Arguments.of("gen", "inc", true, sub, "gen", "inc"),
			Arguments.of("gen", "inc", false, absolute, "gen", "inc"),
			Arguments.of("gen", "inc", true, absolute, "gen", "inc"),
			Arguments.of(null, "inc", false, sub, "sub", "inc" + sep + "sub"),
			Arguments.of(null, "inc", true, sub, "sub", "inc"),
			Arguments.of("gen", "inc", false, home, "gen", "inc"),
			Arguments.of("gen", "gen", false, sub, "gen" + sep + "sub", "gen" + sep + "sub"),
			Arguments.of("gen", "gen", true, sub, "gen", "gen")
		);
	}

	@ParameterizedTest
	@MethodSource("placements")
	public void testHeaderDirectoryResolvesLikeOutputDirectory(String outputDir, String headerDir, boolean exact,
															   String grammar, String sources, String headers) {
		List<String> args = new ArrayList<>();
		if (outputDir != null) {
			args.addAll(Arrays.asList("-o", outputDir));
		}
		args.addAll(Arrays.asList("-header-dir", headerDir));
		if (exact) {
			args.add("-Xexact-output-dir");
		}
		ErrorQueue errors = new ErrorQueue();
		Tool tool = createTool(errors, args.toArray(new String[0]));

		assertEquals(0, errors.errors.size(), errors.toString());
		assertEquals(new File(sources), tool.getOutputDirectory(grammar));
		assertEquals(new File(headers), tool.getHeaderOutputDirectory(grammar));
	}

	@Test
	public void testGrammarSubdirectoryIsKeptUnderHeaderDirectory() throws IOException {
		Path sub = tempDir.resolve("sub");
		Files.createDirectories(sub);
		Files.move(grammarFile, sub.resolve("T.g4"));
		ErrorQueue errors = new ErrorQueue();
		Tool tool = createTool(errors, "-Dlanguage=Cpp", "-no-listener",
			"-o", gen.toString(), "-header-dir", inc.toString(), "sub" + File.separator + "T.g4");
		tool.inputDirectory = tempDir.toFile();
		tool.processGrammarsOnCommandLine();

		assertEquals(0, errors.errors.size(), errors.toString());
		assertEquals(set(
				"gen/sub/T.interp", "gen/sub/T.tokens", "gen/sub/TLexer.cpp", "gen/sub/TLexer.interp",
				"gen/sub/TLexer.tokens", "gen/sub/TParser.cpp",
				"inc/sub/TLexer.h", "inc/sub/TParser.h"),
			generatedFiles());
	}

	@Test
	public void testDependenciesWithoutHeaderDirectoryAreUnchanged() {
		List<File> files = dependencies("-Dlanguage=Cpp", "-o", gen.toString());

		assertPlacement(files, gen, gen,
			"TParser.h", "TParser.cpp", "T.tokens", "TLexer.cpp", "TLexer.tokens", "TLexer.h",
			"TListener.h", "TListener.cpp", "TBaseListener.h", "TBaseListener.cpp");
	}

	@Test
	public void testDependenciesListHeadersInHeaderDirectory() {
		List<File> files = dependencies("-Dlanguage=Cpp", "-visitor",
			"-o", gen.toString(), "-header-dir", inc.toString());

		assertPlacement(files, gen, inc,
			"TParser.h", "TParser.cpp", "T.tokens", "TLexer.cpp", "TLexer.tokens", "TLexer.h",
			"TListener.h", "TListener.cpp", "TBaseListener.h", "TBaseListener.cpp",
			"TVisitor.h", "TVisitor.cpp", "TBaseVisitor.h", "TBaseVisitor.cpp");
	}

	@Test
	public void testDependenciesListHeadersInHeaderDirectoryWithoutOutputDirectory() {
		List<File> files = dependencies("-Dlanguage=Cpp", "-no-listener", "-header-dir", inc.toString());

		assertPlacement(files, tempDir, inc,
			"TParser.h", "TParser.cpp", "T.tokens", "TLexer.cpp", "TLexer.tokens", "TLexer.h");
	}

	@Test
	public void testDependenciesListNoHeadersForTargetWithoutHeaders() {
		List<File> files = dependencies("-Dlanguage=Java", "-o", gen.toString(), "-header-dir", inc.toString());

		for (File file : files) {
			assertFalse(file.getName().endsWith(".h"), files.toString());
			assertEquals(gen.toFile(), file.getParentFile(), file.toString());
		}
		assertTrue(files.contains(gen.resolve("TLexer.java").toFile()), files.toString());
	}

	@Test
	public void testDependencyOverrideIsReachedForHeadersWithoutHeaderDirectory() {
		ErrorQueue errors = new ErrorQueue();
		Tool tool = createTool(errors, "-Dlanguage=Cpp", "-no-listener", "-o", gen.toString());
		Grammar g = tool.loadGrammar(grammarFile.toString());
		Set<String> overridden = new TreeSet<>();
		new BuildDependencyGenerator(tool, g) {
			@Override
			public File getOutputFile(String fileName) {
				overridden.add(fileName);
				return super.getOutputFile(fileName);
			}
		}.getGeneratedFileList();

		assertEquals(0, errors.errors.size(), errors.toString());
		assertTrue(overridden.contains("TParser.h"), overridden.toString());
	}

	@Test
	public void testWriterOverrideIsReachedForHeadersWithoutHeaderDirectory() {
		ErrorQueue errors = new ErrorQueue();
		Set<String> overridden = new TreeSet<>();
		Tool tool = new Tool(new String[] {"-Dlanguage=Cpp", "-no-listener", "-o", gen.toString(),
				grammarFile.toString()}) {
			@Override
			public Writer getOutputFileWriter(Grammar g, String fileName) throws IOException {
				overridden.add(fileName);
				return super.getOutputFileWriter(g, fileName);
			}
		};
		tool.addListener(errors);
		tool.processGrammarsOnCommandLine();

		assertEquals(0, errors.errors.size(), errors.toString());
		assertTrue(overridden.containsAll(set("TLexer.h", "TParser.h")), overridden.toString());
	}

	@ParameterizedTest
	@ValueSource(strings = {"/", "\\"})
	public void testTrailingSeparatorIsAccepted(String separator) {
		ErrorQueue errors = new ErrorQueue();
		Tool tool = createTool(errors, "-Dlanguage=Cpp", "-o", gen.toString(),
			"-header-dir", inc + separator, grammarFile.toString());

		assertEquals(0, errors.errors.size(), errors.toString());
		assertEquals(inc.toFile(), tool.getHeaderOutputDirectory(grammarFile.toString()));
	}

	@Test
	public void testHeaderDirectoryNamingAFileIsAnErrorLikeOutputDirectory() throws IOException {
		Path file = tempDir.resolve("not-a-directory");
		Files.write(file, new byte[0]);
		ErrorQueue errors = new ErrorQueue();
		Tool tool = generate(errors, "-Dlanguage=Cpp", "-o", gen.toString(),
			"-header-dir", file.toString(), grammarFile.toString());

		assertEquals(Arrays.asList(ErrorType.OUTPUT_DIR_IS_FILE), errorTypes(errors));
		assertEquals(gen.toFile(), tool.getHeaderOutputDirectory(grammarFile.toString()));
		// as with -o naming a file, the error stops code generation
		assertEquals(set(), generatedFiles());
	}

	private Tool generate(ErrorQueue errors, String... args) {
		Tool tool = createTool(errors, args);
		tool.processGrammarsOnCommandLine();
		return tool;
	}

	private List<File> dependencies(String... args) {
		ErrorQueue errors = new ErrorQueue();
		Tool tool = createTool(errors, args);
		Grammar g = tool.loadGrammar(grammarFile.toString());
		List<File> files = new BuildDependencyGenerator(tool, g).getGeneratedFileList();
		assertEquals(0, errors.errors.size(), errors.toString());
		return files;
	}

	/**
	 * Exactly the expected names are listed, each once, each header is in the header directory and
	 * every other file is in the source directory.
	 */
	private static void assertPlacement(List<File> files, Path sources, Path headers, String... expectedNames) {
		List<String> names = new ArrayList<>();
		for (File file : files) {
			names.add(file.getName());
			Path expectedDir = file.getName().endsWith(".h") ? headers : sources;
			assertEquals(expectedDir.toFile(), file.getParentFile(), file.toString());
		}
		Collections.sort(names);
		List<String> expected = new ArrayList<>(Arrays.asList(expectedNames));
		Collections.sort(expected);
		assertEquals(expected, names, files.toString());
	}

	/** Attaches the listener before argument handling, so option errors are captured too. */
	private static Tool createTool(ErrorQueue errors, String... args) {
		return new Tool(args) {
			@Override
			protected void handleArgs() {
				addListener(errors);
				super.handleArgs();
			}
		};
	}

	private static List<ErrorType> errorTypes(ErrorQueue errors) {
		List<ErrorType> types = new ArrayList<>();
		for (ANTLRMessage message : errors.errors) {
			types.add(message.getErrorType());
		}
		return types;
	}

	/** Every file created under the temporary directory apart from inputs, with '/' as separator. */
	private Set<String> generatedFiles() throws IOException {
		try (Stream<Path> paths = Files.walk(tempDir)) {
			return paths
				.filter(Files::isRegularFile)
				.map(tempDir::relativize)
				.map(p -> p.toString().replace(File.separatorChar, '/'))
				.filter(name -> !name.equals("T.g4") && !name.equals("sub/T.g4") && !name.equals("not-a-directory"))
				.collect(Collectors.toCollection(TreeSet::new));
		}
	}

	private static Set<String> set(String... names) {
		return new TreeSet<>(Arrays.asList(names));
	}
}
