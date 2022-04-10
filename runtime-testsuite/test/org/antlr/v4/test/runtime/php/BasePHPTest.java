/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.php;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.test.runtime.*;
import org.stringtemplate.v4.ST;

import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.assertTrue;

public class BasePHPTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {

	public String getPropertyPrefix() {
		return "antlr-php";
	}

	@Override
	public String execLexer(
		String grammarFileName,
		String grammarStr,
		String lexerName,
		String input,
		boolean showDFA
	) {
		boolean success = rawGenerateAndBuildRecognizer(
			grammarFileName,
			grammarStr,
			null,
			lexerName,
			"-no-listener"
		);
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		writeLexerTestFile(lexerName, showDFA);
		return execModule("Test.php");
	}

	public String execParser(
		String grammarFileName,
		String grammarStr,
		String parserName,
		String lexerName,
		String listenerName,
		String visitorName,
		String startRuleName,
		String input,
		boolean showDiagnosticErrors
	) {
		return execParser_(
			grammarFileName,
			grammarStr,
			parserName,
			lexerName,
			listenerName,
			visitorName,
			startRuleName,
			input,
			showDiagnosticErrors,
			false
		);
	}

	public String execParser_(
		String grammarFileName,
		String grammarStr,
		String parserName,
		String lexerName,
		String listenerName,
		String visitorName,
		String startRuleName,
		String input,
		boolean debug,
		boolean trace
	) {
		boolean success = rawGenerateAndBuildRecognizer(
			grammarFileName,
			grammarStr,
			parserName,
			lexerName,
			"-visitor"
		);

		assertTrue(success);

		writeFile(getTempDirPath(), "input", input);

		rawBuildRecognizerTestFile(
			parserName,
			lexerName,
			listenerName,
			visitorName,
			startRuleName,
			debug,
			trace
		);

		return execRecognizer();
	}

	/**
	 * Return true if all is well
	 */
	protected boolean rawGenerateAndBuildRecognizer(
		String grammarFileName,
		String grammarStr,
		String parserName,
		String lexerName,
		String... extraOptions
	) {
		return rawGenerateAndBuildRecognizer(
			grammarFileName,
			grammarStr,
			parserName,
			lexerName,
			false,
			extraOptions
		);
	}

	/**
	 * Return true if all is well
	 */
	protected boolean rawGenerateAndBuildRecognizer(
		String grammarFileName,
		String grammarStr,
		String parserName,
		String lexerName,
		boolean defaultListener,
		String... extraOptions
	) {
		ErrorQueue equeue = antlrOnString(getTempDirPath(), "PHP", grammarFileName, grammarStr, defaultListener, extraOptions);

		if (!equeue.errors.isEmpty()) {
			return false;
		}

		List<String> files = new ArrayList<String>();

		if (lexerName != null) {
			files.add(lexerName + ".php");
		}

		if (parserName != null) {
			files.add(parserName + ".php");
			Set<String> optionsSet = new HashSet<String>(Arrays.asList(extraOptions));

			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.')) + "Listener.php");
			}

			if (optionsSet.contains("-visitor")) {
				files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.')) + "Visitor.php");
			}
		}

		return true;
	}

	protected void rawBuildRecognizerTestFile(
		String parserName,
		String lexerName,
		String listenerName,
		String visitorName,
		String parserStartRuleName,
		boolean debug,
		boolean trace
	) {
		setParseErrors(null);
		if (parserName == null) {
			writeLexerTestFile(lexerName, false);
		} else {
			writeParserTestFile(
				parserName,
				lexerName,
				listenerName,
				visitorName,
				parserStartRuleName,
				debug,
				trace
			);
		}
	}

	public String execRecognizer() {
		return execModule("Test.php");
	}

	public String execModule(String fileName) {
		String phpPath = locatePhp();
		String runtimePath = locateRuntime();

		String modulePath = new File(getTempTestDir(), fileName).getAbsolutePath();
		String inputPath = new File(getTempTestDir(), "input").getAbsolutePath();
		Path outputPath = getTempTestDir().toPath().resolve("output").toAbsolutePath();

		try {
			ProcessBuilder builder = new ProcessBuilder(phpPath, modulePath, inputPath, outputPath.toString());
			builder.environment().put("RUNTIME", runtimePath);
			builder.directory(getTempTestDir());
			Process process = builder.start();
			StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
			StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
			stdoutVacuum.start();
			stderrVacuum.start();
			process.waitFor();
			stdoutVacuum.join();
			stderrVacuum.join();
			String output = stdoutVacuum.toString();

			if (output.length() == 0) {
				output = null;
			}

			if (stderrVacuum.toString().length() > 0) {
				setParseErrors(stderrVacuum.toString());
			}

			return output;
		} catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

	private String locateTool(String tool) {
		final String phpPath = System.getProperty("PHP_PATH");

		if (phpPath != null && new File(phpPath).exists()) {
			return phpPath;
		}

		String[] roots = {"/usr/local/bin/", "/opt/local/bin", "/opt/homebrew/bin/", "/usr/bin/"};

		for (String root: roots) {
			if (new File(root + tool).exists()) {
				return root + tool;
			}
		}

		throw new RuntimeException("Could not locate " + tool);
	}

	protected String locatePhp() {
		String propName = getPropertyPrefix() + "-php";
		String prop = System.getProperty(propName);

		if (prop == null || prop.length() == 0) {
			prop = locateTool("php");
		}

		File file = new File(prop);

		if (!file.exists()) {
			throw new RuntimeException("Missing system property:" + propName);
		}

		return file.getAbsolutePath();
	}

	protected String locateRuntime() {
		String propName = "antlr-php-runtime";
		String prop = System.getProperty(propName);

		if (prop == null || prop.length() == 0) {
			prop = "../runtime/PHP";
		}

		File file = new File(prop);

		if (!file.exists()) {
			throw new RuntimeException("Missing system property:" + propName);
		}

		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return file.getAbsolutePath();
		}
	}

	protected void mkdir(String dir) {
		File f = new File(dir);
		f.mkdirs();
	}

	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
			"\\<?php\n"
				+ "\n"
				+ "declare(strict_types=1);\n"
				+ "\n"
				+ "use Antlr\\Antlr4\\Runtime\\CommonTokenStream;\n"
				+ "use Antlr\\Antlr4\\Runtime\\Error\\Listeners\\ConsoleErrorListener;\n"
				+ "use Antlr\\Antlr4\\Runtime\\InputStream;\n"
				+ "use Antlr\\Antlr4\\Runtime\\Lexer;\n"
				+ "\n"
				+ "$runtime = \\getenv('RUNTIME');\n"
				+ "\n"
				+ "\\spl_autoload_register(function (string $class) use ($runtime) : void {\n"
				+ "    $file = \\str_replace('\\\\\\', \\DIRECTORY_SEPARATOR, \\str_replace('Antlr\\Antlr4\\Runtime\\\\\\', $runtime . '\\\\\\src\\\\\\', $class)) . '.php';\n"
				+ "\n"
				+ "    if (\\file_exists($file)) {\n"
				+ "        require_once $file;   \n"
				+ "    }\n"
				+ "});"
				+ "\n"
				+ "$input = InputStream::fromPath($argv[1]);\n"
				+ "$lexer = new <lexerName>($input);\n"
				+ "$lexer->addErrorListener(new ConsoleErrorListener());"
				+ "$tokens = new CommonTokenStream($lexer);\n"
				+ "$tokens->fill();\n"
				+ "\n"
				+ "foreach ($tokens->getAllTokens() as $token) {\n"
				+ "    echo $token . \\PHP_EOL;\n"
				+ "}"
				+ (showDFA
				? "echo $lexer->getInterpreter()->getDFA(Lexer::DEFAULT_MODE)->toLexerString();\n"
				: "")
		);

		outputFileST.add("lexerName", lexerName);

		writeFile(getTempDirPath(), "Test.php", outputFileST.render());
	}

	protected void writeParserTestFile(
		String parserName, String lexerName,
		String listenerName, String visitorName,
		String parserStartRuleName, boolean debug, boolean trace
	) {
		if (!parserStartRuleName.endsWith(")")) {
			parserStartRuleName += "()";
		}
		ST outputFileST = new ST(
			"\\<?php\n"
				+ "\n"
				+ "declare(strict_types=1);\n"
				+ "\n"
				+ "use Antlr\\Antlr4\\Runtime\\CommonTokenStream;\n"
				+ "use Antlr\\Antlr4\\Runtime\\Error\\Listeners\\DiagnosticErrorListener;\n"
				+ "use Antlr\\Antlr4\\Runtime\\Error\\Listeners\\ConsoleErrorListener;\n"
				+ "use Antlr\\Antlr4\\Runtime\\InputStream;\n"
				+ "use Antlr\\Antlr4\\Runtime\\ParserRuleContext;\n"
				+ "use Antlr\\Antlr4\\Runtime\\Tree\\ErrorNode;\n"
				+ "use Antlr\\Antlr4\\Runtime\\Tree\\ParseTreeListener;\n"
				+ "use Antlr\\Antlr4\\Runtime\\Tree\\ParseTreeWalker;\n"
				+ "use Antlr\\Antlr4\\Runtime\\Tree\\RuleNode;\n"
				+ "use Antlr\\Antlr4\\Runtime\\Tree\\TerminalNode;\n"
				+ "\n"
				+ "$runtime = \\getenv('RUNTIME');\n"
				+ "\n"
				+ "\\spl_autoload_register(function (string $class) use ($runtime) : void {\n"
				+ "    $file = \\str_replace('\\\\\\', \\DIRECTORY_SEPARATOR, \\str_replace('Antlr\\Antlr4\\Runtime\\\\\\', $runtime . '\\\\\\src\\\\\\', $class)) . '.php';\n"
				+ "\n"
				+ "    if (\\file_exists($file)) {\n"
				+ "        require_once $file;   \n"
				+ "    }\n"
				+ "});\n"
				+ "\n"
				+ "final class TreeShapeListener implements ParseTreeListener {\n"
				+ "    public function visitTerminal(TerminalNode $node) : void {}\n"
				+ "    public function visitErrorNode(ErrorNode $node) : void {}\n"
				+ "    public function exitEveryRule(ParserRuleContext $ctx) : void {}\n"
				+ "\n"
				+ "    public function enterEveryRule(ParserRuleContext $ctx) : void {\n"
				+ "        for ($i = 0, $count = $ctx->getChildCount(); $i \\< $count; $i++) {\n"
				+ "            $parent = $ctx->getChild($i)->getParent();\n"
				+ "\n"
				+ "            if (!($parent instanceof RuleNode) || $parent->getRuleContext() !== $ctx) {\n"
				+ "                throw new RuntimeException('Invalid parse tree shape detected.');\n"
				+ "            }\n"
				+ "        }\n"
				+ "    }\n"
				+ "}"
				+ "\n"
				+ "$input = InputStream::fromPath($argv[1]);\n"
				+ "$lexer = new <lexerName>($input);\n"
				+ "$lexer->addErrorListener(new ConsoleErrorListener());"
				+ "$tokens = new CommonTokenStream($lexer);\n"
				+ "<createParser>"
				+ "$parser->addErrorListener(new ConsoleErrorListener());"
				+ "$parser->setBuildParseTree(true);\n"
				+ "$tree = $parser-><parserStartRuleName>;\n\n"
				+ "ParseTreeWalker::default()->walk(new TreeShapeListener(), $tree);\n"
		);

		String stSource = "$parser = new <parserName>($tokens);\n";

		if (debug) {
			stSource += "$parser->addErrorListener(new DiagnosticErrorListener());\n";
		}

		if (trace) {
			stSource += "$parser->setTrace(true);\n";
		}

		ST createParserST = new ST(stSource);
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("listenerName", listenerName);
		outputFileST.add("visitorName", visitorName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);

		writeFile(getTempDirPath(), "Test.php", outputFileST.render());
	}

}
