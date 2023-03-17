/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.ExecutedState;
import org.antlr.v4.test.runtime.states.GeneratedState;
import org.antlr.v4.test.runtime.states.State;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.antlr.v4.test.runtime.FileUtils.deleteDirectory;
import static org.antlr.v4.test.runtime.FileUtils.writeFile;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.*;

public abstract class RuntimeRunner implements AutoCloseable {

	public abstract String getLanguage();

	protected String getExtension() { return getLanguage().toLowerCase(); }

	protected String getTitleName() { return getLanguage(); }

	protected String getTestFileName() { return "Test"; }

	protected String getLexerSuffix() { return "Lexer"; }

	protected String getParserSuffix() { return "Parser"; }

	protected String getBaseListenerSuffix() { return "BaseListener"; }

	protected String getListenerSuffix() { return "Listener"; }

	protected String getBaseVisitorSuffix() { return "BaseVisitor"; }

	protected String getVisitorSuffix() { return "Visitor"; }

	protected String grammarNameToFileName(String grammarName) { return grammarName; }

	private static String runtimeToolPath;
	private static String compilerPath;

	protected final String getCompilerPath() {
		if (compilerPath == null) {
			compilerPath = getCompilerName();
			if (compilerPath != null) {
				String compilerPathFromProperty = System.getProperty(getPropertyPrefix() + "-compiler");
				if (compilerPathFromProperty != null && compilerPathFromProperty.length() > 0) {
					compilerPath = compilerPathFromProperty;
				}
			}
		}

		return compilerPath;
	}

	protected final String getRuntimeToolPath() {
		if (runtimeToolPath == null) {
			runtimeToolPath = getRuntimeToolName();
			if (runtimeToolPath != null) {
				String runtimeToolPathFromProperty = System.getProperty(getPropertyPrefix() + "-exec");
				if (runtimeToolPathFromProperty != null && runtimeToolPathFromProperty.length() > 0) {
					runtimeToolPath = runtimeToolPathFromProperty;
				}
			}
		}

		return runtimeToolPath;
	}

	protected String getCompilerName() { return null; }

	protected String getRuntimeToolName() { return getLanguage().toLowerCase(); }

	protected String getTestFileWithExt() { return getTestFileName() + "." + getExtension(); }

	protected String getExecFileName() { return getTestFileWithExt(); }

	protected String[] getExtraRunArgs() { return null; }

	protected Map<String, String> getExecEnvironment() { return null; }

	protected String getPropertyPrefix() {
		return "antlr-" + getLanguage().toLowerCase();
	}

	public final String getTempDirPath() {
		return tempTestDir.toString();
	}

	private boolean saveTestDir;

	protected final Path tempTestDir;

	protected RuntimeRunner() {
		this(null, false);
	}

	protected RuntimeRunner(Path tempDir, boolean saveTestDir) {
		if (tempDir == null) {
			String dirName = getClass().getSimpleName() + "-" + Thread.currentThread().getName() + "-" + System.currentTimeMillis();
			tempTestDir = Paths.get(TempDirectory, dirName);
		}
		else {
			tempTestDir = tempDir;
		}
		this.saveTestDir = saveTestDir;
	}

	public void setSaveTestDir(boolean saveTestDir) {
		this.saveTestDir = saveTestDir;
	}

	public void close() {
		removeTempTestDirIfRequired();
	}

	public final static String cacheDirectory;

	private static class InitializationStatus {
		public final Object lockObject = new Object();
		public volatile Boolean isInitialized;
		public Exception exception;
	}

	private final static HashMap<String, InitializationStatus> runtimeInitializationStatuses = new HashMap<>();

	static {
		cacheDirectory = new File(System.getProperty("java.io.tmpdir"), "ANTLR-runtime-testsuite-cache").getAbsolutePath();
	}

	protected final String getCachePath() {
		return getCachePath(getLanguage());
	}

	public static String getCachePath(String language) {
		return cacheDirectory + FileSeparator + language;
	}

	protected final String getRuntimePath() {
		return getRuntimePath(getLanguage());
	}

	public static String getRuntimePath(String language) {
		return runtimePath.toString() + FileSeparator + language;
	}

	// Allows any target to add additional options for the antlr tool such as the location of the output files
	// which is useful for the Go target for instance to avoid having to move them before running the test
	//
	protected List<String> getTargetToolOptions(RunOptions ro) {
		return null;
	}

	public State run(RunOptions runOptions) {
		List<String> options = new ArrayList<>();
		if (runOptions.useVisitor) {
			options.add("-visitor");
		}
		if (runOptions.superClass != null && runOptions.superClass.length() > 0) {
			options.add("-DsuperClass=" + runOptions.superClass);
		}

		// See if the target wants to add tool options.
		//
		List<String> targetOpts = getTargetToolOptions(runOptions);
		if (targetOpts != null) {
			options.addAll(targetOpts);
		}

		ErrorQueue errorQueue = Generator.antlrOnString(getTempDirPath(), getLanguage(),
				runOptions.grammarFileName, runOptions.grammarStr, false, options.toArray(new String[0]));

		List<GeneratedFile> generatedFiles = getGeneratedFiles(runOptions);
		GeneratedState generatedState = new GeneratedState(errorQueue, generatedFiles, null);

		if (generatedState.containsErrors() || runOptions.endStage == Stage.Generate) {
			return generatedState;
		}

		if (!initAntlrRuntimeIfRequired(runOptions)) {
			// Do not repeat ANTLR runtime initialization error
			return new CompiledState(generatedState, new Exception(getTitleName() + " ANTLR runtime is not initialized"));
		}

		writeRecognizerFile(runOptions);

		CompiledState compiledState = compile(runOptions, generatedState);

		if (compiledState.containsErrors() || runOptions.endStage == Stage.Compile) {
			return compiledState;
		}

		writeFile(getTempDirPath(), "input", runOptions.input);

		return execute(runOptions, compiledState);
	}

	protected List<GeneratedFile> getGeneratedFiles(RunOptions runOptions) {
		List<GeneratedFile> files = new ArrayList<>();
		String extensionWithDot = "." + getExtension();
		String fileGrammarName = grammarNameToFileName(runOptions.grammarName);
		boolean isCombinedGrammarOrGo = runOptions.lexerName != null && runOptions.parserName != null || getLanguage().equals("Go");
		if (runOptions.lexerName != null) {
			files.add(new GeneratedFile(fileGrammarName + (isCombinedGrammarOrGo ? getLexerSuffix() : "") + extensionWithDot, false));
		}
		if (runOptions.parserName != null) {
			files.add(new GeneratedFile(fileGrammarName + (isCombinedGrammarOrGo ? getParserSuffix() : "") + extensionWithDot, true));
			if (runOptions.useListener) {
				files.add(new GeneratedFile(fileGrammarName + getListenerSuffix() + extensionWithDot, true));
				String baseListenerSuffix = getBaseListenerSuffix();
				if (baseListenerSuffix != null) {
					files.add(new GeneratedFile(fileGrammarName + baseListenerSuffix + extensionWithDot, true));
				}
			}
			if (runOptions.useVisitor) {
				files.add(new GeneratedFile(fileGrammarName + getVisitorSuffix() + extensionWithDot, true));
				String baseVisitorSuffix = getBaseVisitorSuffix();
				if (baseVisitorSuffix != null) {
					files.add(new GeneratedFile(fileGrammarName + baseVisitorSuffix + extensionWithDot, true));
				}
			}
		}
		return files;
	}

	protected void writeRecognizerFile(RunOptions runOptions) {
		String text = RuntimeTestUtils.getTextFromResource("org/antlr/v4/test/runtime/helpers/" + getTestFileWithExt() + ".stg");
		ST outputFileST = new ST(text);
		outputFileST.add("grammarName", runOptions.grammarName);
		outputFileST.add("lexerName", runOptions.lexerName);
		outputFileST.add("parserName", runOptions.parserName);
		outputFileST.add("parserStartRuleName", grammarParseRuleToRecognizerName(runOptions.startRuleName));
		outputFileST.add("showDiagnosticErrors", runOptions.showDiagnosticErrors);
		outputFileST.add("traceATN", runOptions.traceATN);
		outputFileST.add("profile", runOptions.profile);
		outputFileST.add("showDFA", runOptions.showDFA);
		outputFileST.add("useListener", runOptions.useListener);
		outputFileST.add("useVisitor", runOptions.useVisitor);
		addExtraRecognizerParameters(outputFileST);
		writeFile(getTempDirPath(), getTestFileWithExt(), outputFileST.render());
	}

	protected String grammarParseRuleToRecognizerName(String startRuleName) {
		return startRuleName;
	}

	protected void addExtraRecognizerParameters(ST template) {
	}

	private boolean initAntlrRuntimeIfRequired(RunOptions runOptions) {
		String language = getLanguage();
		InitializationStatus status;

		// Create initialization status for every runtime with lock object
		synchronized (runtimeInitializationStatuses) {
			status = runtimeInitializationStatuses.get(language);
			if (status == null) {
				status = new InitializationStatus();
				runtimeInitializationStatuses.put(language, status);
			}
		}

		if (status.isInitialized != null) {
			return status.isInitialized;
		}

		// Locking per runtime, several runtimes can be being initialized simultaneously
		synchronized (status.lockObject) {
			if (status.isInitialized == null) {
				Exception exception = null;
				try {
					initRuntime(runOptions);
				} catch (Exception e) {
					exception = e;
					e.printStackTrace();
				}
				status.isInitialized = exception == null;
				status.exception = exception;
			}
		}
		return status.isInitialized;
	}

	protected void initRuntime(RunOptions runOptions) throws Exception {
	}

	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		return new CompiledState(generatedState, null);
	}

	protected ExecutedState execute(RunOptions runOptions, CompiledState compiledState) {
		String output = null;
		String errors = null;
		Exception exception = null;
		try {
			List<String> args = new ArrayList<>();
			String runtimeToolPath = getRuntimeToolPath();
			if (runtimeToolPath != null) {
				args.add(runtimeToolPath);
			}
			String[] extraRunArgs = getExtraRunArgs();
			if (extraRunArgs != null) {
				args.addAll(Arrays.asList(extraRunArgs));
			}
			args.add(getExecFileName());
			args.add("input");
			ProcessorResult result = Processor.run(args.toArray(new String[0]), getTempDirPath(), getExecEnvironment());
			output = result.output;
			errors = result.errors;
		} catch (InterruptedException | IOException e) {
			exception = e;
		}
		return new ExecutedState(compiledState, output, errors, exception);
	}

	protected ProcessorResult runCommand(String[] command, String workPath) throws Exception {
		return runCommand(command, workPath, null);
	}

	protected ProcessorResult runCommand(String[] command, String workPath, String description) throws Exception {
		String cmd = String.join(" ", command);
		try {
			return Processor.run(command, workPath);
		} catch (InterruptedException | IOException e) {
			String msg = "command \"" + cmd + "\"\n  in " + workPath + " failed";
			if (description != null) {
				msg += ":\n  can't " + description;
			}
			throw new Exception(msg, e);
		}
	}

	private void removeTempTestDirIfRequired() {
		if (!saveTestDir) {
			File dirFile = tempTestDir.toFile();
			if (dirFile.exists()) {
				try {
					deleteDirectory(dirFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
