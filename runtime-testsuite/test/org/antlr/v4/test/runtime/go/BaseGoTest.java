/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.go;


import org.antlr.v4.test.runtime.*;
import org.junit.Assert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashMap;

import static junit.framework.TestCase.*;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.fail;

public class BaseGoTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {
	@Override
	public String getLanguage() {
		return "Go";
	}

	@Override
	public String getLexerSuffix() { return "_lexer"; }

	@Override
	public String getParserSuffix() { return "_parser"; }

	@Override
	public String getBaseListenerSuffix() { return "_base_listener"; }

	@Override
	public String getListenerSuffix() { return "_listener"; }

	@Override
	public String getBaseVisitorSuffix() { return "_base_visitor"; }

	@Override
	public String getVisitorSuffix() { return "_visitor"; }

	private final static String antlrTestPackageName = "antlr";
	private static final String goModFileName = "go.mod";
	private static final String GO_RUNTIME_IMPORT_PATH = "github.com/antlr/antlr4/runtime/Go/antlr"; // TODO: Change this before merging with upstream
	private static boolean isRuntimeInitialized = false;
	private static String newGoRootString;
	private static String goModContent = null;

	private File parserTempDir; // "parser" with tempDir

	@Override
	protected String getPropertyPrefix() {
		return "antlr4-go";
	}

	public static void groupSetUp() throws Exception { }
	public static void groupTearDown() throws Exception { }

	public void testSetUp() throws Exception {
		eraseParserTempDir();
		super.testSetUp();
		parserTempDir = new File(getTempTestDir(), "parser");
	}

	@Override
	public File getTempParserDir() {
		return parserTempDir;
	}

	private void eraseParserTempDir() {
		if(parserTempDir != null) {
			eraseDirectory(parserTempDir);
			parserTempDir = null;
		}
	}

	@Override
	public  String execLexer(String grammarFileName, String grammarStr,
	                         String lexerName, String input, boolean showDFA) {
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName, grammarStr);
		assertTrue(success);
		replaceImportPath();
		writeFile(getTempDirPath(), "input", input);
		writeLexerFile(lexerName, showDFA);
		writeGoModFile();
		return execModule();
	}

	@Override
	public String execParser(String grammarFileName, String grammarStr,
	                         String parserName, String lexerName, String listenerName,
	                         String visitorName, String startRuleName, String input,
	                         boolean showDiagnosticErrors)
	{
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName, grammarStr, "-visitor");
		assertTrue(success);
		replaceImportPath();
		writeFile(getTempDirPath(), "input", input);
		writeGoModFile();
		setParseErrors(null);
		String ruleNameFunction = startRuleName.substring(0, 1).toUpperCase() + startRuleName.substring(1);
		writeRecognizerFile(lexerName, parserName, ruleNameFunction, showDiagnosticErrors, false, false,
				listenerName != null, visitorName != null);
		return execModule();
	}

	private void writeGoModFile() {
		if (goModContent == null) {
			try {
				ProcessorResult result = Processor.run(new String[]{"go", "mod", "init", "test"}, getTempDirPath());
				if (result.exitCode != 0) {
					throw new Exception("Non-zero exit while setting up go module: " + result.output);
				}
				goModContent = new String(Files.readAllBytes(Paths.get(getTempDirPath(), goModFileName)), StandardCharsets.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("Unable to execute go mod");
			}
		} else {
			try (PrintWriter out = new PrintWriter(Paths.get(getTempDirPath(), goModFileName).toString())) {
				out.println(goModContent);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Assert.fail("Unable to write " + goModFileName);
			}
		}
	}

	private void replaceImportPath() {
		File[] files = getTempParserDir().listFiles(new GoFileFilter());
		for (File file : files) {
			String fileName = file.toString();
			try {
				String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
				String newContent = content.replaceAll(GO_RUNTIME_IMPORT_PATH, antlrTestPackageName);
				try (PrintWriter out = new PrintWriter(fileName)) {
					out.println(newContent);
				}
			} catch (IOException e) {
				fail("Error during processing " + fileName);
			}
		}
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName, String grammarStr, String... extraOptions) {
		ErrorQueue errorQueue = antlrOnString(getTempParserDirPath(), "Go", grammarFileName, grammarStr,
				false, extraOptions);
		return errorQueue.errors.isEmpty();
	}

	private String execModule() {
		initializeRuntime();

		String modulePath = new File(getTempTestDir(), "Test.go").getAbsolutePath();
		String inputPath = new File(getTempTestDir(), "input").getAbsolutePath();
		try {
			HashMap<String, String> environment = new HashMap<>();
			environment.put("GOROOT", newGoRootString);
			ProcessorResult result = Processor.run(new String[]{"go", "run", modulePath, inputPath}, getTempDirPath(), environment);
			setParseErrors(result.errors);
			return result.output;
		} catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

	private static synchronized boolean initializeRuntime() {
		if (isRuntimeInitialized)
			return true;

		String goRoot = getGoRootValue();
		Path newGoRoot = Paths.get(cachingDirectory, "Go");
		newGoRootString = newGoRoot.toString();
		try {
			File newGoRootDirectory = newGoRoot.toFile();
			if (newGoRootDirectory.exists())
				deleteDirectory(newGoRootDirectory);
			copyDirectory(Paths.get(goRoot), newGoRoot);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Unable to copy go system files");
		}

		String packageDir = Paths.get(newGoRootString, "src", antlrTestPackageName).toString();
		RuntimeTestUtils.mkdir(packageDir);
		File[] runtimeFiles = Paths.get(getRuntimePath("Go"), "antlr").toFile().listFiles(new GoFileFilter());
		if (runtimeFiles == null) {
			Assert.fail("Go runtime file list is empty.");
		}

		for (File runtimeFile : runtimeFiles) {
			File dest = new File(packageDir, runtimeFile.getName());
			try {
				RuntimeTestUtils.copyFile(runtimeFile, dest);
			} catch (IOException e) {
				e.printStackTrace();
				Assert.fail("Unable to copy runtime file " + runtimeFile);
			}
		}

		isRuntimeInitialized = true;
		return isRuntimeInitialized;
	}

	private static void copyDirectory(final Path source, final Path target, final CopyOption... options)
			throws IOException {
		Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), 2147483647, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
					throws IOException {
				Files.createDirectories(target.resolve(source.relativize(dir)));
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
					throws IOException {
				Files.copy(file, target.resolve(source.relativize(file)), options);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private static void deleteDirectory(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				deleteDirectory(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

	private static String getGoRootValue() {
		try {
			return Processor.run(new String[] {"go", "env", "GOROOT"}, null).output.trim();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Unable to execute go env");
		}
		return null;
	}
}
