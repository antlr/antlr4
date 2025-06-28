package org.antlr.v4.runtime.perf;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.perf.ParserPerfUtils;

// You must generate these using ANTLR or maven plugin first!
import org.antlr.v4.runtime.perf.ExprLexer;
import org.antlr.v4.runtime.perf.ExprParser;

import java.nio.file.Files;
import java.nio.file.Path;

public class BenchmarkTest {
    public static void main(String[] args) throws Exception {
        // Load large test input file
        String inputText = Files.readString(Path.of("G:/antlr4/runtime/Java/src/org/antlr/v4/runtime/perf/input.txt"));
        CharStream input = CharStreams.fromString(inputText);

        ExprLexer lexer = new ExprLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);

        // Comment out to compare performance with parse tree
        ParserPerfUtils.disableTreeBuilding(parser);

        long start = System.currentTimeMillis();
        parser.prog();
        long end = System.currentTimeMillis();

        System.out.println("Parse time: " + (end - start) + " ms");
    }
}
