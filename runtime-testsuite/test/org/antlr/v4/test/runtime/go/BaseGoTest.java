/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.go;


import org.antlr.v4.test.runtime.*;
import org.junit.Assert;
import org.stringtemplate.v4.ST;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import static junit.framework.TestCase.*;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.fail;

public class BaseGoTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {
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
		replaceImportPath();
		writeFile(getTempDirPath(), "input", input);
		writeLexerTestFile(lexerName, showDFA);
		writeGoModFile();
		return execModule("Test.go");
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
		replaceImportPath();
		writeFile(getTempDirPath(), "input", input);
		writeGoModFile();
		rawBuildRecognizerTestFile(parserName, lexerName, listenerName,
		                           visitorName, startRuleName, showDiagnosticErrors);
		return execModule("Test.go");
	}

	private void writeGoModFile() {
		if (goModContent == null) {
			try {
				ProcessBuilder pb = new ProcessBuilder("go", "mod", "init", "test");
				pb.directory(getTempTestDir());
				pb.redirectErrorStream(true);
				Process process = pb.start();
				StreamVacuum sucker = new StreamVacuum(process.getInputStream());
				sucker.start();
				int exit = process.waitFor();
				sucker.join();
				if (exit != 0) {
					throw new Exception("Non-zero exit while setting up go module: " + sucker);
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
		ErrorQueue equeue = antlrOnString(getTempParserDirPath(), "Go", grammarFileName, grammarStr,
		                                  defaultListener, extraOptions);
		return equeue.errors.isEmpty();
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

	private String execModule(String fileName) {
		initializeRuntime();

		String modulePath = new File(getTempTestDir(), fileName).getAbsolutePath();
		String inputPath = new File(getTempTestDir(), "input").getAbsolutePath();
		try {
			ProcessBuilder builder = new ProcessBuilder("go", "run", modulePath, inputPath);
			builder.directory(getTempTestDir());
			builder.environment().put("GOROOT", newGoRootString);
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
		File[] runtimeFiles = locateRuntime().listFiles(new GoFileFilter());
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
			ProcessBuilder pb = new ProcessBuilder("go", "env", "GOROOT");
			Process process = pb.start();
			StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
			stdoutVacuum.start();
			process.waitFor();
			stdoutVacuum.join();
			return stdoutVacuum.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Unable to execute go env");
		}
		return null;
	}

	private static File locateRuntime() {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		final URL runtimeSrc = loader.getResource("Go");
		if ( runtimeSrc==null ) {
			throw new RuntimeException("Cannot find Go ANTLR runtime");
		}
		File runtimeDir = new File(runtimeSrc.getPath(), "antlr");
		if (!runtimeDir.exists()) {
			throw new RuntimeException("Cannot find Go ANTLR runtime");
		}
		return new File(runtimeDir.getPath());
	}

	protected void writeParserTestFile(String parserName, String lexerName,
	                                   String listenerName, String visitorName,
	                                   String parserStartRuleName, boolean debug) {
		ST outputFileST = new ST(
			"package main\n" +
				"import (\n"
				+ "	\"test/parser\"\n"
				+ "	\"" + antlrTestPackageName + "\"\n"
				+ "	\"fmt\"\n"
				+ "	\"os\"\n"
				+ ")\n"
				+ "\n"
				+ "type TreeShapeListener struct {\n"
				+ "	*parser.Base<listenerName>\n"
				+ "}\n"
				+ "\n"
				+ "func NewTreeShapeListener() *TreeShapeListener {\n"
				+ "	return new(TreeShapeListener)\n"
				+ "}\n"
				+ "\n"
				+ "func (this *TreeShapeListener) EnterEveryRule(ctx antlr.ParserRuleContext) {\n"
				+ "	for i := 0; i\\<ctx.GetChildCount(); i++ {\n"
				+ "		child := ctx.GetChild(i)\n"
				+ "		parentR,ok := child.GetParent().(antlr.RuleNode)\n"
				+ "		if !ok || parentR.GetBaseRuleContext() != ctx.GetBaseRuleContext() {\n"
				+ "			panic(\"Invalid parse tree shape detected.\")\n"
				+ "		}\n"
				+ "	}\n"
				+ "}\n"
				+ "\n"
				+ "func main() {\n"
				+ "	input, err := antlr.NewFileStream(os.Args[1])\n"
				+ "     if err != nil {\n"
				+ "     	fmt.Printf(\"Failed to find file: %v\", err)\n"
				+ "     	return\n"
				+ "     }\n"
				+ "	lexer := parser.New<lexerName>(input)\n"
				+ "	stream := antlr.NewCommonTokenStream(lexer,0)\n"
				+ "<createParser>"
				+ "	p.BuildParseTrees = true\n"
				+ "	tree := p.<parserStartRuleName>()\n"
				+ "	antlr.ParseTreeWalkerDefault.Walk(NewTreeShapeListener(), tree)\n"
				+ "}\n");

		ST createParserST = new ST(
			"	p := parser.New<parserName>(stream)\n");
		if (debug) {
			createParserST = new ST(
				"	p := parser.New<parserName>(stream)\n"
					+ "	p.AddErrorListener(antlr.NewDiagnosticErrorListener(true))\n");
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("listenerName", listenerName);
		outputFileST.add("visitorName", visitorName);
		outputFileST.add("parserStartRuleName", parserStartRuleName.substring(0, 1).toUpperCase() + parserStartRuleName.substring(1) );
		writeFile(getTempDirPath(), "Test.go", outputFileST.render());
	}

	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
			"package main\n" +
				"import (\n"
				+ "	\"test/parser\"\n"
				+ "	\"" + antlrTestPackageName + "\"\n"
				+ "	\"os\"\n"
				+ "	\"fmt\"\n"
				+ ")\n"
				+ "\n"
				+ "func main() {\n"
				+ "	input, err := antlr.NewFileStream(os.Args[1])\n"
				+ "     if err != nil {\n"
				+ "     	fmt.Printf(\"Failed to find file: %v\", err)\n"
				+ "     	return\n"
				+ "     }\n"
				+ "	lexer := parser.New<lexerName>(input)\n"
				+ "	stream := antlr.NewCommonTokenStream(lexer,0)\n"
				+ "	stream.Fill()\n"
				+ "	for _, t := range stream.GetAllTokens() {\n"
				+ "		fmt.Println(t)\n"
				+ "	}\n"
				+ (showDFA ? "fmt.Print(lexer.GetInterpreter().DecisionToDFA()[antlr.LexerDefaultMode].ToLexerString())\n"
				: "")
				+ "}\n"
				+ "\n");
		outputFileST.add("lexerName", lexerName);
		writeFile(getTempDirPath(), "Test.go", outputFileST.render());
	}
}
