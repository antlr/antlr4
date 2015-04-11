package org.antlr.v4.test.impl.wip;


import org.antlr.v4.test.impl.*;
import org.junit.runner.Description;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Created by jason on 4/8/15.
 */
public class NewAntlrDelegate extends DefaultTestDelegate {
    private static final Logger log = Logger.getLogger(NewAntlrDelegate.class.getName());

    private static String pkgName(Class<?> c) {
        String name = c.getPackage().getName();
        if (name.endsWith("rt.java")) return "rt";
        if (name.endsWith("tool")) return "tool";
        return "other";
    }


    @Override
    public void testWillStart(Description description) {

        if (AntlrTestSettings.CREATE_PER_TEST_DIRECTORIES) {

            String testDirectory = pkgName(description.getTestClass()) +
                                   File.separatorChar +
                                   description.getTestClass().getSimpleName() +
                                   File.separatorChar +
                                   description.getMethodName();

            tmpdir = new File(AntlrTestSettings.BASE_TEST_DIR, testDirectory).getAbsolutePath();

        } else {

            tmpdir = new File(AntlrTestSettings.BASE_TEST_DIR,
                              description.getTestClass().getSimpleName()).getAbsolutePath();


        }
        if (!AntlrTestSettings.PRESERVE_TEST_DIR && new File(getWorkingDir()).exists()) {
            eraseGeneratedFiles();
        }

    }


    @Override
    public String execLexer(String grammarFileName,
                            String grammarStr,
                            String lexerName,
                            String input,
                            boolean showDFA) {
        // if (!new File(getWorkingDir(), LEXER_TEST + ".class").exists()) {
        // log.log(Level.INFO, "generating files for: {0}", grammarFileName);
        boolean success = generateAndBuildRecognizer(grammarFileName,
                                                     grammarStr,
                                                     null,
                                                     lexerName);
        assertTrue(success);
        writeLexerTestFile(lexerName, showDFA);
        compile(LEXER_TEST_FILE_NAME);
        //  }


        GeneratedLexerTest test = createLexerTestInstance();

        test.showDFA = showDFA;
        test.input = input;

        GeneratedTestRunner.Result result = GeneratedTestRunner.run(test);
        String err = stderrDuringParse = result.err;
        if (err.length() > 0) {
            System.err.println(err);
        }

        return result.out;
    }

    @Override
    public String execParser(String grammarFileName,
                             String grammarStr,
                             String parserName,
                             String lexerName,
                             String startRuleName,
                             String input,
                             boolean debug,
                             boolean profile) {
        //  if (!new File(getWorkingDir(), PARSER_TEST + ".class").exists()) {
        log.log(Level.INFO, "generating files for: {0}", grammarFileName);
        boolean success = generateAndBuildRecognizer(grammarFileName,
                                                     grammarStr,
                                                     parserName,
                                                     lexerName,
                                                     "-visitor");
        assertTrue(success);

        if (parserName == null) {
            writeLexerTestFile(lexerName, false);
        } else {
            writeParserTestFile(parserName,
                                lexerName,
                                startRuleName,
                                debug,
                                profile);
        }

        compile(PARSER_TEST_FILE_NAME);
        //  }

        GeneratedParserTest test = createParserTestInstance();
        test.debug = debug;
        test.profile = profile;
        test.input = input;

        GeneratedTestRunner.Result result = GeneratedTestRunner.run(test);
        String err = stderrDuringParse = result.err;
        if (err.length() > 0) {
            System.err.println(err);
        }

        return result.out;
    }

    @Override
    protected void writeParserTestFile(String parserName,
                                       String lexerName,
                                       String parserStartRuleName,
                                       boolean debug,
                                       boolean profile) {
        writeFile(getWorkingDir(),
                  PARSER_TEST_FILE_NAME,
                  NewTestCodeGenerator.generateParserTest(lexerName, parserName, parserStartRuleName));
    }

    @Override
    protected void writeLexerTestFile(String lexerName, boolean showDFA) {
        writeFile(getWorkingDir(), LEXER_TEST_FILE_NAME, NewTestCodeGenerator.generateLexerTest(lexerName));
    }

    GeneratedParserTest createParserTestInstance() {
        try {
            return loadCompiledClass(PARSER_TEST).asSubclass(GeneratedParserTest.class).newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    GeneratedLexerTest createLexerTestInstance() {
        try {
            return loadCompiledClass(LEXER_TEST).asSubclass(GeneratedLexerTest.class).newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static final String LEXER_TEST = "LexerTest";
    static final String PARSER_TEST = "ParserTest";

    static final String LEXER_TEST_FILE_NAME = LEXER_TEST + ".java";
    static final String PARSER_TEST_FILE_NAME = PARSER_TEST + ".java";

    static class Singleton {
        static final NewAntlrDelegate INSTANCE = new NewAntlrDelegate();
    }

    public static AntlrTestDelegate getInstace() {
        return Singleton.INSTANCE;
    }

}
