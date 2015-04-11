package org.antlr.v4.test.impl;

import org.stringtemplate.v4.ST;

/**
 * Created by jason on 4/1/15.
 */
public class TestCodeGenerator {

    public static String generateParserTestCode(String parserName,
                                                String lexerName,
                                                String parserStartRuleName,
                                                boolean debug,
                                                boolean profile) {
        return generateParserTestCode("Test", parserName, lexerName, parserStartRuleName, debug, profile);
    }

    public static String generateParserTestCode(String testClassName,
                                                String parserName,
                                                String lexerName,
                                                String parserStartRuleName,
                                                boolean debug,
                                                boolean profile) {
        ST outputFileST = new ST(
                "import org.antlr.v4.runtime.*;\n" +
                "import org.antlr.v4.runtime.tree.*;\n" +
                "import org.antlr.v4.runtime.atn.*;\n" +
                "import java.util.Arrays;\n" +
                "import org.antlr.v4.test.TreeShapeListener;" +
                "\n" +
                "public class <className> {\n" +
                "    public static void main(String[] args) throws Exception {\n" +
                "        CharStream input = new ANTLRFileStream(args[0]);\n" +
                "        <lexerName> lex = new <lexerName>(input);\n" +
                "        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
                "        <parserName> parser = new <parserName>(tokens);\n" +
                "<if(debug)>" +
                "        parser.addErrorListener(new DiagnosticErrorListener());\n" +
                "<endif>" +
                "		 parser.setBuildParseTree(true);\n" +
                "<if(profile)>" +
                "        ProfilingATNSimulator profiler = new ProfilingATNSimulator(parser);\n" +
                "        parser.setInterpreter(profiler);" +
                "<endif>" +
                "        ParserRuleContext tree = parser.<parserStartRuleName>();\n" +
                "<if(profile)>" +
                "        System.out.println(Arrays.toString(profiler.getDecisionInfo()));" +
                "<endif>\n" +
                "        ParseTreeWalker.DEFAULT.walk(new TreeShapeListener(), tree);\n" +
                "    }\n" +
                "\n" +
                "}"
        );
        outputFileST.add("className", testClassName);
        outputFileST.add("parserName", parserName);
        outputFileST.add("lexerName", lexerName);
        outputFileST.add("parserStartRuleName", parserStartRuleName);
        outputFileST.add("debug", debug);
        outputFileST.add("profile", profile);


        return outputFileST.render();
    }

    public static String generateLexerTestCode(boolean showDFA, String lexerName) {
        return generateLexerTestCode("Test", showDFA, lexerName);
    }

    public static String generateLexerTestCode(String testClassName, boolean showDFA, String lexerName) {
        ST outputFileST = new ST(
                "import org.antlr.v4.runtime.*;\n" +
                "\n" +
                "public class <className> {\n" +
                "    public static void main(String[] args) throws Exception {\n" +
                "        CharStream input = new ANTLRFileStream(args[0]);\n" +
                "        <lexerName> lex = new <lexerName>(input);\n" +
                "        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
                "        tokens.fill();\n" +
                "        for (Object t : tokens.getTokens()) System.out.println(t);\n" +
                "<if(showDFA)>" +
                "        System.out.print(lex.getInterpreter().getDFA(Lexer.DEFAULT_MODE).toLexerString());\n" +
                "<endif>" +
                "    }\n" +
                "}"
        );
        outputFileST.add("className", testClassName);
        outputFileST.add("showDFA", showDFA);
        outputFileST.add("lexerName", lexerName);
        return outputFileST.render();
    }
}
