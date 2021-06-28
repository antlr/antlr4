/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.go;


import org.antlr.v4.test.runtime.*;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.*;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;

public class BaseGoTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {

	private static final String GO_RUNTIME_IMPORT_PATH = "github.com/antlr/antlr4/runtime/Go/antlr"; // TODO: Change this before merging with upstream

	private File parserTempDir; // "parser" with tempDir

	@Override
	protected String getPropertyPrefix() {
		return "antlr4-go";
	}

	public static void groupSetUp() throws Exception { }
	public static void groupTearDown() throws Exception { }

    private void setupAntlrRuntime() throws Exception {
        File packageDir = new File(getTempParserDir(), "antlr");
        if (!packageDir.mkdirs()) {
            throw new Exception("Cannot make directory for runtime");
        }
		File[] runtimeFiles = locateRuntime().listFiles(new GoFileFilter());
		if (runtimeFiles == null) {
			throw new Exception("Go runtime file list is empty.");
		}
		for (File runtimeFile : runtimeFiles) {
            File dest = new File(packageDir, runtimeFile.getName());

            RuntimeTestUtils.copyFile(runtimeFile, dest);
		}
    }

    private void setupGoMod() throws Exception {
        String goExecutable = locateGo();
        ProcessBuilder pb = new ProcessBuilder(goExecutable, "mod", "init", "antlr.org/test");
        pb.directory(getTempTestDir());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        StreamVacuum sucker = new StreamVacuum(process.getInputStream());
        sucker.start();
        int exit = process.waitFor();
        sucker.join();
        if (exit != 0) {
            throw new Exception("Non-zero exit while setting up go module: " + sucker.toString());
        }
    }

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
		writeFile(getTempDirPath(), "input", input);
		writeLexerTestFile(lexerName, showDFA);
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
		writeFile(getTempDirPath(), "input", input);
		rawBuildRecognizerTestFile(parserName, lexerName, listenerName,
		                           visitorName, startRuleName, showDiagnosticErrors);
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
		ErrorQueue equeue = antlrOnString(getTempParserDirPath(), "Go", grammarFileName, grammarStr,
		                                  defaultListener, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}
		return true;
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
		return execModule("Test.go");
	}

	public String execModule(String fileName) {
		String goExecutable = locateGo();
		String modulePath = new File(getTempTestDir(), fileName).getAbsolutePath();
		String inputPath = new File(getTempTestDir(), "input").getAbsolutePath();
		try {
			ProcessBuilder builder = new ProcessBuilder(goExecutable, "run", modulePath, inputPath);
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
			if ( output.length()==0 ) {
				output = null;
			}
			if (stderrVacuum.toString().length() > 0) {
				setParseErrors(stderrVacuum.toString());
			}
			return output;
		}
		catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

	private static String locateTool(String tool) {
		ArrayList<String> paths = new ArrayList<String>(); // default cap is about right

		// GOROOT should have priority if set
		String goroot = System.getenv("GOROOT");
		if (goroot != null) {
			paths.add(goroot + File.separatorChar + "bin");
		}

		String pathEnv = System.getenv("PATH");
		if (pathEnv != null) {
			paths.addAll(Arrays.asList(pathEnv.split(File.pathSeparator)));
		}

		// OS specific default locations of binary dist as last resort
		paths.add("/usr/local/go/bin");
		paths.add("c:\\Go\\bin");

		for (String path : paths) {
			File candidate = new File(new File(path), tool);
			if (candidate.exists()) {
				return candidate.getPath();
			}
			candidate = new File(new File(path), tool+".exe");
			if (candidate.exists()) {
				return candidate.getPath();
			}
		}
		return null;
	}

	private static String locateGo() {
		String propName = "antlr-go";
		String prop = System.getProperty(propName);
		if (prop == null || prop.length() == 0) {
			prop = locateTool("go");
		}
		if (prop == null) {
			throw new RuntimeException("Missing system property:" + propName);
		}
		return prop;
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
		return runtimeDir;
	}

    private void replaceImportPath() throws Exception {
        File[] files = getTempParserDir().listFiles(new GoFileFilter());
        for (File file: files) {
            File temp = File.createTempFile(
                file.getName(),
                ".bak",
                file.getParentFile()
            );
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
            String s = "";

            while ((s = br.readLine()) != null) {
                bw.write(s.replace(GO_RUNTIME_IMPORT_PATH, "antlr.org/test/parser/antlr"));
                bw.newLine();
            }

            br.close();
            bw.close();

            file.delete();
            temp.renameTo(file);
        }
    }

	protected void writeParserTestFile(String parserName, String lexerName,
	                                   String listenerName, String visitorName,
	                                   String parserStartRuleName, boolean debug) {
		ST outputFileST = new ST(
			"package main\n" +
				"import (\n"
				+ "	\"antlr.org/test/parser\"\n"
				+ "	\"antlr.org/test/parser/antlr\"\n"
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
        try {
            setupGoMod();
            setupAntlrRuntime();
            replaceImportPath();
        } catch (Exception e) {
            //
        }
		writeFile(getTempDirPath(), "Test.go", outputFileST.render());
	}



	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
			"package main\n" +
				"import (\n"
				+ "	\"antlr.org/test/parser\"\n"
				+ "	\"antlr.org/test/parser/antlr\"\n"
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
        try {
            setupGoMod();
            setupAntlrRuntime();
            replaceImportPath();
        } catch (Exception e) {
            //
        }
		writeFile(getTempDirPath(), "Test.go", outputFileST.render());
	}

}
