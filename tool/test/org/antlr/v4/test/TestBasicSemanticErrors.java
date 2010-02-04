package org.antlr.v4.test;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.semantics.SemanticsPipeline;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.Grammar;
import org.junit.Test;

public class TestBasicSemanticErrors extends BaseTest {
    public static class InOutPair { String in, out; }
    static String[] pairs = {
        // INPUT
        "grammar A;\n" +
        "\n" +
        "options {\n" +
        "        output=template;\n" +
        "}\n" +
        "\n" +
        "a : ID<Foo> -> ID ;\n" +
        "\n" +
        "b : A^ | ((B!|C)) -> ICK;",
        // YIELDS
        "error(68): A.g:7:7: alts with rewrites can't use heterogeneous types left of ->\n" +
        "error(77): A.g:9:4: AST operator with non-AST output option: ^\n" +
        "error(77): A.g:9:11: AST operator with non-AST output option: !\n" +
        "error(78): A.g:9:11: rule b alt 2 uses rewrite syntax and also an AST operator",

        // INPUT
        "tree grammar B;\n" +
        "options {\n" +
        "\tfilter=true;\n" +
        "\tbacktrack=false;\n" +
        "\toutput=template;\n" +
        "}\n" +
        "\n" +
        "a : A;\n" +
        "\n" +
        "b : ^(. A) ;",
        // YIELDS
        "error(79): B.g:10:6: Wildcard invalid as root; wildcard can itself be a tree\n" +
        "error(80): B.g:1:5: option backtrack=false conflicts with tree grammar filter mode\n" +
        "error(80): B.g:1:5: option output=template conflicts with tree grammar filter mode",
    };

    @Test public void testErrors() {
        for (int i = 0; i < pairs.length; i+=2) {
            String input = pairs[i];
            String expect = pairs[i+1];
            ErrorQueue equeue = new ErrorQueue();
            ErrorManager.setErrorListener(equeue);
            try {
                String[] lines = input.split("\n");
                int lastSpace = lines[0].lastIndexOf(' ');
                int semi = lines[0].lastIndexOf(';');
                String fileName = lines[0].substring(lastSpace+1, semi)+".g";
                Grammar g = new Grammar(fileName, input);
                g.loadImportedGrammars();
                SemanticsPipeline sem = new SemanticsPipeline();
                sem.process(g);
            }
            catch (RecognitionException re) {
                re.printStackTrace(System.err);
            }
            String actual = equeue.toString();
            assertEquals(expect,actual);
        }
    }
}