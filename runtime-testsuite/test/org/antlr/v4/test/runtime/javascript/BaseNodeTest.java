/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.javascript;

import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.test.runtime.*;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.*;

public class BaseNodeTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {

	@Override
	protected String getPropertyPrefix() {
		return "antlr4-javascript";
	}

	protected String execLexer(String grammarFileName, String grammarStr,
	                           String lexerName, String input) {
		return execLexer(grammarFileName, grammarStr, lexerName, input, false);
	}

	@Override
	public  String execLexer(String grammarFileName, String grammarStr,
	                         String lexerName, String input, boolean showDFA) {
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
		                                                grammarStr, null, lexerName, "-no-listener");
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		writeLexerTestFile(lexerName, showDFA);
		writeFile(getTempDirPath(), "package.json", "{\"type\": \"module\"}");
		String output = execModule("Test.js");
		if ( output!=null && output.length()==0 ) {
			output = null;
		}
		return output;
	}

	@Override
	public String execParser(String grammarFileName, String grammarStr,
	                         String parserName, String lexerName, String listenerName,
	                         String visitorName, String startRuleName, String input,
	                         boolean showDiagnosticErrors)
	{
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
				grammarStr, parserName, lexerName, "-visitor");
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		rawBuildRecognizerTestFile(parserName, lexerName, listenerName,
		                           visitorName, startRuleName, showDiagnosticErrors);
		writeFile(getTempDirPath(), "package.json", "{\"type\": \"module\"}");
		return execRecognizer();
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
	                                                String grammarStr, String parserName, String lexerName,
	                                                String... extraOptions) {
		return rawGenerateAndBuildRecognizer(grammarFileName, grammarStr,
		                                     parserName, lexerName, false, extraOptions);
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
	                                                String grammarStr, String parserName, String lexerName,
	                                                boolean defaultListener, String... extraOptions) {
		ErrorQueue equeue = antlrOnString(getTempDirPath(), "JavaScript", grammarFileName, grammarStr,
		                                  defaultListener, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}

		List<String> files = new ArrayList<String>();
		if (lexerName != null) {
			files.add(lexerName + ".js");
		}
		if (parserName != null) {
			files.add(parserName + ".js");
			Set<String> optionsSet = new HashSet<String>(
					Arrays.asList(extraOptions));
			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarFileName.substring(0,
						grammarFileName.lastIndexOf('.'))
						+ "Listener.js");
			}
			if (optionsSet.contains("-visitor")) {
				files.add(grammarFileName.substring(0,
						grammarFileName.lastIndexOf('.'))
						+ "Visitor.js");
			}
		}
		return true; // allIsWell: no compile
	}

	protected void rawBuildRecognizerTestFile(String parserName,
			String lexerName, String listenerName, String visitorName,
			String parserStartRuleName, boolean debug) {
		setParseErrors(null);
		if (parserName == null) {
			writeLexerTestFile(lexerName, false);
		}
		else {
			writeParserTestFile(parserName, lexerName, listenerName,
					visitorName, parserStartRuleName, debug);
		}
	}

	public String execRecognizer() {
		return execModule("Test.js");
	}

	public String execModule(String fileName) {
		try {
			String npmPath = locateNpm();
			if(!TestContext.isCI()) {
				installRuntime(npmPath);
				registerRuntime(npmPath);
			}
			String modulePath = new File(getTempTestDir(), fileName)
					.getAbsolutePath();
			linkRuntime(npmPath);
			String nodejsPath = locateNodeJS();
			String inputPath = new File(getTempTestDir(), "input")
					.getAbsolutePath();
			ProcessBuilder builder = new ProcessBuilder(nodejsPath, modulePath,
					inputPath);
			builder.environment().put("NODE_PATH", getTempDirPath());
			builder.directory(getTempTestDir());
			Process process = builder.start();
			StreamVacuum stdoutVacuum = new StreamVacuum(
					process.getInputStream());
			StreamVacuum stderrVacuum = new StreamVacuum(
					process.getErrorStream());
			stdoutVacuum.start();
			stderrVacuum.start();
			// TODO switch to jdk 8
			process.waitFor();
			// if(!process.waitFor(1L, TimeUnit.MINUTES))
			//	process.destroyForcibly();
			stdoutVacuum.join();
			stderrVacuum.join();
			String output = stdoutVacuum.toString();
			if ( output.length()==0 ) {
				output = null;
			}
			if (stderrVacuum.toString().length() > 0) {
				setParseErrors(stderrVacuum.toString());
			}
			return output;
		} catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
			System.err.println();
			return null;
		}
	}

	private void installRuntime(String npmPath) throws IOException, InterruptedException {
		String runtimePath = locateRuntime();
		ProcessBuilder builder = new ProcessBuilder(npmPath, "install");
		builder.directory(new File(runtimePath));
		builder.redirectError(new File(getTempTestDir(), "error.txt"));
		builder.redirectOutput(new File(getTempTestDir(), "output.txt"));
		Process process = builder.start();
		// TODO switch to jdk 8
		process.waitFor();
		// if(!process.waitFor(30L, TimeUnit.SECONDS))
		// 	process.destroyForcibly();
		int error = process.exitValue();
		if(error!=0)
			throw new IOException("'npm install' failed");
	}

	private void registerRuntime(String npmPath) throws IOException, InterruptedException {
		String runtimePath = locateRuntime();
		ProcessBuilder builder = new ProcessBuilder(npmPath, "link");
		builder.directory(new File(runtimePath));
		builder.redirectError(new File(getTempTestDir(), "error.txt"));
		builder.redirectOutput(new File(getTempTestDir(), "output.txt"));
		Process process = builder.start();
		// TODO switch to jdk 8
		process.waitFor();
		// if(!process.waitFor(30L, TimeUnit.SECONDS))
		//	process.destroyForcibly();
		int error = process.exitValue();
		if(error!=0)
			throw new IOException("'npm link' failed");
	}

	private void linkRuntime(String npmPath) throws IOException, InterruptedException {
		List<String> args = new ArrayList<>();
		if(TestContext.isCircleCI())
			args.add("sudo");
		args.addAll(Arrays.asList(npmPath, "link", "antlr4"));
		ProcessBuilder builder = new ProcessBuilder(args.toArray(new String[0]));
		builder.directory(getTempTestDir());
		File errorFile = new File(getTempTestDir(), "error.txt");
		builder.redirectError(errorFile);
		builder.redirectOutput(new File(getTempTestDir(), "output.txt"));
		Process process = builder.start();
		// TODO switch to jdk 8
		process.waitFor();
		// if(!process.waitFor(30L, TimeUnit.SECONDS))
		//	process.destroyForcibly();
		int error = process.exitValue();
		if(error!=0) {
			char[] errors = Utils.readFile(errorFile.getAbsolutePath());
			throw new IOException("'npm link antlr4' failed: " + new String(errors));
		}
	}

	private boolean canExecute(String tool) {
		try {
			ProcessBuilder builder = new ProcessBuilder(tool, "--version");
			builder.redirectErrorStream(true);
			Process process = builder.start();
			StreamVacuum vacuum = new StreamVacuum(process.getInputStream());
			vacuum.start();
			// TODO switch to jdk 8
			process.waitFor();
			// if(!process.waitFor(30L, TimeUnit.SECONDS))
			//	process.destroyForcibly();
			vacuum.join();
			return process.exitValue() == 0;
		} catch (Exception e) {
			return false;
		}
	}

	private String locateNpm() {
		// typically /usr/local/bin/npm
		String prop = System.getProperty("antlr-javascript-npm");
		if ( prop!=null && prop.length()!=0 ) {
			if(prop.contains(" "))
				prop = "\"" + prop + "\"";
			return prop;
		}
		return "npm"; // everywhere
	}

	private String locateNodeJS() {
		// typically /usr/local/bin/node
		String prop = System.getProperty("antlr-javascript-nodejs");
		if ( prop!=null && prop.length()!=0 ) {
			if(prop.contains(" "))
				prop = "\"" + prop + "\"";
			return prop;
		}
		if (canExecute("nodejs")) {
			return "nodejs"; // nodejs on Debian without node-legacy package
		}
		return "node"; // everywhere else
	}

	private String locateRuntime() {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		final URL runtimeSrc = loader.getResource("JavaScript");
		if ( runtimeSrc==null ) {
			throw new RuntimeException("Cannot find JavaScript runtime");
		}
		if(isWindows()){
			return runtimeSrc.getPath().replaceFirst("/", "");
		}
		return runtimeSrc.getPath();
	}

	protected void writeParserTestFile(String parserName, String lexerName,
			String listenerName, String visitorName,
			String parserStartRuleName, boolean debug) {
		ST outputFileST = new ST(
				"import antlr4 from 'antlr4';\n"
						+ "import <lexerName> from './<lexerName>.js';\n"
						+ "import <parserName> from './<parserName>.js';\n"
						+ "import <listenerName> from './<listenerName>.js';\n"
						+ "import <visitorName> from './<visitorName>.js';\n"
						+ "\n"
						+ "class TreeShapeListener extends antlr4.tree.ParseTreeListener {\n" +
						"    enterEveryRule(ctx) {\n" +
						"        for (let i = 0; i \\< ctx.getChildCount; i++) {\n" +
						"            const child = ctx.getChild(i)\n" +
						"            const parent = child.parentCtx\n" +
						"            if (parent.getRuleContext() !== ctx || !(parent instanceof antlr4.tree.RuleNode)) {\n" +
						"                throw `Invalid parse tree shape detected.`\n" +
						"            }\n" +
						"        }\n" +
						"    }\n" +
						"}\n"
						+ "\n"
						+ "function main(argv) {\n"
						+ "    var input = new antlr4.FileStream(argv[2], true);\n"
						+ "    var lexer = new <lexerName>(input);\n"
						+ "    var stream = new antlr4.CommonTokenStream(lexer);\n"
						+ "<createParser>"
						+ "    parser.buildParseTrees = true;\n"
						+ "	   const printer = function() {\n"
						+ "		this.println = function(s) { console.log(s); }\n"
						+ "		this.print = function(s) { process.stdout.write(s); }\n"
						+ "		return this;\n"
						+ "	 };\n"
						+ "    parser.printer = new printer();\n"
						+ "    var tree = parser.<parserStartRuleName>();\n"
						+ "    antlr4.tree.ParseTreeWalker.DEFAULT.walk(new TreeShapeListener(), tree);\n"
						+ "}\n" + "\n" + "main(process.argv);\n" + "\n");
		ST createParserST = new ST(
				"	var parser = new <parserName>(stream);\n");
		if (debug) {
			createParserST = new ST(
					"	var parser = new <parserName>(stream);\n"
							+ "	parser.addErrorListener(new antlr4.error.DiagnosticErrorListener());\n");
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("listenerName", listenerName);
		outputFileST.add("visitorName", visitorName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(getTempDirPath(), "Test.js", outputFileST.render());
	}

	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
				"import antlr4 from 'antlr4';\n"
						+ "import <lexerName> from './<lexerName>.js';\n"
						+ "\n"
						+ "function main(argv) {\n"
						+ "    var input = new antlr4.FileStream(argv[2], true);\n"
						+ "    var lexer = new <lexerName>(input);\n"
						+ "    var stream = new antlr4.CommonTokenStream(lexer);\n"
						+ "    stream.fill();\n"
						+ "    for(var i=0; i\\<stream.tokens.length; i++) {\n"
						+ "		console.log(stream.tokens[i].toString());\n"
						+ "    }\n"
						+ (showDFA ? "    process.stdout.write(lexer._interp.decisionToDFA[antlr4.Lexer.DEFAULT_MODE].toLexerString());\n"
								: "") + "}\n" + "\n" + "main(process.argv);\n"
						+ "\n");
		outputFileST.add("lexerName", lexerName);
		writeFile(getTempDirPath(), "Test.js", outputFileST.render());
	}

}
