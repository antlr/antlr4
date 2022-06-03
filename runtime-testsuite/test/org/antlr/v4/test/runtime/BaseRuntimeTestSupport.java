package org.antlr.v4.test.runtime;

import org.antlr.v4.test.runtime.states.CompiledState;
import org.antlr.v4.test.runtime.states.ExecutedState;
import org.antlr.v4.test.runtime.states.GeneratedState;
import org.antlr.v4.test.runtime.states.State;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.antlr.v4.test.runtime.FileUtils.eraseDirectory;
import static org.antlr.v4.test.runtime.FileUtils.writeFile;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.FileSeparator;

public abstract class BaseRuntimeTestSupport {
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

	protected String getRuntimeToolName() { return getLanguage().toLowerCase(); }

	protected String getTestFileWithExt() { return getTestFileName() + "." + getExtension(); }

	protected String getExecFileName() { return getTestFileWithExt(); }

	protected String[] getExtraRunArgs() { return null; }

	protected Map<String, String> getExecEnvironment() { return null; }

	protected String getPropertyPrefix() {
		return "antlr-" + getLanguage().toLowerCase();
	}

	protected final File getTempTestDir() {
		return tempTestDir;
	}

	protected final String getTempDirPath() {
		return tempTestDir.getAbsolutePath();
	}

	private File tempTestDir = null;

	private final static Object runtimeInitLockObject = new Object();

	public final static String cacheDirectory;
	public final static Path targetClassesPath;

	private static class InitializationStatus {
		public final Object lockObject = new Object();
		public Boolean isInitialized;
		public Exception exception;
	}

	private final static HashMap<String, InitializationStatus> runtimeInitializationStatuses = new HashMap<>();

	static {
		targetClassesPath = Paths.get(RuntimeTestUtils.runtimeTestsuitePath.toString(), "target", "classes");
		cacheDirectory = new File(System.getProperty("java.io.tmpdir"), "ANTLR-runtime-testsuite-cache").getAbsolutePath();
	}

	@org.junit.Rule
	public final TestRule testWatcher = new TestWatcher() {
		@Override
		protected void succeeded(Description description) {
			eraseDirectory(tempTestDir);
		}
	};

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
		return targetClassesPath.toString() + FileSeparator + language;
	}

	protected State run(RunOptions runOptions) {
		String[] options = runOptions.useVisitor ? new String[]{"-visitor"} : new String[0];
		ErrorQueue errorQueue = Generator.antlrOnString(getTempDirPath(), getLanguage(),
				runOptions.grammarFileName, runOptions.grammarStr, false, options);

		List<String> generatedFiles = getGeneratedFiles(runOptions);
		GeneratedState generatedState = new GeneratedState(errorQueue, generatedFiles, null);

		if (generatedState.containsErrors() || runOptions.endStage == Stage.Generate) {
			return generatedState;
		}

		writeRecognizerFile(runOptions);

		if (!initAntlrRuntimeIfRequired()) {
			// Do not repeat ANTLR runtime initialization error
			return new CompiledState(generatedState, new Exception(getTitleName() + " ANTLR runtime is not initialized"));
		}

		CompiledState compiledState = compile(runOptions, generatedState);

		if (compiledState.containsErrors() || runOptions.endStage == Stage.Compile) {
			return compiledState;
		}

		writeFile(getTempDirPath(), "input", runOptions.input);

		return execute(runOptions, compiledState);
	}

	protected List<String> getGeneratedFiles(RunOptions runOptions) {
		List<String> files = new ArrayList<>();
		String extensionWithDot = "." + getExtension();
		String fileGrammarName = grammarNameToFileName(runOptions.grammarName);
		boolean isCombinedGrammarOrGo = runOptions.lexerName != null && runOptions.parserName != null || getLanguage().equals("Go");
		if (runOptions.lexerName != null) {
			files.add(fileGrammarName + (isCombinedGrammarOrGo ? getLexerSuffix() : "") + extensionWithDot);
		}
		if (runOptions.parserName != null) {
			files.add(fileGrammarName + (isCombinedGrammarOrGo ? getParserSuffix() : "") + extensionWithDot);
			if (runOptions.useListener) {
				files.add(fileGrammarName + getListenerSuffix() + extensionWithDot);
				String baseListenerSuffix = getBaseListenerSuffix();
				if (baseListenerSuffix != null) {
					files.add(fileGrammarName + baseListenerSuffix + extensionWithDot);
				}
			}
			if (runOptions.useVisitor) {
				files.add(fileGrammarName + getVisitorSuffix() + extensionWithDot);
				String baseVisitorSuffix = getBaseVisitorSuffix();
				if (baseVisitorSuffix != null) {
					files.add(fileGrammarName + baseVisitorSuffix + extensionWithDot);
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
		outputFileST.add("debug", runOptions.showDiagnosticErrors);
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

	protected void addExtraRecognizerParameters(ST template) {}

	private boolean initAntlrRuntimeIfRequired() {
		String language = getLanguage();
		InitializationStatus status = runtimeInitializationStatuses.get(language);

		// Create initialization status for every runtime with lock object
		if (status == null) {
			synchronized (runtimeInitLockObject) {
				status = runtimeInitializationStatuses.get(language);
				if (status == null) {
					status = new InitializationStatus();
					runtimeInitializationStatuses.put(language, status);
				}
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
					initRuntime();
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

	protected void initRuntime() throws Exception {
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
			String runtimeToolName = getRuntimeToolName();
			if (runtimeToolName != null) {
				args.add(runtimeToolName);
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
		try {
			return Processor.run(command, workPath);
		} catch (InterruptedException | IOException e) {
			throw description != null ? new Exception("can't " + description, e) : e;
		}
	}

	public void testSetUp() throws Exception {
		// new output dir for each test
		String propName = getPropertyPrefix() + "-test-dir";
		String prop = System.getProperty(propName);
		if(prop!=null && prop.length()>0) {
			tempTestDir = new File(prop);
		} else {
			String dirName = getClass().getSimpleName() +  "-" + Thread.currentThread().getName() + "-" + System.nanoTime();
			tempTestDir = new File(System.getProperty("java.io.tmpdir"), dirName);
		}
	}
}
