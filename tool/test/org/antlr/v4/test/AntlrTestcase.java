package org.antlr.v4.test;

import org.antlr.v4.Tool;
import org.antlr.v4.automata.ATNFactory;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.test.impl.AntlrTestDelegate;
import org.antlr.v4.test.impl.AntlrTestSettings;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by jason on 4/1/15.
 */
public class AntlrTestcase {
    public static String NEWLINE = System.getProperty("line.separator");

    final AntlrTestDelegate delegate;

    @Rule
    public final TestRule junitAdapter = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            delegate.testWillStart(description);
        }

        @Override
        protected void finished(Description description) {
            delegate.testDidFinish();
        }
    };

    public AntlrTestcase(AntlrTestDelegate delegate) {
        this.delegate = delegate;
    }

    public AntlrTestcase() {
        this(AntlrTestSettings.getHelper());
    }

    protected String tmpdir() {
        return delegate.getWorkingDir();
    }

    protected String stderrDuringParse() {
        return delegate.getStdErrDuringParse();
    }


    protected String load(URL fileName, String encoding) throws IOException {
        return delegate.loadFile(fileName, encoding);
    }
    protected ErrorQueue antlr(String grammarFileName, boolean defaultListener, String... extraOptions) {
        return delegate.antlr(grammarFileName, defaultListener, extraOptions);
    }

    protected void mkdir(String path) {
        delegate.mkdir(path);
    }

    protected void writeFile(String dir, String fileName, String content) {
        delegate.writeFile(dir, fileName, content);
    }

    protected Class<?> loadCompiledClass(String name) throws Exception {
        return delegate.loadCompiledClass(name);
    }

    protected Class<? extends Lexer> loadLexerClassFromTempDir(String name) throws Exception {
        return loadCompiledClass(name).asSubclass(Lexer.class);
    }

    protected Class<? extends Parser> loadParserClassFromTempDir(String name) throws Exception {
        return loadCompiledClass(name).asSubclass(Parser.class);
    }


    /**
     * Return true if all is well
     */
    protected boolean generateAndBuildRecognizer(String grammarFileName,
                                                 String grammarStr,
                                                 String parserName,
                                                 String lexerName,
                                                 String... extraOptions) {
        return generateAndBuildRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
    }

    /**
     * Return true if all is well
     */
    protected boolean generateAndBuildRecognizer(String grammarFileName,
                                                 String grammarStr,
                                                 String parserName,
                                                 String lexerName,
                                                 boolean defaultListener,
                                                 String... extraOptions) {
        return delegate.generateAndBuildRecognizer(grammarFileName,
                                                   grammarStr,
                                                   parserName,
                                                   lexerName,
                                                   defaultListener,
                                                   extraOptions);
    }


    protected String execLexer(String grammarFileName,
                               String grammarStr,
                               String lexerName,
                               String input) {
        return execLexer(grammarFileName, grammarStr, lexerName, input, false);
    }

    protected String execLexer(String grammarFileName, String grammarStr, String lexerName, String input, boolean showDFA) {
        return delegate.execLexer(grammarFileName, grammarStr, lexerName, input, showDFA);
    }

    public ParseTree execStartRule(String startRuleName, Parser parser)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Method startRule;
        Object[] args = null;
        try {
            startRule = parser.getClass().getMethod(startRuleName);
        } catch (NoSuchMethodException nsme) {
            // try with int _p arg for recursive func
            startRule = parser.getClass().getMethod(startRuleName, int.class);
            args = new Integer[]{0};
        }
        //		System.out.println("parse tree = "+result.toStringTree(parser));
        return (ParseTree) startRule.invoke(parser, args);
    }

    protected String execParser(String grammarFileName,
                                String grammarStr,
                                String parserName,
                                String lexerName,
                                String startRuleName,
                                String input, boolean debug) {
        return execParser(grammarFileName, grammarStr, parserName,
                          lexerName, startRuleName, input, debug, false);
    }

    protected String execParser(String grammarFileName,
                                String grammarStr,
                                String parserName,
                                String lexerName,
                                String startRuleName,
                                String input, boolean debug,
                                boolean profile) {
        return delegate.execParser(grammarFileName,
                                   grammarStr,
                                   parserName,
                                   lexerName,
                                   startRuleName,
                                   input,
                                   debug,
                                   profile);
    }


    protected Pair<Parser, Lexer> getParserAndLexer(String input,
                                                    String parserName, String lexerName) {
        return delegate.getParserAndLexer(input, parserName, lexerName);
    }


    public void testErrors(String[] pairs, boolean printTree) {
        for (int i = 0; i < pairs.length; i += 2) {
            String input = pairs[i];
            String expect = pairs[i + 1];

            String[] lines = input.split("\n");
            String fileName = getFilenameFromFirstLineOfGrammar(lines[0]);

            writeFile(tmpdir(),fileName,input);
            ErrorQueue equeue = antlr(fileName, false);

            String actual = equeue.toString(true);
            actual = actual.replace(delegate.getWorkingDir() + File.separator, "");
           // System.err.println(actual);
            String msg = input;
            msg = msg.replace("\n", "\\n");
            msg = msg.replace("\r", "\\r");
            msg = msg.replace("\t", "\\t");

            assertEquals(msg,expect, actual);
        }
    }

    public void testActions(String templates, String actionName, String action, String expected) throws org.antlr.runtime.RecognitionException {
        int lp = templates.indexOf('(');
        String name = templates.substring(0, lp);
        STGroup group = new STGroupString(templates);
        ST st = group.getInstanceOf(name);
        st.add(actionName, action);
        String grammar = st.render();
        ErrorQueue equeue = new ErrorQueue();
        Grammar g = new Grammar(grammar, equeue);
        if (g.ast != null && !g.ast.hasErrors) {
            SemanticPipeline sem = new SemanticPipeline(g);
            sem.process();

            ATNFactory factory = new ParserATNFactory(g);
            if (g.isLexer()) factory = new LexerATNFactory((LexerGrammar) g);
            g.atn = factory.createATN();

            CodeGenerator gen = new CodeGenerator(g);
            ST outputFileST = gen.generateParser();
            String output = outputFileST.render();
            //System.out.println(output);
            String b = "#" + actionName + "#";
            int start = output.indexOf(b);
            String e = "#end-" + actionName + "#";
            int end = output.indexOf(e);
            String snippet = output.substring(start + b.length(), end);
            assertEquals(expected, snippet);
        }
        if (equeue.size() > 0) {
            System.err.println(equeue.toString());
        }
    }


    private String getFilenameFromFirstLineOfGrammar(String line) {
        String fileName = "A" + Tool.GRAMMAR_EXTENSION;
        int grIndex = line.lastIndexOf("grammar");
        int semi = line.lastIndexOf(';');
        if (grIndex >= 0 && semi >= 0) {
            int space = line.indexOf(' ', grIndex);
            fileName = line.substring(space + 1, semi) + Tool.GRAMMAR_EXTENSION;
        }
        if (fileName.length() == Tool.GRAMMAR_EXTENSION.length()) fileName = "A" + Tool.GRAMMAR_EXTENSION;
        return fileName;
    }


    protected ATN createATN(Grammar g, boolean useSerializer) {
        return TestUtils.createATN(g, useSerializer);
    }

    protected List<String> realElements(List<String> elements) {
        return TestUtils.realElements(elements);
    }

    protected IntegerList getTokenTypesViaATN(String input, LexerATNSimulator lexerATN) {
        return TestUtils.getTokenTypesViaATN(input, lexerATN);
    }

    protected void checkRuleATN(Grammar g, String ruleName, String expecting) {
        TestUtils.checkRuleATN(g, ruleName, expecting);
    }

    protected void semanticProcess(Grammar g) {
        TestUtils.semanticProcess(g);
    }


}
