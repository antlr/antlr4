/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.test;


import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.antlr.v4.Tool;
import org.antlr.v4.tool.ANTLRErrorListener;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Message;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.stringtemplate.v4.ST;

import java.io.*;
import java.util.*;


public abstract class BaseTest {
	public static final String jikes = null;//"/usr/bin/jikes";
	public static final String pathSep = System.getProperty("path.separator");
    
   /**
    * When runnning from Maven, the junit tests are run via the surefire plugin. It sets the
    * classpath for the test environment into the following property. We need to pick this up
    * for the junit tests that are going to generate and try to run code.
    */
    public static final String SUREFIRE_CLASSPATH = System.getProperty("surefire.test.class.path", "");

    /**
     * Build up the full classpath we need, including the surefire path (if present)
     */
    public static final String CLASSPATH = System.getProperty("java.class.path") + (SUREFIRE_CLASSPATH.equals("") ? "" : pathSep + SUREFIRE_CLASSPATH); 
    
	public String tmpdir = null;

    /** reset during setUp and set to true if we find a problem */  
    protected boolean lastTestFailed = false;

	/** If error during parser execution, store stderr here; can't return
     *  stdout and stderr.  This doesn't trap errors from running antlr.
     */
	protected String stderrDuringParse;

    @Before
	public void setUp() throws Exception {
        lastTestFailed = false; // hope for the best, but set to true in asserts that fail
        // new output dir for each test
        tmpdir = new File(System.getProperty("java.io.tmpdir"), "antlr-"+getClass().getName()+"-"+System.currentTimeMillis()).getAbsolutePath();
        ErrorManager.resetErrorState();
    }

    @After
    public void tearDown() throws Exception {
        // remove tmpdir if no error.
        if ( !lastTestFailed ) eraseTempDir();

    }

    protected org.antlr.v4.Tool newTool(String[] args) {
		Tool tool = new Tool(args);
		tool.setOutputDirectory(tmpdir);
		return tool;
	}

	protected Tool newTool() {
		org.antlr.v4.Tool tool = new Tool();
		tool.setOutputDirectory(tmpdir);
		return tool;
	}

	protected boolean compile(String fileName) {
		String compiler = "javac";
		String classpathOption = "-classpath";

		if (jikes!=null) {
			compiler = jikes;
			classpathOption = "-bootclasspath";
		}

		String[] args = new String[] {
					compiler, "-d", tmpdir,
					classpathOption, tmpdir+pathSep+CLASSPATH,
					tmpdir+"/"+fileName
		};
		String cmdLine = compiler+" -d "+tmpdir+" "+classpathOption+" "+tmpdir+pathSep+CLASSPATH+" "+fileName;
		//System.out.println("compile: "+cmdLine);
		File outputDir = new File(tmpdir);
		try {
			Process process =
				Runtime.getRuntime().exec(args, null, outputDir);
			StreamVacuum stdout = new StreamVacuum(process.getInputStream());
			StreamVacuum stderr = new StreamVacuum(process.getErrorStream());
			stdout.start();
			stderr.start();
			process.waitFor();
            stdout.join();
            stderr.join();
			if ( stdout.toString().length()>0 ) {
				System.err.println("compile stdout from: "+cmdLine);
				System.err.println(stdout);
			}
			if ( stderr.toString().length()>0 ) {
				System.err.println("compile stderr from: "+cmdLine);
				System.err.println(stderr);
			}
			int ret = process.exitValue();
			return ret==0;
		}
		catch (Exception e) {
			System.err.println("can't exec compilation");
			e.printStackTrace(System.err);
			return false;
		}
	}

	/** Return true if all is ok, no errors */
	protected boolean antlr(String fileName, String grammarFileName, String grammarStr, boolean debug) {
		boolean allIsWell = true;
		mkdir(tmpdir);
		writeFile(tmpdir, fileName, grammarStr);
		try {
			final List options = new ArrayList();
			if ( debug ) {
				options.add("-debug");
			}
			options.add("-o");
			options.add(tmpdir);
			options.add("-lib");
			options.add(tmpdir);
			options.add(new File(tmpdir,grammarFileName).toString());
			final String[] optionsA = new String[options.size()];
			options.toArray(optionsA);
			/*
			final ErrorQueue equeue = new ErrorQueue();
			ErrorManager.setErrorListener(equeue);
			*/
			Tool antlr = newTool(optionsA);
			antlr.processGrammarsOnCommandLine();
			ANTLRErrorListener listener = ErrorManager.getErrorListener();
			if ( listener instanceof ErrorQueue ) {
				ErrorQueue equeue = (ErrorQueue)listener;
				if ( equeue.errors.size()>0 ) {
					allIsWell = false;
					System.err.println("antlr reports errors from "+options);
					for (int i = 0; i < equeue.errors.size(); i++) {
						Message msg = (Message) equeue.errors.get(i);
						System.err.println(msg);
					}
                    System.out.println("!!!\ngrammar:");
                    System.out.println(grammarStr);
                    System.out.println("###");
                }
			}
		}
		catch (Exception e) {
			allIsWell = false;
			System.err.println("problems building grammar: "+e);
			e.printStackTrace(System.err);
		}
		return allIsWell;
	}

	protected String execLexer(String grammarFileName,
							   String grammarStr,
							   String lexerName,
							   String input,
							   boolean debug)
	{
		rawGenerateAndBuildRecognizer(grammarFileName,
									  grammarStr,
									  null,
									  lexerName,
									  debug);
		writeFile(tmpdir, "input", input);
		return rawExecRecognizer(null,
								 null,
								 lexerName,
								 null,
								 null,
								 false,
								 false,
								 false,
								 debug);
	}

	protected String execParser(String grammarFileName,
								String grammarStr,
								String parserName,
								String lexerName,
								String startRuleName,
								String input, boolean debug)
	{
		rawGenerateAndBuildRecognizer(grammarFileName,
									  grammarStr,
									  parserName,
									  lexerName,
									  debug);
		writeFile(tmpdir, "input", input);
		boolean parserBuildsTrees =
			grammarStr.indexOf("output=AST")>=0 ||
			grammarStr.indexOf("output = AST")>=0;
		boolean parserBuildsTemplate =
			grammarStr.indexOf("output=template")>=0 ||
			grammarStr.indexOf("output = template")>=0;
		return rawExecRecognizer(parserName,
								 null,
								 lexerName,
								 startRuleName,
								 null,
								 parserBuildsTrees,
								 parserBuildsTemplate,
								 false,
								 debug);
	}

	protected String execTreeParser(String parserGrammarFileName,
									String parserGrammarStr,
									String parserName,
									String treeParserGrammarFileName,
									String treeParserGrammarStr,
									String treeParserName,
									String lexerName,
									String parserStartRuleName,
									String treeParserStartRuleName,
									String input)
	{
		return execTreeParser(parserGrammarFileName,
							  parserGrammarStr,
							  parserName,
							  treeParserGrammarFileName,
							  treeParserGrammarStr,
							  treeParserName,
							  lexerName,
							  parserStartRuleName,
							  treeParserStartRuleName,
							  input,
							  false);
	}

	protected String execTreeParser(String parserGrammarFileName,
									String parserGrammarStr,
									String parserName,
									String treeParserGrammarFileName,
									String treeParserGrammarStr,
									String treeParserName,
									String lexerName,
									String parserStartRuleName,
									String treeParserStartRuleName,
									String input,
									boolean debug)
	{
		// build the parser
		rawGenerateAndBuildRecognizer(parserGrammarFileName,
									  parserGrammarStr,
									  parserName,
									  lexerName,
									  debug);

		// build the tree parser
		rawGenerateAndBuildRecognizer(treeParserGrammarFileName,
									  treeParserGrammarStr,
									  treeParserName,
									  lexerName,
									  debug);

		writeFile(tmpdir, "input", input);

		boolean parserBuildsTrees =
			parserGrammarStr.indexOf("output=AST")>=0 ||
			parserGrammarStr.indexOf("output = AST")>=0;
		boolean treeParserBuildsTrees =
			treeParserGrammarStr.indexOf("output=AST")>=0 ||
			treeParserGrammarStr.indexOf("output = AST")>=0;
		boolean parserBuildsTemplate =
			parserGrammarStr.indexOf("output=template")>=0 ||
			parserGrammarStr.indexOf("output = template")>=0;

		return rawExecRecognizer(parserName,
								 treeParserName,
								 lexerName,
								 parserStartRuleName,
								 treeParserStartRuleName,
								 parserBuildsTrees,
								 parserBuildsTemplate,
								 treeParserBuildsTrees,
								 debug);
	}

	/** Return true if all is well */
	protected boolean rawGenerateAndBuildRecognizer(String grammarFileName,
													String grammarStr,
													String parserName,
													String lexerName,
													boolean debug)
	{
		boolean allIsWell =
			antlr(grammarFileName, grammarFileName, grammarStr, debug);
		if ( lexerName!=null ) {
			boolean ok;
			if ( parserName!=null ) {
				ok = compile(parserName+".java");
				if ( !ok ) { allIsWell = false; }
			}
			ok = compile(lexerName+".java");
			if ( !ok ) { allIsWell = false; }
		}
		else {
			boolean ok = compile(parserName+".java");
			if ( !ok ) { allIsWell = false; }
		}
		return allIsWell;
	}

	protected String rawExecRecognizer(String parserName,
									   String treeParserName,
									   String lexerName,
									   String parserStartRuleName,
									   String treeParserStartRuleName,
									   boolean parserBuildsTrees,
									   boolean parserBuildsTemplate,
									   boolean treeParserBuildsTrees,
									   boolean debug)
	{
        this.stderrDuringParse = null;
		if ( treeParserBuildsTrees && parserBuildsTrees ) {
			writeTreeAndTreeTestFile(parserName,
									 treeParserName,
									 lexerName,
									 parserStartRuleName,
									 treeParserStartRuleName,
									 debug);
		}
		else if ( parserBuildsTrees ) {
			writeTreeTestFile(parserName,
							  treeParserName,
							  lexerName,
							  parserStartRuleName,
							  treeParserStartRuleName,
							  debug);
		}
		else if ( parserBuildsTemplate ) {
			writeTemplateTestFile(parserName,
								  lexerName,
								  parserStartRuleName,
								  debug);
		}
		else if ( parserName==null ) {
			writeLexerTestFile(lexerName, debug);
		}
		else {
			writeTestFile(parserName,
						  lexerName,
						  parserStartRuleName,
						  debug);
		}

		compile("Test.java");
		try {
			String[] args = new String[] {
				"java", "-classpath", tmpdir+pathSep+CLASSPATH,
				"Test", new File(tmpdir, "input").getAbsolutePath()
			};
			//String cmdLine = "java -classpath "+CLASSPATH+pathSep+tmpdir+" Test " + new File(tmpdir, "input").getAbsolutePath();
			//System.out.println("execParser: "+cmdLine);
			Process process =
				Runtime.getRuntime().exec(args, null, new File(tmpdir));
			StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
			StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
			stdoutVacuum.start();
			stderrVacuum.start();
			process.waitFor();
			stdoutVacuum.join();
			stderrVacuum.join();
			String output = null;
			output = stdoutVacuum.toString();
			if ( stderrVacuum.toString().length()>0 ) {
				this.stderrDuringParse = stderrVacuum.toString();
				//System.err.println("exec stderrVacuum: "+ stderrVacuum);
			}
			return output;
		}
		catch (Exception e) {
			System.err.println("can't exec recognizer");
			e.printStackTrace(System.err);
		}
		return null;
	}

    public void testErrors(String[] pairs, boolean printTree) {
        for (int i = 0; i < pairs.length; i+=2) {
            String input = pairs[i];
            String expect = pairs[i+1];
            ErrorQueue equeue = new ErrorQueue();
            ErrorManager.setErrorListener(equeue);
            try {
                String[] lines = input.split("\n");
				String fileName = getFilenameFromFirstLineOfGrammar(lines[0]);
                Grammar g = new Grammar(fileName, input);
                g.loadImportedGrammars();
				if ( printTree ) {
					if ( g.ast!=null ) System.out.println(g.ast.toStringTree());
					else System.out.println("null tree");
				}
            }
            catch (RecognitionException re) {
                re.printStackTrace(System.err);
            }
            String actual = equeue.toString();
			String msg = input;
			msg = msg.replaceAll("\n","\\\\n");
			msg = msg.replaceAll("\r","\\\\r");
			msg = msg.replaceAll("\t","\\\\t");
            assertEquals("error in: "+msg,expect,actual);
        }
    }

	public String getFilenameFromFirstLineOfGrammar(String line) {
		String fileName = "<string>";
		int grIndex = line.lastIndexOf("grammar");
		int semi = line.lastIndexOf(';');
		if ( grIndex>=0 && semi>=0 ) {
			int space = line.indexOf(' ', grIndex);
			fileName = line.substring(space+1, semi)+".g";
		}
		if ( fileName.length()==".g".length() ) fileName = "<string>";
		return fileName;
	}

	public static class StreamVacuum implements Runnable {
		StringBuffer buf = new StringBuffer();
		BufferedReader in;
		Thread sucker;
		public StreamVacuum(InputStream in) {
			this.in = new BufferedReader( new InputStreamReader(in) );
		}
		public void start() {
			sucker = new Thread(this);
			sucker.start();
		}
		public void run() {
			try {
				String line = in.readLine();
				while (line!=null) {
					buf.append(line);
					buf.append('\n');
					line = in.readLine();
				}
			}
			catch (IOException ioe) {
				System.err.println("can't read output from process");
			}
		}
		/** wait for the thread to finish */
		public void join() throws InterruptedException {
			sucker.join();
		}
		public String toString() {
			return buf.toString();
		}
	}

    public static class FilteringTokenStream extends CommonTokenStream {
        public FilteringTokenStream(TokenSource src) { super(src); }
        Set<Integer> hide = new HashSet<Integer>();
        protected void sync(int i) {
            super.sync(i);
            if ( hide.contains(get(i).getType()) ) get(i).setChannel(Token.HIDDEN_CHANNEL);
        }
        public void setTokenTypeChannel(int ttype, int channel) {
            hide.add(ttype);
        }
    }

	protected void writeFile(String dir, String fileName, String content) {
		try {
			File f = new File(dir, fileName);
			FileWriter w = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(w);
			bw.write(content);
			bw.close();
			w.close();
		}
		catch (IOException ioe) {
			System.err.println("can't write file");
			ioe.printStackTrace(System.err);
		}
	}

	protected void mkdir(String dir) {
		File f = new File(dir);
		f.mkdirs();
	}

	protected void writeTestFile(String parserName,
								 String lexerName,
								 String parserStartRuleName,
								 boolean debug)
	{
		ST outputFileST = new ST(
			"import org.antlr.runtime.*;\n" +
			"import org.antlr.runtime.tree.*;\n" +
			"import org.antlr.runtime.debug.*;\n" +
			"\n" +
			"class Profiler2 extends Profiler {\n" +
			"    public void terminate() { ; }\n" +
			"}\n"+
			"public class Test {\n" +
			"    public static void main(String[] args) throws Exception {\n" +
			"        CharStream input = new ANTLRFileStream(args[0]);\n" +
			"        $lexerName$ lex = new $lexerName$(input);\n" +
			"        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
			"        $createParser$\n"+
			"        parser.$parserStartRuleName$();\n" +
			"    }\n" +
			"}"
			);
		ST createParserST =
			new ST(
			"        Profiler2 profiler = new Profiler2();\n"+
			"        $parserName$ parser = new $parserName$(tokens,profiler);\n" +
			"        profiler.setParser(parser);\n");
		if ( !debug ) {
			createParserST =
				new ST(
				"        $parserName$ parser = new $parserName$(tokens);\n");
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(tmpdir, "Test.java", outputFileST.toString());
	}

	protected void writeLexerTestFile(String lexerName, boolean debug) {
		ST outputFileST = new ST(
			"import org.antlr.runtime.*;\n" +
			"import org.antlr.runtime.tree.*;\n" +
			"import org.antlr.runtime.debug.*;\n" +
			"\n" +
			"class Profiler2 extends Profiler {\n" +
			"    public void terminate() { ; }\n" +
			"}\n"+
			"public class Test {\n" +
			"    public static void main(String[] args) throws Exception {\n" +
			"        CharStream input = new ANTLRFileStream(args[0]);\n" +
			"        $lexerName$ lex = new $lexerName$(input);\n" +
			"        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
			"        System.out.println(tokens);\n" +
			"    }\n" +
			"}"
			);
		outputFileST.add("lexerName", lexerName);
		writeFile(tmpdir, "Test.java", outputFileST.toString());
	}

	protected void writeTreeTestFile(String parserName,
									 String treeParserName,
									 String lexerName,
									 String parserStartRuleName,
									 String treeParserStartRuleName,
									 boolean debug)
	{
		ST outputFileST = new ST(
			"import org.antlr.runtime.*;\n" +
			"import org.antlr.runtime.tree.*;\n" +
			"import org.antlr.runtime.debug.*;\n" +
			"\n" +
			"class Profiler2 extends Profiler {\n" +
			"    public void terminate() { ; }\n" +
			"}\n"+
			"public class Test {\n" +
			"    public static void main(String[] args) throws Exception {\n" +
			"        CharStream input = new ANTLRFileStream(args[0]);\n" +
			"        $lexerName$ lex = new $lexerName$(input);\n" +
			"        TokenRewriteStream tokens = new TokenRewriteStream(lex);\n" +
			"        $createParser$\n"+
			"        $parserName$.$parserStartRuleName$_return r = parser.$parserStartRuleName$();\n" +
			"        $if(!treeParserStartRuleName)$\n" +
			"        if ( r.tree!=null ) {\n" +
			"            System.out.println(((Tree)r.tree).toStringTree());\n" +
			"            ((CommonTree)r.tree).sanityCheckParentAndChildIndexes();\n" +
			"		 }\n" +
			"        $else$\n" +
			"        CommonTreeNodeStream nodes = new CommonTreeNodeStream((Tree)r.tree);\n" +
			"        nodes.setTokenStream(tokens);\n" +
			"        $treeParserName$ walker = new $treeParserName$(nodes);\n" +
			"        walker.$treeParserStartRuleName$();\n" +
			"        $endif$\n" +
			"    }\n" +
			"}"
			);
		ST createParserST =
			new ST(
			"        Profiler2 profiler = new Profiler2();\n"+
			"        $parserName$ parser = new $parserName$(tokens,profiler);\n" +
			"        profiler.setParser(parser);\n");
		if ( !debug ) {
			createParserST =
				new ST(
				"        $parserName$ parser = new $parserName$(tokens);\n");
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("treeParserName", treeParserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		outputFileST.add("treeParserStartRuleName", treeParserStartRuleName);
		writeFile(tmpdir, "Test.java", outputFileST.toString());
	}

	/** Parser creates trees and so does the tree parser */
	protected void writeTreeAndTreeTestFile(String parserName,
											String treeParserName,
											String lexerName,
											String parserStartRuleName,
											String treeParserStartRuleName,
											boolean debug)
	{
		ST outputFileST = new ST(
			"import org.antlr.runtime.*;\n" +
			"import org.antlr.runtime.tree.*;\n" +
			"import org.antlr.runtime.debug.*;\n" +
			"\n" +
			"class Profiler2 extends Profiler {\n" +
			"    public void terminate() { ; }\n" +
			"}\n"+
			"public class Test {\n" +
			"    public static void main(String[] args) throws Exception {\n" +
			"        CharStream input = new ANTLRFileStream(args[0]);\n" +
			"        $lexerName$ lex = new $lexerName$(input);\n" +
			"        TokenRewriteStream tokens = new TokenRewriteStream(lex);\n" +
			"        $createParser$\n"+
			"        $parserName$.$parserStartRuleName$_return r = parser.$parserStartRuleName$();\n" +
			"        ((CommonTree)r.tree).sanityCheckParentAndChildIndexes();\n" +
			"        CommonTreeNodeStream nodes = new CommonTreeNodeStream((Tree)r.tree);\n" +
			"        nodes.setTokenStream(tokens);\n" +
			"        $treeParserName$ walker = new $treeParserName$(nodes);\n" +
			"        $treeParserName$.$treeParserStartRuleName$_return r2 = walker.$treeParserStartRuleName$();\n" +
			"		 CommonTree rt = ((CommonTree)r2.tree);\n" +
			"		 if ( rt!=null ) System.out.println(((CommonTree)r2.tree).toStringTree());\n" +
			"    }\n" +
			"}"
			);
		ST createParserST =
			new ST(
			"        Profiler2 profiler = new Profiler2();\n"+
			"        $parserName$ parser = new $parserName$(tokens,profiler);\n" +
			"        profiler.setParser(parser);\n");
		if ( !debug ) {
			createParserST =
				new ST(
				"        $parserName$ parser = new $parserName$(tokens);\n");
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("treeParserName", treeParserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		outputFileST.add("treeParserStartRuleName", treeParserStartRuleName);
		writeFile(tmpdir, "Test.java", outputFileST.toString());
	}

	protected void writeTemplateTestFile(String parserName,
										 String lexerName,
										 String parserStartRuleName,
										 boolean debug)
	{
		ST outputFileST = new ST(
			"import org.antlr.runtime.*;\n" +
			"import org.antlr.stringtemplate.*;\n" +
			"import org.antlr.stringtemplate.language.*;\n" +
			"import org.antlr.runtime.debug.*;\n" +
			"import java.io.*;\n" +
			"\n" +
			"class Profiler2 extends Profiler {\n" +
			"    public void terminate() { ; }\n" +
			"}\n"+
			"public class Test {\n" +
			"    static String templates =\n" +
			"    		\"group test;\"+" +
			"    		\"foo(x,y) ::= \\\"<x> <y>\\\"\";\n"+
			"    static STGroup group ="+
			"    		new STGroup(new StringReader(templates)," +
			"					AngleBracketTemplateLexer.class);"+
			"    public static void main(String[] args) throws Exception {\n" +
			"        CharStream input = new ANTLRFileStream(args[0]);\n" +
			"        $lexerName$ lex = new $lexerName$(input);\n" +
			"        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
			"        $createParser$\n"+
			"		 parser.setTemplateLib(group);\n"+
			"        $parserName$.$parserStartRuleName$_return r = parser.$parserStartRuleName$();\n" +
			"        if ( r.st!=null )\n" +
			"            System.out.print(r.st.toString());\n" +
			"	 	 else\n" +
			"            System.out.print(\"\");\n" +
			"    }\n" +
			"}"
			);
		ST createParserST =
			new ST(
			"        Profiler2 profiler = new Profiler2();\n"+
			"        $parserName$ parser = new $parserName$(tokens,profiler);\n" +
			"        profiler.setParser(parser);\n");
		if ( !debug ) {
			createParserST =
				new ST(
				"        $parserName$ parser = new $parserName$(tokens);\n");
		}
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(tmpdir, "Test.java", outputFileST.toString());
	}

    protected void eraseFiles(final String filesEndingWith) {
        File tmpdirF = new File(tmpdir);
        String[] files = tmpdirF.list();
        for(int i = 0; files!=null && i < files.length; i++) {
            if ( files[i].endsWith(filesEndingWith) ) {
                new File(tmpdir+"/"+files[i]).delete();
            }
        }
    }

    protected void eraseFiles() {
        File tmpdirF = new File(tmpdir);
        String[] files = tmpdirF.list();
        for(int i = 0; files!=null && i < files.length; i++) {
            new File(tmpdir+"/"+files[i]).delete();
        }
    }

    protected void eraseTempDir() {
        File tmpdirF = new File(tmpdir);
        if ( tmpdirF.exists() ) {
            eraseFiles();
            tmpdirF.delete();
        }
    }

	public String getFirstLineOfException() {
		if ( this.stderrDuringParse ==null ) {
			return null;
		}
		String[] lines = this.stderrDuringParse.split("\n");
		String prefix="Exception in thread \"main\" ";
		return lines[0].substring(prefix.length(),lines[0].length());
	}

    public String sortLinesInString(String s) {
        String lines[] = s.split("\n");
        Arrays.sort(lines);
        List<String> linesL = Arrays.asList(lines);
        StringBuffer buf = new StringBuffer();
        for (String l : linesL) {
            buf.append(l);
            buf.append('\n');
        }
        return buf.toString();
    }
    
    /**
     * When looking at a result set that consists of a Map/HashTable
     * we cannot rely on the output order, as the hashing algorithm or other aspects
     * of the implementation may be different on differnt JDKs or platforms. Hence
     * we take the Map, convert the keys to a List, sort them and Stringify the Map, which is a
     * bit of a hack, but guarantees that we get the same order on all systems. We assume that
     * the keys are strings.
     * 
     * @param m The Map that contains keys we wish to return in sorted order
     * @return A string that represents all the keys in sorted order.
     */
    public String sortMapToString(Map m) {
        
        System.out.println("Map toString looks like: " + m.toString());
        // Pass in crap, and get nothing back
        //
        if  (m == null) {
            return null;
        }
        
        // Sort the keys in the Map
        //
        TreeMap nset = new TreeMap(m);
        
        System.out.println("Tree map looks like: " + nset.toString());
        return nset.toString();
    }

    // override to track errors

    public void assertEquals(String msg, Object a, Object b) { try {Assert.assertEquals(msg,a,b);} catch (Error e) {lastTestFailed=true; throw e;} }
    public void assertEquals(Object a, Object b) { try {Assert.assertEquals(a,b);} catch (Error e) {lastTestFailed=true; throw e;} }
    public void assertEquals(String msg, long a, long b) { try {Assert.assertEquals(msg,a,b);} catch (Error e) {lastTestFailed=true; throw e;} }
    public void assertEquals(long a, long b) { try {Assert.assertEquals(a,b);} catch (Error e) {lastTestFailed=true; throw e;} }

    public void assertTrue(String msg, boolean b) { try {Assert.assertTrue(msg,b);} catch (Error e) {lastTestFailed=true; throw e;} }
    public void assertTrue(boolean b) { try {Assert.assertTrue(b);} catch (Error e) {lastTestFailed=true; throw e;} }

    public void assertFalse(String msg, boolean b) { try {Assert.assertFalse(msg,b);} catch (Error e) {lastTestFailed=true; throw e;} }
    public void assertFalse(boolean b) { try {Assert.assertFalse(b);} catch (Error e) {lastTestFailed=true; throw e;} }

    public void assertNotNull(String msg, Object p) { try {Assert.assertNotNull(msg, p);} catch (Error e) {lastTestFailed=true; throw e;} }
    public void assertNotNull(Object p) { try {Assert.assertNotNull(p);} catch (Error e) {lastTestFailed=true; throw e;} }

    public void assertNull(String msg, Object p) { try {Assert.assertNull(msg, p);} catch (Error e) {lastTestFailed=true; throw e;} }
    public void assertNull(Object p) { try {Assert.assertNull(p);} catch (Error e) {lastTestFailed=true; throw e;} }
}
