/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.dart;

import org.antlr.v4.misc.Utils;
import org.antlr.v4.test.runtime.*;
import org.stringtemplate.v4.ST;

import java.io.*;
import java.net.URL;
import java.util.*;

import static junit.framework.TestCase.*;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.readFile;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;


public class BaseDartTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {

	private static String cacheDartPackages;
	private static String cacheDartPackageConfig;

	public String getPropertyPrefix() {
		return "antlr-dart";
	}

	@Override
	public String execLexer(String grammarFileName,
							String grammarStr,
							String lexerName,
							String input,
							boolean showDFA) {
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
			grammarStr,
			null,
			lexerName);
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		writeLexerTestFile(lexerName, showDFA);
		String output = execClass("Test", false);
		return output;
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
		return execParser(grammarFileName, grammarStr, parserName, lexerName,
			listenerName, visitorName, startRuleName, input, showDiagnosticErrors, false);
	}

	public String execParser(String grammarFileName,
							 String grammarStr,
							 String parserName,
							 String lexerName,
							 String listenerName,
							 String visitorName,
							 String startRuleName,
							 String input,
							 boolean showDiagnosticErrors,
							 boolean profile) {
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
			grammarStr,
			parserName,
			lexerName,
			"-visitor");
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		return rawExecRecognizer(parserName,
			lexerName,
			startRuleName,
			showDiagnosticErrors,
			profile,
			false);
	}

	/**
	 * Return true if all is well
	 */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													String parserName,
													String lexerName,
													String... extraOptions) {
		return rawGenerateAndBuildRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
	}

	/**
	 * Return true if all is well
	 */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													String parserName,
													String lexerName,
													boolean defaultListener,
													String... extraOptions) {
		ErrorQueue equeue =
			BaseRuntimeTest.antlrOnString(getTempDirPath(), "Dart", grammarFileName, grammarStr, defaultListener, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}

		List<String> files = new ArrayList<String>();
		if (lexerName != null) {
			files.add(lexerName + ".dart");
		}
		if (parserName != null) {
			files.add(parserName + ".dart");
			Set<String> optionsSet = new HashSet<String>(Arrays.asList(extraOptions));
			String grammarName = grammarFileName.substring(0, grammarFileName.lastIndexOf('.'));
			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarName + "Listener.dart");
				files.add(grammarName + "BaseListener.dart");
			}
			if (optionsSet.contains("-visitor")) {
				files.add(grammarName + "Visitor.dart");
				files.add(grammarName + "BaseVisitor.dart");
			}
		}

		String runtime = locateRuntime();
		writeFile(getTempDirPath(), "pubspec.yaml",
			"name: \"test\"\n" +
				"dependencies:\n" +
				"  antlr4:\n" +
				"    path: " + runtime + "\n" +
				"environment:\n" +
  				"  sdk: \">=2.12.0 <3.0.0\"\n");
		final File dartToolDir = new File(getTempDirPath(), ".dart_tool");
		if (cacheDartPackages == null) {
			try {
				final Process process =
					Runtime.getRuntime().exec(
						new String[]{locateDart(), "pub", "get"}, null, getTempTestDir());
				StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
				stderrVacuum.start();
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							process.destroy();
						} catch(Exception e) {
							e.printStackTrace(System.err);
						}
					}
				}, 30_000);
				process.waitFor();
				timer.cancel();
				stderrVacuum.join();
				String stderrDuringPubGet = stderrVacuum.toString();
				if (!stderrDuringPubGet.isEmpty()) {
					System.out.println("Pub Get error: " + stderrVacuum);
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				return false;
			}
			cacheDartPackages = readFile(getTempDirPath(), ".packages");
			cacheDartPackageConfig = readFile(dartToolDir.getAbsolutePath(), "package_config.json");
		} else {
			writeFile(getTempDirPath(), ".packages", cacheDartPackages);
			//noinspection ResultOfMethodCallIgnored
			dartToolDir.mkdir();
			writeFile(dartToolDir.getAbsolutePath(), "package_config.json", cacheDartPackageConfig);
		}
		return true; // allIsWell: no compile
	}

	protected String rawExecRecognizer(String parserName,
									   String lexerName,
									   String parserStartRuleName,
									   boolean debug,
									   boolean profile,
									   boolean aotCompile) {
		setParseErrors(null);
		if (parserName == null) {
			writeLexerTestFile(lexerName, false);
		}
		else {
			writeTestFile(parserName,
				lexerName,
				parserStartRuleName,
				debug,
				profile);
		}

		return execClass("Test", aotCompile);
	}

	public String execClass(String className, boolean compile) {
		try {
			if (compile) {
				String[] args = new String[]{
					locateDart(),
					"compile", "exe", className + ".dart", "-o", className
				};
				String cmdLine = Utils.join(args, " ");
				System.err.println("Compile: " + cmdLine);
				final Process process =
					Runtime.getRuntime().exec(args, null, getTempTestDir());
				StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
				stderrVacuum.start();
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							process.destroy();
						} catch(Exception e) {
							e.printStackTrace(System.err);
						}
					}
				}, 30_000);
				int result = process.waitFor();
				timer.cancel();
				if (result != 0) {
					stderrVacuum.join();
					System.err.print("Error compiling dart file: " + stderrVacuum);
				}
			}

			String[] args;
			if (compile) {
				args = new String[]{
					new File(getTempTestDir(), className).getAbsolutePath(), new File(getTempTestDir(), "input").getAbsolutePath()
				};
			} else {
				args = new String[]{
					locateDart(),
					className + ".dart", new File(getTempTestDir(), "input").getAbsolutePath()
				};
			}
			//String cmdLine = Utils.join(args, " ");
			//System.err.println("execParser: " + cmdLine);
			final Process process =
				Runtime.getRuntime().exec(args, null, getTempTestDir());
			StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
			StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
			stdoutVacuum.start();
			stderrVacuum.start();
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						process.destroy();
					} catch(Exception e) {
						e.printStackTrace(System.err);
					}
				}
			}, 30_000);
			process.waitFor();
			timer.cancel();
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
		final String dartPath = System.getProperty("DART_PATH");

		final String[] tools = isWindows()
				? new String[]{tool + ".exe", tool + ".bat", tool}
				: new String[]{tool};

		if (dartPath != null) {
			for (String t : tools) {
				if (new File(dartPath + t).exists()) {
					return dartPath + t;
				}
			}
		}

		final String[] roots = isWindows()
				? new String[]{"C:\\tools\\dart-sdk\\bin\\"}
				: new String[]{"/usr/local/bin/", "/opt/local/bin/", "/opt/homebrew/bin/", "/usr/bin/", "/usr/lib/dart/bin/", "/usr/local/opt/dart/libexec"};

		for (String root : roots) {
			for (String t : tools) {
				if (new File(root + t).exists()) {
					return root + t;
				}
			}
		}

		throw new RuntimeException("Could not locate " + tool);
	}

	protected String locateDart() {
		String propName = getPropertyPrefix() + "-dart";
		String prop = System.getProperty(propName);

		if (prop == null || prop.length() == 0) {
			prop = locateTool("dart");
		}

		File file = new File(prop);

		if (!file.exists()) {
			throw new RuntimeException("Missing system property:" + propName);
		}

		return file.getAbsolutePath();
	}

	private String locateRuntime() {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		final URL runtimeSrc = loader.getResource("Dart");
		if (runtimeSrc == null) {
			throw new RuntimeException("Cannot find Dart runtime");
		}
		if (isWindows()) {
			return runtimeSrc.getPath().replaceFirst("/", "");
		}
		return runtimeSrc.getPath();
	}

	protected void writeTestFile(String parserName,
								 String lexerName,
								 String parserStartRuleName,
								 boolean debug,
								 boolean profile) {
		ST outputFileST = new ST(
			"import 'package:antlr4/antlr4.dart';\n" +
				"\n" +
				"import '<lexerName>.dart';\n" +
				"import '<parserName>.dart';\n" +
				"\n" +
				"void main(List\\<String> args) async {\n" +
				"  CharStream input = await InputStream.fromPath(args[0]);\n" +
				"  final lex = <lexerName>(input);\n" +
				"  final tokens = CommonTokenStream(lex);\n" +
				"  <createParser>\n" +
				"  parser.buildParseTree = true;\n" +
				"  <profile>\n" +
				"  ParserRuleContext tree = parser.<parserStartRuleName>();\n" +
				"  <if(profile)>print('[${profiler.getDecisionInfo().join(', ')}]');<endif>\n" +
				"  ParseTreeWalker.DEFAULT.walk(TreeShapeListener(), tree);\n" +
				"}\n" +
				"\n" +
				"class TreeShapeListener implements ParseTreeListener {\n" +
				"  @override void visitTerminal(TerminalNode node) {}\n" +
				"\n" +
				"  @override void visitErrorNode(ErrorNode node) {}\n" +
				"\n" +
				"  @override void exitEveryRule(ParserRuleContext ctx) {}\n" +
				"\n" +
				"  @override\n" +
				"  void enterEveryRule(ParserRuleContext ctx) {\n" +
				"    for (var i = 0; i \\< ctx.childCount; i++) {\n" +
				"      final parent = ctx.getChild(i)?.parent;\n" +
				"      if (!(parent is RuleNode) || (parent as RuleNode).ruleContext != ctx) {\n" +
				"        throw StateError('Invalid parse tree shape detected.');\n" +
				"      }\n" +
				"    }\n" +
				"  }\n" +
				"}\n"
		);
		ST createParserST = new ST("final parser = <parserName>(tokens);\n");
		if (debug) {
			createParserST =
				new ST(
					"final parser = <parserName>(tokens);\n" +
						"  parser.addErrorListener(new DiagnosticErrorListener());\n");
		}
		if (profile) {
			outputFileST.add("profile",
				"ProfilingATNSimulator profiler = ProfilingATNSimulator(parser);\n" +
					"parser.setInterpreter(profiler);");
		} else {
			outputFileST.add("profile", new ArrayList<Object>());
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(getTempDirPath(), "Test.dart", outputFileST.render());
	}

	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
			"import 'dart:io';\n" +
				"\n" +
				"import 'package:antlr4/antlr4.dart';\n" +
				"\n" +
				"import '<lexerName>.dart';\n" +
				"\n" +
				"void main(List\\<String> args) async {\n" +
				"  CharStream input = await InputStream.fromPath(args[0]);\n" +
				"  <lexerName> lex = <lexerName>(input);\n" +
				"  CommonTokenStream tokens = CommonTokenStream(lex);\n" +
				"  tokens.fill();\n" +
				"  for (Object t in tokens.getTokens()!)\n" +
				"    print(t);\n" +
				"\n" +
				(showDFA ? "stdout.write(lex.interpreter!.getDFA(Lexer.DEFAULT_MODE).toLexerString());\n" : "") +
				"}\n"
		);

		outputFileST.add("lexerName", lexerName);
		writeFile(getTempDirPath(), "Test.dart", outputFileST.render());
	}

}
