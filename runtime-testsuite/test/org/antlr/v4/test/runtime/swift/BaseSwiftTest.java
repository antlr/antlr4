package org.antlr.v4.test.runtime.swift;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.test.runtime.ErrorQueue;
import org.antlr.v4.test.runtime.RuntimeTestSupport;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.DefaultToolListener;
import org.stringtemplate.v4.ST;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class BaseSwiftTest implements RuntimeTestSupport {

    public static class StreamVacuum implements Runnable {
        StringBuilder buf = new StringBuilder();
        BufferedReader in;
        Thread sucker;
        public StreamVacuum(InputStream in) {
            this.in = new BufferedReader( new InputStreamReader(in) );
        }
        public void start() {
            sucker = new Thread(this);
            sucker.start();
        }
        @Override
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
        @Override
        public String toString() {
            return buf.toString();
        }
    }

    public String tmpdir = null;

    /** Errors found while running antlr */
    protected StringBuilder antlrToolErrors;

    /** If error during parser execution, store stderr here; can't return
     *  stdout and stderr.  This doesn't trap errors from running antlr.
     */
    protected String stderrDuringParse;

    @Override
    public void testSetUp() throws Exception {
        // new output dir for each test
        String propName =  "antlr-swift-test-dir";
        String prop = System.getProperty(propName);
        if(prop!=null && prop.length()>0) {
            tmpdir = prop;
        }
        else {
            tmpdir = new File(System.getProperty("java.io.tmpdir"), getClass().getSimpleName()+
                    "-"+Thread.currentThread().getName()+"-"+System.currentTimeMillis()).getAbsolutePath();
        }
        antlrToolErrors = new StringBuilder();

    }

    @Override
    public void testTearDown() throws Exception {

    }

    @Override
    public void eraseTempDir() {

    }

    @Override
    public String getTmpDir() {
        return tmpdir;
    }

    @Override
    public String getStdout() {
        return null;
    }

    @Override
    public String getParseErrors() {
        return stderrDuringParse;
    }

    @Override
    public String getANTLRToolErrors() {
        if (antlrToolErrors.length() == 0) {
            return null;
        }
        return antlrToolErrors.toString();
    }

    @Override
    public String execLexer(String grammarFileName, String grammarStr, String lexerName, String input, boolean showDFA) {
        boolean success = rawGenerateAndBuildRecognizer(grammarFileName,
                grammarStr,
                null,
                lexerName,"-no-listener");
        assertTrue(success);
        writeFile(tmpdir, "input", input);
        writeLexerTestFile(lexerName, showDFA);
        String output = execModule("Test.swift");
        return output;
    }

    @Override
    public String execParser(String grammarFileName, String grammarStr, String parserName, String lexerName, String listenerName, String visitorName, String startRuleName, String input, boolean showDiagnosticErrors) {
        return null;
    }

    public static void writeFile(String dir, String fileName, String content) {
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

    protected void writeParserTestFile(String parserName,
                                       String lexerName,
                                       String parserStartRuleName,
                                       boolean debug,
                                       boolean profile)
    {

        ST outputFileST = new ST(
                "import Antlr4\n" +
                        "import Foundation\n" +
                        "setbuf(__stdoutp, nil)\n" +
                        "class TreeShapeListener: ParseTreeListener{\n" +
                        "    func visitTerminal(_ node: TerminalNode){ }\n" +
                        "    func visitErrorNode(_ node: ErrorNode){ }\n" +
                        "    func enterEveryRule(_ ctx: ParserRuleContext) throws { }\n" +
                        "    func exitEveryRule(_ ctx: ParserRuleContext) throws {\n" +
                        "        for i in 0..\\<ctx.getChildCount() {\n" +
                        "            let parent = ctx.getChild(i)?.getParent()\n" +
                        "            if (!(parent is RuleNode) || (parent as! RuleNode ).getRuleContext() !== ctx) {\n" +
                        "                throw ANTLRError.illegalState(msg: \"Invalid parse tree shape detected.\")\n" +
                        "            }\n" +
                        "        }\n" +
                        "    }\n" +
                        "}\n" +
                        "\n" +
                        "do {\n" +
                        "let args = CommandLine.arguments\n" +
                        "let input = ANTLRFileStream(args[1])\n" +
                        "let lex = <lexerName>(input)\n" +
                        "let tokens = CommonTokenStream(lex)\n" +
                        "<createParser>\n" +
                        "parser.setBuildParseTree(true)\n" +
                        "<profile>\n" +
                        "let tree = try parser.<parserStartRuleName>()\n" +
                        "<if(profile)>print(profiler.getDecisionInfo().description)<endif>\n" +
                        "try ParseTreeWalker.DEFAULT.walk(TreeShapeListener(), tree)\n" +
                        "}catch ANTLRException.cannotInvokeStartRule {\n" +
                        "    print(\"error occur: cannotInvokeStartRule\")\n" +
                        "}catch ANTLRException.recognition(let e )   {\n" +
                        "    print(\"error occur\\(e)\")\n" +
                        "}catch {\n" +
                        "    print(\"error occur\")\n" +
                        "}\n"
        );
        ST createParserST = new ST("       let parser = try <parserName>(tokens)\n");
        if ( debug ) {
            createParserST =
                    new ST(
                            "        let parser = try <parserName>(tokens)\n" +
                                    "        parser.addErrorListener(DiagnosticErrorListener())\n");
        }
        if ( profile ) {
            outputFileST.add("profile",
                    "let profiler = ProfilingATNSimulator(parser)\n" +
                            "parser.setInterpreter(profiler)");
        }
        else {
            outputFileST.add("profile", new ArrayList<Object>());
        }
        outputFileST.add("createParser", createParserST);
        outputFileST.add("parserName", parserName);
        outputFileST.add("lexerName", lexerName);
        outputFileST.add("parserStartRuleName", parserStartRuleName);
        writeFile(tmpdir, "main.swift", outputFileST.render());
    }

    protected void writeLexerTestFile(String lexerName, boolean showDFA) {
        ST outputFileST = new ST(
                "import Antlr4\n" +
                        "import Foundation\n" +
                        "setbuf(__stdoutp, nil)\n" +
                        "let args = CommandLine.arguments\n" +
                        "let input = ANTLRFileStream(args[1])\n" +
                        "let lex = <lexerName>(input)\n" +
                        "let tokens = CommonTokenStream(lex)\n" +
                        "do {\n" +
                        "    try tokens.fill()\n" +
                        "}catch ANTLRException.cannotInvokeStartRule {\n" +
                        "    print(\"error occur: cannotInvokeStartRule\")\n" +
                        "}catch ANTLRException.recognition(let e )   {\n" +
                        "    print(\"error occur\\(e)\")\n" +
                        "}catch {\n" +
                        "    print(\"error occur\")\n" +
                        "}\n" +
                        "for t in tokens.getTokens() {\n" +
                        "  print(t)\n" +
                        "}\n" +
                        (showDFA?"print(lex.getInterpreter().getDFA(Lexer.DEFAULT_MODE).toLexerString(), terminator: \"\" )\n":"") );

        outputFileST.add("lexerName", lexerName);
        writeFile(tmpdir, "main.swift", outputFileST.render());
    }

    public String execModule(String fileName) {
        String runtimePath = locateRuntime();
        String includePath = runtimePath + "/runtime/src";
        String binPath = new File(new File(tmpdir), "a.out").getAbsolutePath();
        String inputPath = new File(new File(tmpdir), "input").getAbsolutePath();

        // Build runtime using cmake once.
        synchronized (runtimeBuiltOnce) {
            if ( !runtimeBuiltOnce ) {
                try {
                    String command[] = {"clang++", "--version"};
                    String output = runCommand(command, tmpdir, "printing compiler version");
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
            String command[] = { "ln", "-s", runtimePath + "/dist/libantlr4-runtime." + libExtension };
            if (runCommand(command, tmpdir, "sym linking C++ runtime") == null)
                return null;
        }
        catch (Exception e) {
            System.err.println("can't exec module: " + fileName);
        }

        try {
            List<String> command2 = new ArrayList<String>(Arrays.asList("clang++", "-std=c++11", "-I", includePath, "-L.", "-lantlr4-runtime"));
            command2.addAll(allCppFiles(tmpdir));
            if (runCommand(command2.toArray(new String[0]), tmpdir, "building test binary") == null) {
                return null;
            }
        }
        catch (Exception e) {
            System.err.println("can't compile test module: " + e.getMessage());
            return null;
        }

        // Now run the newly minted binary. Reset the error output, as we could have got compiler warnings which are not relevant here.
        this.stderrDuringParse = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(binPath, inputPath);
            builder.directory(new File(tmpdir));
            Map<String, String> env = builder.environment();
            env.put("LD_PRELOAD", runtimePath + "/dist/libantlr4-runtime.so"); // For linux.
            String output = runProcess(builder, "running test binary");
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
            System.err.println("can't exec module: " + fileName + "\nerror is: "+ e.getMessage());
        }

        return null;
    }

    protected String locateRuntime() {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final URL runtimeSrc = loader.getResource("Cpp");
        if (runtimeSrc == null) {
            throw new RuntimeException("Cannot find runtime");
        }
        return runtimeSrc.getPath();
    }

    private String runProcess(ProcessBuilder builder, String description) throws Exception {
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
            this.stderrDuringParse = stderrVacuum.toString();
        }
        if (errcode != 0) {
            String err = "execution failed with error code: "+errcode;
            if ( this.stderrDuringParse!=null ) {
                this.stderrDuringParse += err;
            }
            else {
                this.stderrDuringParse = err;
            }
        }

        return output;
    }

    private String runCommand(String command[], String workPath, String description) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(new File(workPath));

        return runProcess(builder, description);
    }

    /** Return true if all is well */
    protected boolean rawGenerateRecognizer(String grammarFileName,
                                            String grammarStr,
                                            String parserName,
                                            String lexerName,
                                            String... extraOptions)
    {
        return rawGenerateRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
    }

    /** Return true if all is well */
    protected boolean rawGenerateRecognizer(String grammarFileName,
                                            String grammarStr,
                                            String parserName,
                                            String lexerName,
                                            boolean defaultListener,
                                            String... extraOptions)
    {
        ErrorQueue equeue = antlr(grammarFileName, grammarStr, defaultListener, extraOptions);
        if (!equeue.errors.isEmpty()) {
            return false;
        }

        List<String> files = new ArrayList<String>();
        if ( lexerName!=null ) {
            files.add(lexerName+".swift");
            files.add(lexerName+"ATN.swift");
        }
        if ( parserName!=null ) {
            files.add(parserName+".swift");
            files.add(parserName+"ATN.swift");
            Set<String> optionsSet = new HashSet<String>(Arrays.asList(extraOptions));
            String grammarName = grammarFileName.substring(0, grammarFileName.lastIndexOf('.'));
            if (!optionsSet.contains("-no-listener")) {
                files.add(grammarName+"Listener.swift");
                files.add(grammarName+"BaseListener.swift");
            }
            if (optionsSet.contains("-visitor")) {
                files.add(grammarName+"Visitor.swift");
                files.add(grammarName+"BaseVisitor.swift");
            }
        }
        addSourceFiles(files.toArray(new String[files.size()]));
        return true;
    }

    protected ErrorQueue antlr(String grammarFileName, boolean defaultListener, String... extraOptions) {
        final List<String> options = new ArrayList<String>();
        Collections.addAll(options, extraOptions);
        options.add("-Dlanguage=Swift");
        if ( !options.contains("-o") ) {
            options.add("-o");
            options.add(tmpdir);
        }
        if ( !options.contains("-lib") ) {
            options.add("-lib");
            options.add(tmpdir);
        }
        if ( !options.contains("-encoding") ) {
            options.add("-encoding");
            options.add("UTF-8");
        }
        options.add(new File(tmpdir,grammarFileName).toString());

        final String[] optionsA = new String[options.size()];
        options.toArray(optionsA);
        Tool antlr = newTool(optionsA);
        ErrorQueue equeue = new ErrorQueue(antlr);
        antlr.addListener(equeue);
        if (defaultListener) {
            antlr.addListener(new DefaultToolListener(antlr));
        }
        antlr.processGrammarsOnCommandLine();

        if ( !defaultListener && !equeue.errors.isEmpty() ) {
            System.err.println("antlr reports errors from "+options);
            for (int i = 0; i < equeue.errors.size(); i++) {
                ANTLRMessage msg = equeue.errors.get(i);
                System.err.println(msg);
            }
            System.out.println("!!!\ngrammar:");
            try {
                System.out.println(new String(Utils.readFile(tmpdir+"/"+grammarFileName)));
            }
            catch (IOException ioe) {
                System.err.println(ioe.toString());
            }
            System.out.println("###");
        }
        if ( !defaultListener && !equeue.warnings.isEmpty() ) {
            System.err.println("antlr reports warnings from "+options);
            for (int i = 0; i < equeue.warnings.size(); i++) {
                ANTLRMessage msg = equeue.warnings.get(i);
                System.err.println(msg);
            }
        }

        return equeue;
    }

    protected ErrorQueue antlr(String grammarFileName, String grammarStr, boolean defaultListener, String... extraOptions) {
        System.out.println("dir "+tmpdir);
        mkdir(tmpdir);
        writeFile(tmpdir, grammarFileName, grammarStr);
        return antlr(grammarFileName, defaultListener, extraOptions);
    }

    protected static void mkdir(String dir) {
        File f = new File(dir);
        f.mkdirs();
    }

    protected Tool newTool(String[] args) {
        Tool tool = new Tool(args);
        return tool;
    }

    protected Tool newTool() {
        Tool tool = new Tool(new String[] {"-o", tmpdir});
        return tool;
    }
}
