package org.antlr.v4.test.impl;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.ErrorQueue;
import org.antlr.v4.tool.DefaultToolListener;
import org.junit.runner.Description;

import java.net.URL;

/**
 * Created by jason on 4/1/15.
 */
public interface AntlrTestDelegate {
    void testWillStart(Description description);

    void testDidFinish(Description description);

    String loadFile(URL url, String encoding);

    void mkdir(String path);

    void writeFile(String dir, String fileName, String content);

    Class<?> loadCompiledClass(String name);

    String execLexer(String grammarFileName, String grammarStr, String lexerName, String input, boolean showDFA);

    String execParser(String grammarFileName,
                      String grammarStr,
                      String parserName,
                      String lexerName,
                      String startRuleName,
                      String input, boolean debug,
                      boolean profile);

    /**
     * runs antlr and then compiles the generated sources.
     * @param grammarFileName the name of the grammar file. usually ending in g4.
     * @param grammarStr grammar text
     * @param parserName name of the parser that will be created
     * @param lexerName name of the lexer that will be created
     * @param defaultListener if true, adds a {@link DefaultToolListener} to the antlr tool
     * @param extraOptions any extra options to give to the antlr tool
     * @return returns true if everything was o.k.
     */
    //TODO parser/lexer names should not be necessary
    boolean generateAndBuildRecognizer(String grammarFileName,
                                       String grammarStr,
                                       String parserName,
                                       String lexerName,
                                       boolean defaultListener,
                                       String... extraOptions);

    String getWorkingDir();

    String getStdErrDuringParse();

    Pair<Parser, Lexer> getParserAndLexer(String input, String parserName, String lexerName);

    ErrorQueue antlr(String grammarFileName, boolean defaultListener, String... extraOptions);
}
