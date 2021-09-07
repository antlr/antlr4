/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.csharp;

import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.test.runtime.*;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BaseCSharpTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {

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
		addSourceFiles(files.toArray(new String[files.size()]));
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
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return false;
		}
	}

	private String locateExec() {
		return new File(getTempTestDir(), "bin/Release/netcoreapp3.1/Test.dll").getAbsolutePath();
	}

	public boolean buildProject() {
		try {
			// save auxiliary files
			String pack = BaseCSharpTest.class.getPackage().getName().replace(".", "/") + "/";
			saveResourceAsFile(pack + "Antlr4.Test.csproj", new File(getTempTestDir(), "Antlr4.Test.csproj"));

			// find runtime package
			final ClassLoader loader = Thread.currentThread().getContextClassLoader();
			final URL runtimeProj = loader.getResource("CSharp/src/Antlr4.csproj");
			if (runtimeProj == null) {
				throw new RuntimeException("C# runtime project file not found!");
			}
			File runtimeProjFile = new File(runtimeProj.getFile());
			String runtimeProjPath = runtimeProjFile.getPath();

			// add Runtime project reference
			String[] args = new String[]{
					"dotnet",
					"add",
					"Antlr4.Test.csproj",
					"reference",
					runtimeProjPath
			};
			boolean success = runProcess(args, getTempDirPath());
			assertTrue(success);

			// build test
			args = new String[]{
					"dotnet",
					"build",
					"Antlr4.Test.csproj",
					"-c",
					"Release"
			};
			success = runProcess(args, getTempDirPath());
			assertTrue(success);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return false;
		}

		return true;
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
			System.err.println("runProcess stdoutVacuum: " + stdoutVacuum.toString());
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

	private void saveResourceAsFile(String resourceName, File file) throws IOException {
		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
		if (input == null) {
			System.err.println("Can't find " + resourceName + " as resource");
			throw new IOException("Missing resource:" + resourceName);
		}
		OutputStream output = new FileOutputStream(file.getAbsolutePath());
		while (input.available() > 0) {
			output.write(input.read());
		}
		output.close();
		input.close();
	}

	public String execTest() {
		String exec = locateExec();
		try {
			File tmpdirFile = new File(getTempDirPath());
			Path output = tmpdirFile.toPath().resolve("output");
			Path errorOutput = tmpdirFile.toPath().resolve("error-output");
			String[] args = getExecTestArgs(exec, output, errorOutput);
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
			String writtenOutput = TestOutputReading.read(output);
			setParseErrors(TestOutputReading.read(errorOutput));
			int exitValue = process.exitValue();
			String stdoutString = stdoutVacuum.toString().trim();
			String stderrString = stderrVacuum.toString().trim();
			if (exitValue != 0) {
				System.err.println("execTest command: " + Utils.join(args, " "));
				System.err.println("execTest exitValue: " + exitValue);
			}
			if (!stdoutString.isEmpty()) {
				System.err.println("execTest stdoutVacuum: " + stdoutString);
			}
			if (!stderrString.isEmpty()) {
				System.err.println("execTest stderrVacuum: " + stderrString);
			}
			return writtenOutput;
		} catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

	private String[] getExecTestArgs(String exec, Path output, Path errorOutput) {
		return new String[]{
				"dotnet", exec, new File(getTempTestDir(), "input").getAbsolutePath(),
				output.toAbsolutePath().toString(),
				errorOutput.toAbsolutePath().toString()
		};
	}

	protected void writeParserTestFile(String parserName,
									   String lexerName,
									   String parserStartRuleName,
									   boolean debug) {
		ST outputFileST = new ST(
				"using System;\n" +
						"using Antlr4.Runtime;\n" +
						"using Antlr4.Runtime.Tree;\n" +
						"using System.IO;\n" +
						"using System.Text;\n" +
						"\n" +
						"public class Test {\n" +
						"    public static void Main(string[] args) {\n" +
						"        var input = CharStreams.fromPath(args[0]);\n" +
						"        using (FileStream fsOut = new FileStream(args[1], FileMode.Create, FileAccess.Write))\n" +
						"        using (FileStream fsErr = new FileStream(args[2], FileMode.Create, FileAccess.Write))\n" +
						"        using (TextWriter output = new StreamWriter(fsOut),\n" +
						"                          errorOutput = new StreamWriter(fsErr)) {\n" +
						"                <lexerName> lex = new <lexerName>(input, output, errorOutput);\n" +
						"                CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
						"                <createParser>\n" +
						"			 parser.BuildParseTree = true;\n" +
						"                ParserRuleContext tree = parser.<parserStartRuleName>();\n" +
						"                ParseTreeWalker.Default.Walk(new TreeShapeListener(), tree);\n" +
						"        }\n" +
						"    }\n" +
						"}\n" +
						"\n" +
						"class TreeShapeListener : IParseTreeListener {\n" +
						"	public void VisitTerminal(ITerminalNode node) { }\n" +
						"	public void VisitErrorNode(IErrorNode node) { }\n" +
						"	public void ExitEveryRule(ParserRuleContext ctx) { }\n" +
						"\n" +
						"	public void EnterEveryRule(ParserRuleContext ctx) {\n" +
						"		for (int i = 0; i \\< ctx.ChildCount; i++) {\n" +
						"			IParseTree parent = ctx.GetChild(i).Parent;\n" +
						"			if (!(parent is IRuleNode) || ((IRuleNode)parent).RuleContext != ctx) {\n" +
						"				throw new Exception(\"Invalid parse tree shape detected.\");\n" +
						"			}\n" +
						"		}\n" +
						"	}\n" +
						"}"
		);
		ST createParserST = new ST("        <parserName> parser = new <parserName>(tokens, output, errorOutput);\n");
		if (debug) {
			createParserST =
					new ST(
							"        <parserName> parser = new <parserName>(tokens, output, errorOutput);\n" +
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
						"        var input = CharStreams.fromPath(args[0]);\n" +
						"        using (FileStream fsOut = new FileStream(args[1], FileMode.Create, FileAccess.Write))\n" +
						"        using (FileStream fsErr = new FileStream(args[2], FileMode.Create, FileAccess.Write))\n" +
						"        using (TextWriter output = new StreamWriter(fsOut),\n" +
						"                          errorOutput = new StreamWriter(fsErr)) {\n" +
						"        <lexerName> lex = new <lexerName>(input, output, errorOutput);\n" +
						"        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
						"        tokens.Fill();\n" +
						"        foreach (object t in tokens.GetTokens())\n" +
						"			output.WriteLine(t);\n" +
						(showDFA ? "        output.Write(lex.Interpreter.GetDFA(Lexer.DEFAULT_MODE).ToLexerString());\n" : "") +
						"    }\n" +
						"}\n" +
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
