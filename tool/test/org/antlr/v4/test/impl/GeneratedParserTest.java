package org.antlr.v4.test.impl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ProfilingATNSimulator;

import java.util.Arrays;

/**
 * Created by jason on 3/23/15.
 */
public abstract class GeneratedParserTest implements GeneratedTest{

    public boolean debug = false;
    public boolean profile = false;

    ProfilingATNSimulator profiler;


    protected abstract Lexer createLexer(CharStream input);

    protected abstract Parser createParser(TokenStream tokens);

    protected abstract ParserRuleContext callStartRule(Parser parser);

    public String input;

    @Override
    public void test() {
        test(input);
    }

    public void test(String input){

        CharStream stream = new ANTLRInputStream(input);
        Lexer lexer = createLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Parser parser = createParser(tokens);

        if (debug) parser.addErrorListener(new DiagnosticErrorListener());

        parser.setBuildParseTree(true);

        if (profile) {
            profiler = new ProfilingATNSimulator(parser);
            parser.setInterpreter(profiler);
        }

        ParserRuleContext tree = callStartRule(parser);

        if(profile){
            System.out.println(Arrays.toString(profiler.getDecisionInfo()));
        }
        TreeShapeListener.check(tree);
    }
}
