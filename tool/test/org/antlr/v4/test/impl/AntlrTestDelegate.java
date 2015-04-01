package org.antlr.v4.test.impl;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.ErrorQueue;
import org.junit.runner.Description;

import java.net.URL;

/**
 * Created by jason on 4/1/15.
 */
public interface AntlrTestDelegate {
    void testWillStart(Description description);

    void testDidFinish();

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

    boolean generateAndBuildRecognizer(String grammarFileName,
                                       String grammarStr,
                                       String parserName,
                                       String lexerName,
                                       boolean defaultListener,
                                       String... extraOptions);

    String getGenPath();

    String getStdErrDuringParse();

    Pair<Parser, Lexer> getParserAndLexer(String input, String parserName, String lexerName);

    ErrorQueue antlr(String grammarFileName, boolean defaultListener, String... extraOptions);
}
