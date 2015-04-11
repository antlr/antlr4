package org.antlr.v4.test.impl.wip;


import org.antlr.v4.test.impl.GeneratedLexerTest;
import org.antlr.v4.test.impl.GeneratedParserTest;

/**
 * Created by jason on 3/28/15.
 */
public class NewTestCodeGenerator {

    static final String PARSER_TEST_FQN = GeneratedParserTest.class.getName();
    static final String LEXER_TEST_FQN = GeneratedLexerTest.class.getName();


    public static String generateParserTest(String lexName, String parserName, String startRule) {
        return "import org.antlr.v4.runtime.CharStream;\n" +
                "import org.antlr.v4.runtime.Lexer;\n" +
                "import org.antlr.v4.runtime.Parser;\n" +
                "import org.antlr.v4.runtime.ParserRuleContext;\n" +
                "import org.antlr.v4.runtime.TokenStream;\n" +
                "import " + PARSER_TEST_FQN + ";\n" +
                "\n" +
                "public class ParserTest extends " + PARSER_TEST_FQN + " {\n" +
                "\n" +
                "    @Override\n" +
                "    protected Lexer createLexer(CharStream charStream) {\n" +
                "        return new " + lexName + "(charStream);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    protected Parser createParser(TokenStream tokenStream) {\n" +
                "        return new " + parserName + "(tokenStream);\n"
                + "    }\n" +
                "\n" +
                "    @Override\n" +
                "    protected ParserRuleContext callStartRule(Parser parser) {\n" +
                "        return((" + parserName + ") parser)." + startRule + "();\n" +
                "    }\n" +
                "\n" +
                "}";
    }

    public static String generateLexerTest(String lexerName) {

        return "import org.antlr.v4.runtime.CharStream;\n" +
                "import org.antlr.v4.runtime.Lexer;\n" +
                "\n" +
                "import " + LEXER_TEST_FQN + ";\n" +
                "\n" +
                "public class LexerTest extends " + LEXER_TEST_FQN + " {\n" +
                "    @Override\n" +
                "    protected Lexer createLexer(CharStream input) {\n" +
                "        return new " + lexerName + "(input);\n" +
                "    }\n" +
                "}";
    }


}
