/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.csharp;

import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.test.runtime.*;
import org.stringtemplate.v4.ST;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.assertTrue;

public class BaseCSharpTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {
	private static Boolean isRuntimeInitialized = false;
	private final static String cSharpAntlrRuntimeDllName = "Antlr4.Runtime.Standard.dll";
	private final static String testProjectFileName = "Antlr4.Test.csproj";
	private static String cSharpTestProjectContent;
	private static final String cSharpCachingDirectory = Paths.get(cachingDirectory, "CSharp").toString();

	@Override
	protected String getPropertyPrefix() {
		return "antlr4-csharp";
	}

	protected String execLexer(String grammarFileName,
							   String grammarStr,
							   String lexerName,
							   String input) {
		return execLexer(grammarFileName, grammarStr, lexerName, input, false);
	}

	@Override
	public String execLexer(String grammarFileName,
							String grammarStr,
							String lexerName,
							String input,
							boolean showDFA) {
		boolean success = rawGenerateRecognizer(grammarFileName,
				grammarStr,
				null,
				lexerName);
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		writeLexerTestFile(lexerName, showDFA);
		addSourceFiles("Test.cs");
		if (!compile()) {
			System.err.println("Failed to compile!");
			return getParseErrors();
		}
		String output = execTest();
		if (output != null && output.length() == 0) {
			output = null;
		}
		return output;
	}

	Set<String> sourceFiles = new HashSet<>();

	private void addSourceFiles(String... files) {
		Collections.addAll(sourceFiles, files);
	}

	@Override
	public String execParser(String grammarFileName,
							 String grammarStr,
							 String parserName,
							 String lexerName,
							 String listenerName,
							 String visitorName,
							 String startRuleName,
							 String input,
							 boolean showDiagnosticErrors) {
		boolean success = rawGenerateRecognizer(grammarFileName,
				grammarStr,
				parserName,
				lexerName,
				"-visitor");
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		return rawExecRecognizer(parserName,
				lexerName,
				startRuleName,
				showDiagnosticErrors);
	}

	/**
	 * Return true if all is well
	 */
	protected boolean rawGenerateRecognizer(String grammarFileName,
											String grammarStr,
											String parserName,
											String lexerName,
											String... extraOptions) {
		return rawGenerateRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
	}

	/**
	 * Return true if all is well
	 */
	protected boolean rawGenerateRecognizer(String grammarFileName,
											String grammarStr,
											String parserName,
											String lexerName,
											boolean defaultListener,
											String... extraOptions) {
		ErrorQueue equeue = antlrOnString(getTempDirPath(), "CSharp", grammarFileName, grammarStr, defaultListener, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}

		List<String> files = new ArrayList<String>();
		if (lexerName != null) {
			files.add(lexerName + ".cs");
		}
		if (parserName != null) {
			files.add(parserName + ".cs");
			Set<String> optionsSet = new HashSet<String>(Arrays.asList(extraOptions));
			String grammarName = grammarFileName.substring(0, grammarFileName.lastIndexOf('.'));
			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarName + "Listener.cs");
				files.add(grammarName + "BaseListener.cs");
			}
			if (optionsSet.contains("-visitor")) {
				files.add(grammarName + "Visitor.cs");
				files.add(grammarName + "BaseVisitor.cs");
			}
		}
		addSourceFiles(files.toArray(new String[0]));
		return true;
	}

	protected String rawExecRecognizer(String parserName,
									   String lexerName,
									   String parserStartRuleName,
									   boolean debug) {
		setParseErrors(null);
		if (parserName == null) {
			writeLexerTestFile(lexerName, false);
		} else {
			writeParserTestFile(parserName,
					lexerName,
					parserStartRuleName,
					debug);
		}

		addSourceFiles("Test.cs");
		return execRecognizer();
	}

	public String execRecognizer() {
		boolean success = compile();
		assertTrue(success);

		String output = execTest();
		if (output != null && output.length() == 0) {
			output = null;
		}
		return output;
	}

	public boolean compile() {
		try {
			return buildProject();
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			return false;
		}
	}

	private String locateExec() {
		return new File(getTempTestDir(), "bin/Release/netcoreapp3.1/Test.dll").getAbsolutePath();
	}

	public boolean buildProject() {
		try {
			assertTrue(initializeRuntime());
			// save auxiliary files
			try (PrintWriter out = new PrintWriter(new File(getTempTestDir(), testProjectFileName))) {
				out.print(cSharpTestProjectContent);
			}

			// build test
			String[] args = new String[] { "dotnet", "build", testProjectFileName, "-c", "Release" };
			boolean success = runProcess(args, getTempDirPath());
			assertTrue(success);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			return false;
		}

		return true;
	}

	private boolean initializeRuntime() {
		// Compile runtime project once per overall maven test session (assuming forkCount=0)
		synchronized (BaseCSharpTest.class) {
			if ( isRuntimeInitialized) {
//				System.out.println("C# runtime build REUSED\n");
				return true;
			}

			System.out.println("Building C# runtime\n");

			// find runtime package
			final ClassLoader loader = Thread.currentThread().getContextClassLoader();
			final URL runtimeProj = loader.getResource("CSharp/src/Antlr4.csproj");
			if (runtimeProj == null) {
				throw new RuntimeException("C# runtime project file not found!");
			}
			File runtimeProjFile = new File(runtimeProj.getFile());
			String runtimeProjPath = runtimeProjFile.getPath();

			RuntimeTestUtils.mkdir(cSharpCachingDirectory);
			String[] args = new String[]{
					"dotnet",
					"build",
					runtimeProjPath,
					"-c",
					"Release",
					"-o",
					cSharpCachingDirectory
			};

			boolean success;
			try {
				String cSharpTestProjectResourceName = BaseCSharpTest.class.getPackage().getName().replace(".", "/") + "/";
				InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(cSharpTestProjectResourceName + testProjectFileName);
				int bufferSize = 1024;
				char[] buffer = new char[bufferSize];
				StringBuilder out = new StringBuilder();
				Reader in = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
				for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
					out.append(buffer, 0, numRead);
				}
				cSharpTestProjectContent = out.toString().replace(cSharpAntlrRuntimeDllName, Paths.get(cSharpCachingDirectory, cSharpAntlrRuntimeDllName).toString());

				success = runProcess(args, cSharpCachingDirectory);
			} catch (Exception e) {
				e.printStackTrace(System.err);
				success = false;
			}

			if (success) System.out.println("C# runtime build succeeded\n");
			else System.out.println("C# runtime build failed\n");

			isRuntimeInitialized = true; // try only once
			return success;
		}
	}

	private boolean runProcess(String[] args, String path) throws Exception {
		return runProcess(args, path, 0);
	}

	private boolean runProcess(String[] args, String path, int retries) throws Exception {
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.directory(new File(path));
		Process process = pb.start();
		StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
		StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
		stdoutVacuum.start();
		stderrVacuum.start();
		process.waitFor();
		stdoutVacuum.join();
		stderrVacuum.join();
		int exitValue = process.exitValue();
		boolean success = (exitValue == 0);
		if (!success) {
			setParseErrors(stderrVacuum.toString());
			System.err.println("runProcess command: " + Utils.join(args, " "));
			System.err.println("runProcess exitValue: " + exitValue);
			System.err.println("runProcess stdoutVacuum: " + stdoutVacuum);
			System.err.println("runProcess stderrVacuum: " + getParseErrors());
		}
		if (exitValue == 132) {
			// Retry after SIGILL.  We are seeing this intermittently on
			// macOS (issue #2078).
			if (retries < 3) {
				System.err.println("runProcess retrying; " + retries +
						" retries so far");
				return runProcess(args, path, retries + 1);
			} else {
				System.err.println("runProcess giving up after " + retries +
						" retries");
				return false;
			}
		}
		return success;
	}

	public String execTest() {
		String exec = locateExec();
		try {
			File tmpdirFile = new File(getTempDirPath());
			String[] args = new String[] { "dotnet", exec, new File(getTempTestDir(), "input").getAbsolutePath() };
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(tmpdirFile);
			Process process = pb.start();
			StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
			StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
			stdoutVacuum.start();
			stderrVacuum.start();
			process.waitFor();
			stdoutVacuum.join();
			stderrVacuum.join();
			process.exitValue();
			String stdoutString = stdoutVacuum.toString();
			String stderrString = stderrVacuum.toString();
			setParseErrors(stderrString);
			return stdoutString;
		}
		catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

	protected void writeParserTestFile(String parserName,
									   String lexerName,
									   String parserStartRuleName,
									   boolean debug) {
		ST outputFileST = new ST(
				"using System;\n" +
						"using Antlr4.Runtime;\n" +
						"using Antlr4.Runtime.Tree;\n" +
						"using System.Text;\n" +
						"\n" +
						"public class Test {\n" +
						"    public static void Main(string[] args) {\n" +
						"        Console.OutputEncoding = Encoding.UTF8;\n" +
						"        Console.InputEncoding = Encoding.UTF8;\n" +
						"        var input = CharStreams.fromPath(args[0]);\n" +
						"        <lexerName> lex = new <lexerName>(input);\n" +
						"        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
						"        <createParser>\n" +
						"        parser.BuildParseTree = true;\n" +
						"        ParserRuleContext tree = parser.<parserStartRuleName>();\n" +
						"        ParseTreeWalker.Default.Walk(new TreeShapeListener(), tree);\n" +
						"    }\n" +
						"}\n" +
						"\n" +
						"class TreeShapeListener : IParseTreeListener {\n" +
						"    public void VisitTerminal(ITerminalNode node) { }\n" +
						"    public void VisitErrorNode(IErrorNode node) { }\n" +
						"    public void ExitEveryRule(ParserRuleContext ctx) { }\n" +
						"\n" +
						"    public void EnterEveryRule(ParserRuleContext ctx) {\n" +
						"        for (int i = 0; i \\< ctx.ChildCount; i++) {\n" +
						"            IParseTree parent = ctx.GetChild(i).Parent;\n" +
						"            if (!(parent is IRuleNode) || ((IRuleNode)parent).RuleContext != ctx) {\n" +
						"                throw new Exception(\"Invalid parse tree shape detected.\");\n" +
						"            }\n" +
						"        }\n" +
						"    }\n" +
						"}"
		);
		ST createParserST = new ST("<parserName> parser = new <parserName>(tokens);\n");
		if (debug) {
			createParserST =
					new ST(
							"<parserName> parser = new <parserName>(tokens);\n" +
									"        parser.AddErrorListener(new DiagnosticErrorListener());\n");
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(getTempDirPath(), "Test.cs", outputFileST.render());
	}

	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
				"using System;\n" +
						"using Antlr4.Runtime;\n" +
						"using System.IO;\n" +
						"using System.Text;\n" +
						"\n" +
						"public class Test {\n" +
						"    public static void Main(string[] args) {\n" +
						"        Console.OutputEncoding = Encoding.UTF8;\n" +
						"        Console.InputEncoding = Encoding.UTF8;\n" +
						"        var input = CharStreams.fromPath(args[0]);\n" +
						"        <lexerName> lex = new <lexerName>(input);\n" +
						"        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
						"        tokens.Fill();\n" +
						"        foreach (object t in tokens.GetTokens())\n" +
						"        Console.Out.WriteLine(t);\n" +
						(showDFA ? "        Console.Out.Write(lex.Interpreter.GetDFA(Lexer.DEFAULT_MODE).ToLexerString());\n" : "") +
						"    }\n" +
						"}"
		);

		outputFileST.add("lexerName", lexerName);
		writeFile(getTempDirPath(), "Test.cs", outputFileST.render());
	}

	/**
	 * Return map sorted by key
	 */
	public <K extends Comparable<? super K>, V> LinkedHashMap<K, V> sort(Map<K, V> data) {
		LinkedHashMap<K, V> dup = new LinkedHashMap<K, V>();
		List<K> keys = new ArrayList<K>(data.keySet());
		Collections.sort(keys);
		for (K k : keys) {
			dup.put(k, data.get(k));
		}
		return dup;
	}

	protected static void assertEquals(String msg, int a, int b) {
		org.junit.Assert.assertEquals(msg, a, b);
	}

	protected static void assertEquals(String a, String b) {
		org.junit.Assert.assertEquals(a, b);
	}
}
