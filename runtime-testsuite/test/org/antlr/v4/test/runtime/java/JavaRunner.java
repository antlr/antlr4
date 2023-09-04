/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.java;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.ProfilingATNSimulator;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.test.runtime.*;
import org.antlr.v4.test.runtime.java.helpers.CustomStreamErrorListener;
import org.antlr.v4.test.runtime.java.helpers.RuntimeTestLexer;
import org.antlr.v4.test.runtime.java.helpers.RuntimeTestParser;
import org.antlr.v4.test.runtime.java.helpers.TreeShapeListener;
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

public class JavaRunner extends RuntimeRunner {
	@Override
	public String getLanguage() {
		return "Java";
	}

	public static final String classPath = System.getProperty("java.class.path");

	public static final String runtimeTestLexerName = "org.antlr.v4.test.runtime.java.helpers.RuntimeTestLexer";
	public static final String runtimeTestParserName = "org.antlr.v4.test.runtime.java.helpers.RuntimeTestParser";

	public static final String runtimeHelpersPath = Paths.get(RuntimeTestUtils.runtimeTestsuitePath.toString(),
		"test", "org", "antlr", "v4", "test", "runtime", "java", "helpers").toString();

	private static JavaCompiler compiler;

	private final static DiagnosticErrorListener DiagnosticErrorListenerInstance = new DiagnosticErrorListener();

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
	protected void writeInputFile(RunOptions runOptions) {}

	@Override
	protected void writeRecognizerFile(RunOptions runOptions) {}

	@Override
	protected JavaCompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		String tempTestDir = getTempDirPath();

		List<GeneratedFile> generatedFiles = generatedState.generatedFiles;
		GeneratedFile firstFile = generatedFiles.get(0);

		if (!firstFile.isParser) {
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

		ClassLoader loader = null;
		Class<? extends Lexer> lexer = null;
		Class<? extends Parser> parser = null;
		Exception exception = null;

		try {
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

			ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

			List<File> files = new ArrayList<>();
			if (runOptions.lexerName != null) {
				files.add(new File(tempTestDir, runOptions.lexerName + ".java"));
			}
			if (runOptions.parserName != null) {
				files.add(new File(tempTestDir, runOptions.parserName + ".java"));
			}

			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(files);

			Iterable<String> compileOptions =
					Arrays.asList("-g", "-source", "1.8", "-target", "1.8", "-implicit:class", "-Xlint:-options", "-d",
							tempTestDir, "-cp", tempTestDir + PathSeparator + runtimeHelpersPath + PathSeparator + classPath);

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
		String output = null;
		String errors = null;
		ParseTree parseTree = null;
		Exception exception = null;

		try {
			InMemoryStreamHelper outputStreamHelper = InMemoryStreamHelper.initialize();
			InMemoryStreamHelper errorsStreamHelper = InMemoryStreamHelper.initialize();

			PrintStream outStream = new PrintStream(outputStreamHelper.pipedOutputStream);
			CustomStreamErrorListener errorListener = new CustomStreamErrorListener(new PrintStream(errorsStreamHelper.pipedOutputStream));

			CommonTokenStream tokenStream;
			RuntimeTestLexer lexer;
			if (runOptions.lexerName != null) {
				lexer = (RuntimeTestLexer) javaCompiledState.initializeLexer(runOptions.input);
				lexer.setOutStream(outStream);
				lexer.removeErrorListeners();
				lexer.addErrorListener(errorListener);
				tokenStream = new CommonTokenStream(lexer);
			} else {
				lexer = null;
				tokenStream = null;
			}

			if (runOptions.parserName != null) {
				RuntimeTestParser parser = (RuntimeTestParser) javaCompiledState.initializeParser(tokenStream);
				parser.setOutStream(outStream);
				parser.removeErrorListeners();
				parser.addErrorListener(errorListener);

				if (runOptions.showDiagnosticErrors) {
					parser.addErrorListener(DiagnosticErrorListenerInstance);
				}

				if (runOptions.traceATN) {
					// Setting trace_atn_sim isn't thread-safe,
					// But it's used only in helper TraceATN that is not integrated into tests infrastructure
					ParserATNSimulator.trace_atn_sim = true;
				}

				ProfilingATNSimulator profiler = null;
				if (runOptions.profile) {
					profiler = new ProfilingATNSimulator(parser);
					parser.setInterpreter(profiler);
				}
				parser.getInterpreter().setPredictionMode(runOptions.predictionMode);
				parser.setBuildParseTree(runOptions.buildParseTree);

				Method startRule;
				Object[] args = null;
				try {
					startRule = javaCompiledState.parser.getMethod(runOptions.startRuleName);
				} catch (NoSuchMethodException noSuchMethodException) {
					// try with int _p arg for recursive func
					startRule = javaCompiledState.parser.getMethod(runOptions.startRuleName, int.class);
					args = new Integer[]{0};
				}

				parseTree = (ParserRuleContext) startRule.invoke(parser, args);

				if (runOptions.profile) {
					outStream.println(Arrays.toString(profiler.getDecisionInfo()));
				}

				ParseTreeWalker.DEFAULT.walk(TreeShapeListener.INSTANCE, parseTree);
			}
			else {
				assert tokenStream != null;
				tokenStream.fill();
				for (Object t : tokenStream.getTokens()) {
					outStream.println(t);
				}
				if (runOptions.showDFA) {
					outStream.print(lexer.getInterpreter().getDFA(Lexer.DEFAULT_MODE).toLexerString());
				}
			}

			output = outputStreamHelper.close();
			errors = errorsStreamHelper.close();
		} catch (Exception ex) {
			exception = ex;
		}
		return new JavaExecutedState(javaCompiledState, output, errors, parseTree, exception);
	}

	static class InMemoryStreamHelper {
		private final PipedOutputStream pipedOutputStream;
		private final StreamReader streamReader;

		private InMemoryStreamHelper(PipedOutputStream pipedOutputStream, StreamReader streamReader) {
			this.pipedOutputStream = pipedOutputStream;
			this.streamReader = streamReader;
		}

		public static InMemoryStreamHelper initialize() throws IOException {
			PipedInputStream pipedInputStream = new PipedInputStream();
			PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
			StreamReader stdoutReader = new StreamReader(pipedInputStream);
			stdoutReader.start();
			return new InMemoryStreamHelper(pipedOutputStream, stdoutReader);
		}

		public String close() throws InterruptedException, IOException {
			pipedOutputStream.close();
			streamReader.join();
			return streamReader.toString();
		}
	}
}
