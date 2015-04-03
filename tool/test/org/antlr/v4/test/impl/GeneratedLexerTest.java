package org.antlr.v4.test.impl;


import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

/**
 * Created by jason on 3/29/15.
 */
public abstract class GeneratedLexerTest implements GeneratedTest{

    public boolean showDFA = false;
    public String input;

    @Override
    public void test() {
        test(input);
    }

    protected abstract Lexer createLexer(CharStream input);


    public void test(String input) {
        CharStream charStream = new ANTLRInputStream(input);
        Lexer lexer = createLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();
        for (Object token : tokens.getTokens()) {
            System.out.println(token);
        }

        if (showDFA) {
            System.out.print(lexer.getInterpreter().getDFA(Lexer.DEFAULT_MODE).toLexerString());
        }
    }
}
