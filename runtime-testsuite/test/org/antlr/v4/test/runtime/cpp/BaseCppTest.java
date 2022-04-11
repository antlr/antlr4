/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.cpp;

import org.antlr.v4.test.runtime.*;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.antlr.v4.test.runtime.BaseRuntimeTest.antlrOnString;
import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;
import static org.junit.Assert.assertTrue;

public class BaseCppTest extends BaseRuntimeTestSupport implements RuntimeTestSupport {

	protected String getPropertyPrefix() {
		return "antlr-" + getLanguage().toLowerCase();
	}

	protected String getLanguage() {
		return "Cpp";
	}

	protected String execLexer(String grammarFileName,
	                           String grammarStr,
	                           String lexerName,
	                           String input)
	{
		return execLexer(grammarFileName, grammarStr, lexerName, input, false);
	}

	@Override
	public  String execLexer(String grammarFileName,
	                         String grammarStr,
	                         String lexerName,
	                         String input,
	                         boolean showDFA)
	{
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
		                                                grammarStr,
		                                                null,
		                                                lexerName,"-no-listener");
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		writeLexerTestFile(lexerName, showDFA);
		String output = execModule("Test.cpp");
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
	                         boolean showDiagnosticErrors)
	{
		boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
		                                                grammarStr,
		                                                parserName,
		                                                lexerName,
		                                                "-visitor");
		assertTrue(success);
		writeFile(getTempDirPath(), "input", input);
		rawBuildRecognizerTestFile(parserName,
		                           lexerName,
		                           listenerName,
		                           visitorName,
		                           startRuleName,
		                           showDiagnosticErrors,
		                           false);
		return execRecognizer();
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
	                                                String grammarStr,
	                                                String parserName,
	                                                String lexerName,
	                                                String... extraOptions)
	{
		return rawGenerateAndBuildRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
	                                                String grammarStr,
	                                                String parserName,
	                                                String lexerName,
	                                                boolean defaultListener,
	                                                String... extraOptions)
	{
		ErrorQueue equeue =
			antlrOnString(getTempDirPath(), "Cpp", grammarFileName, grammarStr, defaultListener, extraOptions);
		if (!equeue.errors.isEmpty()) {
			return false;
		}

		List<String> files = new ArrayList<String>();
		if ( lexerName!=null ) {
			files.add(lexerName+".cpp");
			files.add(lexerName+".h");
		}
		if ( parserName!=null ) {
			files.add(parserName+".cpp");
			files.add(parserName+".h");
			Set<String> optionsSet = new HashSet<String>(Arrays.asList(extraOptions));
			if (!optionsSet.contains("-no-listener")) {
				files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.'))+"Listener.cpp");
				files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.'))+"Listener.h");
			}
			if (optionsSet.contains("-visitor")) {
				files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.'))+"Visitor.cpp");
				files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.'))+"Visitor.h");
			}
		}
		return true; // allIsWell: no compile
	}

	protected void rawBuildRecognizerTestFile(String parserName,
	                                          String lexerName,
	                                          String listenerName,
	                                          String visitorName,
	                                          String parserStartRuleName,
	                                          boolean debug,
	                                          boolean trace)
	{
		setParseErrors(null);
		if ( parserName==null ) {
			writeLexerTestFile(lexerName, false);
		}
		else {
			writeParserTestFile(parserName,
			                    lexerName,
			                    listenerName,
			                    visitorName,
			                    parserStartRuleName,
			                    debug, trace);
		}
	}

	public String execRecognizer() {
		return execModule("Test.cpp");
	}


	public List<String> allCppFiles(String path) {
		ArrayList<String> files = new ArrayList<String>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (File listOfFile : listOfFiles) {
			String file = listOfFile.getAbsolutePath();
			if (file.endsWith(".cpp")) {
				files.add(file);
			}
		}
		return files;
	}

	private String runProcess(ProcessBuilder builder, String description, boolean showStderr) throws Exception {
		// System.out.println("BUILDER: " + builder.command() + " @ " + builder.directory().toString());
		Process process = builder.start();
		StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
		StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
		stdoutVacuum.start();
		stderrVacuum.start();
		int errcode = process.waitFor();
		stdoutVacuum.join();
		stderrVacuum.join();
		String output = stdoutVacuum.toString();
		if ( stderrVacuum.toString().length()>0 ) {
			setParseErrors(stderrVacuum.toString());
			if ( showStderr ) System.err.println(getParseErrors());
		}
		if (errcode != 0) {
			String err = "execution of '"+description+"' failed with error code: "+errcode;
			if ( getParseErrors()!=null ) {
				setParseErrors(getParseErrors() + err);
			}
			else {
				setParseErrors(err);
			}
		}

		return output;
	}

	private String runCommand(String[] command, String workPath, String description, boolean showStderr) throws Exception {
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File(workPath));

		return runProcess(builder, description, showStderr);
	}

	// TODO: add a buildRuntimeOnWindows variant.
	private boolean buildRuntime() {
		String runtimePath = locateRuntime();
		System.out.println("Building ANTLR4 C++ runtime (if necessary) at "+ runtimePath);

		try {
			String[] command = { "cmake", ".", "-DCMAKE_BUILD_TYPE=Debug" };
			if (runCommand(command, runtimePath, "antlr runtime cmake", false) == null) {
				return false;
			}
		}
		catch (Exception e) {
			System.err.println("can't configure antlr cpp runtime cmake file");
		}

		try {
			String[] command = { "make", "-j", Integer.toString(Runtime.getRuntime().availableProcessors()) };
			if (runCommand(command, runtimePath, "building antlr runtime", true) == null)
				return false;
		}
		catch (Exception e) {
			System.err.println("can't compile antlr cpp runtime");
			e.printStackTrace(System.err);
			try {
			    String[] command = { "ls", "-la" };
					String output = runCommand(command, runtimePath + "/dist/", "printing library folder content", true);
				System.out.println(output);
			}
			catch (Exception e2) {
				System.err.println("can't even list folder content");
				e2.printStackTrace(System.err);
			}
		}

/* for debugging
		try {
		    String command[] = { "ls", "-la" };
				String output = runCommand(command, runtimePath + "/dist/", "printing library folder content");
			System.out.println(output);
		}
		catch (Exception e) {
			System.err.println("can't print folder content");
		}
*/

		return true;
	}

	static Boolean runtimeBuiltOnce = false;

	public String execModule(String fileName) {
		String runtimePath = locateRuntime();
		String includePath = runtimePath + "/runtime/src";
		String binPath = new File(getTempTestDir(), "a.out").getAbsolutePath();
		String inputPath = new File(getTempTestDir(), "input").getAbsolutePath();

		// Build runtime using cmake once per VM.
		synchronized (BaseCppTest.class) {
			if ( !runtimeBuiltOnce ) {
				try {
					String[] command = {"clang++", "--version"};
					String output = runCommand(command, getTempDirPath(), "printing compiler version", false);
					System.out.println("Compiler version is: "+output);
				}
				catch (Exception e) {
					System.err.println("Can't get compiler version");
				}

				runtimeBuiltOnce = true;
				if ( !buildRuntime() ) {
					System.out.println("C++ runtime build failed\n");
					return null;
				}
				System.out.println("C++ runtime build succeeded\n");
			}
		}

		// Create symlink to the runtime. Currently only used on OSX.
		String libExtension = (getOS().equals("mac")) ? "dylib" : "so";
		try {
			String[] command = { "ln", "-s", runtimePath + "/dist/libantlr4-runtime." + libExtension };
			if (runCommand(command, getTempDirPath(), "sym linking C++ runtime", true) == null)
				return null;
		}
		catch (Exception e) {
			System.err.println("can't create link to " + runtimePath + "/dist/libantlr4-runtime." + libExtension);
			e.printStackTrace(System.err);
			return null;
		}

		try {
			List<String> command2 = new ArrayList<String>(Arrays.asList("clang++", "-std=c++17", "-I", includePath, "-L.", "-lantlr4-runtime", "-pthread", "-o", "a.out"));
			command2.addAll(allCppFiles(getTempDirPath()));
			if (runCommand(command2.toArray(new String[0]), getTempDirPath(), "building test binary", true) == null) {
				return null;
			}
		}
		catch (Exception e) {
			System.err.println("can't compile test module: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}

		// Now run the newly minted binary. Reset the error output, as we could have got compiler warnings which are not relevant here.
		setParseErrors(null);
		try {
			ProcessBuilder builder = new ProcessBuilder(binPath, inputPath);
			builder.directory(getTempTestDir());
			Map<String, String> env = builder.environment();
			env.put("LD_PRELOAD", runtimePath + "/dist/libantlr4-runtime." + libExtension);
			String output = runProcess(builder, "running test binary", false);
			if ( output.length()==0 ) {
				output = null;
			}

      /* for debugging
		  System.out.println("=========================================================");
		  System.out.println(output);
		  System.out.println("=========================================================");
		  */
			return output;
		}
		catch (Exception e) {
			System.err.println("can't exec module: " + fileName);
			e.printStackTrace(System.err);
		}

		return null;
	}

	protected String locateRuntime() {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		final URL runtimeURL = loader.getResource("Cpp");
		if (runtimeURL == null) {
			throw new RuntimeException("Cannot find runtime");
		}
		// Windows not getting runtime right. See:
		// http://stackoverflow.com/questions/6164448/convert-url-to-normal-windows-filename-java
		// it was coming back "/C:/projects/antlr4-l7imv/runtime-testsuite/target/classes/Cpp"
		String p;
		try {
			p = Paths.get(runtimeURL.toURI()).toFile().toString();
		}
		catch (URISyntaxException use) {
			p = "Can't find runtime at " + runtimeURL;
		}
		return p;
	}

	protected void writeParserTestFile(String parserName, String lexerName,
	                                   String listenerName, String visitorName,
	                                   String parserStartRuleName, boolean debug, boolean trace) {
		if(!parserStartRuleName.endsWith(")"))
			parserStartRuleName += "()";
		ST outputFileST = new ST(
			"#include \\<iostream>\n"
				+ "\n"
				+ "#include \"antlr4-runtime.h\"\n"
				+ "#include \"<lexerName>.h\"\n"
				+ "#include \"<parserName>.h\"\n"
				+ "\n"
				+ "using namespace antlr4;\n"
				+ "\n"
				+ "class TreeShapeListener : public tree::ParseTreeListener {\n"
				+ "public:\n"
				+ "  void visitTerminal(tree::TerminalNode *) override {}\n"
				+ "  void visitErrorNode(tree::ErrorNode *) override {}\n"
				+ "  void exitEveryRule(ParserRuleContext *) override {}\n"
				+ "  void enterEveryRule(ParserRuleContext *ctx) override {\n"
				+ "    for (auto child : ctx->children) {\n"
				+ "      tree::ParseTree *parent = child->parent;\n"
				+ "      ParserRuleContext *rule = dynamic_cast\\<ParserRuleContext *>(parent);\n"
				+ "      if (rule != ctx) {\n"
				+ "        throw \"Invalid parse tree shape detected.\";\n"
				+ "      }\n"

				+ "    }\n"
				+ "  }\n"
				+ "};\n"
				+ "\n"
				+ "\n"
				+ "int main(int argc, const char* argv[]) {\n"
				+ "  ANTLRFileStream input;\n"
				+ "  input.loadFromFile(argv[1]);\n"
				+ "  <lexerName> lexer(&input);\n"
				+ "  CommonTokenStream tokens(&lexer);\n"
				+ "<createParser>"
				+ "\n"
				+ "  tree::ParseTree *tree = parser.<parserStartRuleName>;\n"
				+ "  TreeShapeListener listener;\n"
				+ "  tree::ParseTreeWalker::DEFAULT.walk(&listener, tree);\n"
				+ "\n"
				+ "  return 0;\n"
				+ "}\n"
		);

		String stSource = "  <parserName> parser(&tokens);\n";
		if(debug) {
			stSource += "  DiagnosticErrorListener errorListener;\n";
			stSource += "  parser.addErrorListener(&errorListener);\n";
		}
		if(trace)
			stSource += "  parser.setTrace(true);\n";
		ST createParserST = new ST(stSource);
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("listenerName", listenerName);
		outputFileST.add("visitorName", visitorName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(getTempDirPath(), "Test.cpp", outputFileST.render());
	}

	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
			"#include \\<iostream>\n"
				+ "\n"
				+ "#include \"antlr4-runtime.h\"\n"
				+ "#include \"<lexerName>.h\"\n"
				+ "\n"
				+ "#include \"support/StringUtils.h\"\n"
				+ "\n"
				+ "using namespace antlr4;\n"
				+ "\n"
				+ "int main(int argc, const char* argv[]) {\n"
				+ "  ANTLRFileStream input;\n"
				+ "  input.loadFromFile(argv[1]);\n"
				+ "  <lexerName> lexer(&input);\n"
				+ "  CommonTokenStream tokens(&lexer);\n"
				+ "  tokens.fill();\n"
				+ "  for (auto token : tokens.getTokens())\n"
				+ "    std::cout \\<\\< token->toString() \\<\\< std::endl;\n"
				+ (showDFA ? "  std::cout \\<\\< lexer.getInterpreter\\<atn::LexerATNSimulator>()->getDFA(Lexer::DEFAULT_MODE).toLexerString();\n" : "\n")
				+ "  return 0;\n"
				+ "}\n");
		outputFileST.add("lexerName", lexerName);
		writeFile(getTempDirPath(), "Test.cpp", outputFileST.render());
	}

}

