/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.java;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.states.*;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.antlr.v4.test.runtime.FileUtils.replaceInFile;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.PathSeparator;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.getTextFromResource;

public class JavaRunner extends RuntimeRunner {
	@Override
	public String getLanguage() {
		return "Java";
	}

	public static final String classPath = System.getProperty("java.class.path");

	public static final String runtimeTestLexerName = "RuntimeTestLexer";
	public static final String runtimeTestParserName = "RuntimeTestParser";

	private final static String testLexerContent;
	private final static String testParserContent;
	private static JavaCompiler compiler;

	static {
		testLexerContent = getTextFromResource("org/antlr/v4/test/runtime/helpers/" + runtimeTestLexerName + ".java");
		testParserContent = getTextFromResource("org/antlr/v4/test/runtime/helpers/" + runtimeTestParserName + ".java");
	}

	public JavaRunner(Path tempDir, boolean saveTestDir) {
		super(tempDir, saveTestDir);
	}

	public JavaRunner() {
		super();
	}

	@Override
	protected void initRuntime(RunOptions runOptions) {
		compiler = ToolProvider.getSystemJavaCompiler();
	}

	@Override
	protected String getCompilerName() {
		return "javac";
	}

	@Override
	protected JavaCompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		String tempTestDir = getTempDirPath();

		List<GeneratedFile> generatedFiles = generatedState.generatedFiles;
		GeneratedFile firstFile = generatedFiles.get(0);

		if (!firstFile.isParser) {
			FileUtils.writeFile(tempTestDir, runtimeTestLexerName + ".java", testLexerContent);
			try {
				// superClass for combined grammar generates the same extends base class for Lexer and Parser
				// So, for lexer it should be replaced on correct base lexer class
				replaceInFile(Paths.get(getTempDirPath(), firstFile.name),
						"extends " + runtimeTestParserName + " {",
						"extends " + runtimeTestLexerName + " {");
			} catch (IOException e) {
				return new JavaCompiledState(generatedState, null, null, null, e);
			}
		}
		if (generatedFiles.stream().anyMatch(file -> file.isParser)) {
			FileUtils.writeFile(tempTestDir,  runtimeTestParserName + ".java", testParserContent);
		}

		ClassLoader loader = null;
		Class<? extends Lexer> lexer = null;
		Class<? extends Parser> parser = null;
		Exception exception = null;

		try {
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

			ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

			List<File> files = new ArrayList<>();
			File f = new File(tempTestDir, getTestFileWithExt());
			files.add(f);

			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(files);

			Iterable<String> compileOptions =
					Arrays.asList("-g", "-source", "1.8", "-target", "1.8", "-implicit:class", "-Xlint:-options", "-d",
							tempTestDir, "-cp", tempTestDir + PathSeparator + classPath);

			JavaCompiler.CompilationTask task =
					compiler.getTask(null, fileManager, null, compileOptions, null,
							compilationUnits);
			task.call();

			loader = new URLClassLoader(new URL[]{new File(tempTestDir).toURI().toURL()}, systemClassLoader);
			if (runOptions.lexerName != null) {
				lexer = loader.loadClass(runOptions.lexerName).asSubclass(Lexer.class);
			}
			if (runOptions.parserName != null) {
				parser = loader.loadClass(runOptions.parserName).asSubclass(Parser.class);
			}
		} catch (Exception ex) {
			exception = ex;
		}

		return new JavaCompiledState(generatedState, loader, lexer, parser, exception);
	}

	@Override
	protected ExecutedState execute(RunOptions runOptions, CompiledState compiledState) {
		JavaCompiledState javaCompiledState = (JavaCompiledState) compiledState;

		ExecutedState result;
		if (runOptions.returnObject) {
			result = execWithObject(runOptions, javaCompiledState);
		} else {
			result = execCommon(javaCompiledState);
		}
		return result;
	}

	private JavaExecutedState execWithObject(RunOptions runOptions, JavaCompiledState javaCompiledState) {
		ParseTree parseTree = null;
		Exception exception = null;
		try {
			Pair<Lexer, Parser> lexerParser = javaCompiledState.initializeLexerAndParser(runOptions.input);

			if (runOptions.parserName != null) {
				Method startRule;
				Object[] args = null;
				try {
					startRule = javaCompiledState.parser.getMethod(runOptions.startRuleName);
				} catch (NoSuchMethodException noSuchMethodException) {
					// try with int _p arg for recursive func
					startRule = javaCompiledState.parser.getMethod(runOptions.startRuleName, int.class);
					args = new Integer[]{0};
				}
				parseTree = (ParseTree) startRule.invoke(lexerParser.b, args);
			}
		} catch (Exception ex) {
			exception = ex;
		}
		return new JavaExecutedState(javaCompiledState, null, null, parseTree, exception);
	}

	private ExecutedState execCommon(JavaCompiledState compiledState) {
		Exception exception = null;
		String output = null;
		String errors = null;
		try {
			final Class<?> mainClass = compiledState.loader.loadClass(getTestFileName());
			final Method recognizeMethod = mainClass.getDeclaredMethod("recognize", String.class,
					PrintStream.class, PrintStream.class);

			PipedInputStream stdoutIn = new PipedInputStream();
			PipedInputStream stderrIn = new PipedInputStream();
			PipedOutputStream stdoutOut = new PipedOutputStream(stdoutIn);
			PipedOutputStream stderrOut = new PipedOutputStream(stderrIn);
			StreamReader stdoutReader = new StreamReader(stdoutIn);
			StreamReader stderrReader = new StreamReader(stderrIn);
			stdoutReader.start();
			stderrReader.start();

			recognizeMethod.invoke(null, new File(getTempDirPath(), "input").getAbsolutePath(),
					new PrintStream(stdoutOut), new PrintStream(stderrOut));

			stdoutOut.close();
			stderrOut.close();
			stdoutReader.join();
			stderrReader.join();
			output = stdoutReader.toString();
			errors = stderrReader.toString();
		} catch (Exception ex) {
			exception = ex;
		}
		return new JavaExecutedState(compiledState, output, errors, null, exception);
	}
}
