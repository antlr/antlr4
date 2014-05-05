package org.antlr.v4.test;

import org.antlr.runtime.Token;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestTokenPositionOptions extends BaseTest {
    @Test public void testLeftRecursionRewrite() throws Exception {
        Grammar g = new Grammar(
                "grammar T;\n" +
                "s : e ';' ;\n" +
                "e : e '*' e\n" +
                "  | e '+' e\n" +
                "  | e '.' ID\n" +
                "  | '-' e\n" +
                "  | ID\n" +
                "  ;\n" +
                "ID : [a-z]+ ;\n"
        );

        String expectedTree =
                "(COMBINED_GRAMMAR T (RULES (RULE s (BLOCK (ALT e ';'))) (RULE e (BLOCK (ALT (BLOCK (ALT {} ('-' (ELEMENT_OPTIONS (= charIndex 64) (= line 6) (= charPos 4))) (e (ELEMENT_OPTIONS (= charIndex 68) (= line 6) (= charPos 8) (= p 2)))) (ALT (ID (ELEMENT_OPTIONS (= charIndex 74) (= line 7) (= charPos 4))))) (* (BLOCK (ALT ({precpred(_ctx, 5)}? (ELEMENT_OPTIONS (= p 5))) ('*' (ELEMENT_OPTIONS (= charIndex 29) (= line 3) (= charPos 6))) (e (ELEMENT_OPTIONS (= charIndex 33) (= line 3) (= charPos 10) (= p 6)))) (ALT ({precpred(_ctx, 4)}? (ELEMENT_OPTIONS (= p 4))) ('+' (ELEMENT_OPTIONS (= charIndex 41) (= line 4) (= charPos 6))) (e (ELEMENT_OPTIONS (= charIndex 45) (= line 4) (= charPos 10) (= p 5)))) (ALT ({precpred(_ctx, 3)}? (ELEMENT_OPTIONS (= p 3))) ('.' (ELEMENT_OPTIONS (= charIndex 53) (= line 5) (= charPos 6))) (ID (ELEMENT_OPTIONS (= charIndex 57) (= line 5) (= charPos 10)))))))))))";
        assertEquals(expectedTree, g.ast.toStringTree());

        String expectedElementTokens =
                "[@5,11:11='s',<56>,2:0]\n" +
                "[@9,15:15='e',<56>,2:4]\n" +
                "[@11,17:19='';'',<61>,2:6]\n" +
                "[@15,23:23='e',<56>,3:0]\n" +
                "[@8,64:66=''-'',<61>,6:4]\n" +
                "[@23,68:68='e',<56>,6:8]\n" +
                "[@44,74:75='ID',<65>,7:4]\n" +
                "[@70,29:31=''*'',<61>,3:6]\n" +
                "[@85,33:33='e',<56>,3:10]\n" +
                "[@113,41:43=''+'',<61>,4:6]\n" +
                "[@128,45:45='e',<56>,4:10]\n" +
                "[@156,53:55=''.'',<61>,5:6]\n" +
                "[@171,57:58='ID',<65>,5:10]";

        IntervalSet types =
                new IntervalSet(ANTLRParser.TOKEN_REF,
                ANTLRParser.STRING_LITERAL,
                ANTLRParser.RULE_REF);
        List<GrammarAST> nodes = g.ast.getNodesWithTypePreorderDFS(types);
        List<Token> tokens = new ArrayList<Token>();
        for (GrammarAST node : nodes) {
            tokens.add(node.getToken());
        }
        assertEquals(expectedElementTokens, Utils.join(tokens.toArray(), "\n"));
    }

    @Test public void testLeftRecursionWithLabels() throws Exception {
        Grammar g = new Grammar(
                "grammar T;\n" +
                "s : e ';' ;\n" +
                "e : e '*' x=e\n" +
                "  | e '+' e\n" +
                "  | e '.' y=ID\n" +
                "  | '-' e\n" +
                "  | ID\n" +
                "  ;\n" +
                "ID : [a-z]+ ;\n"
        );

        String expectedTree =
                "(COMBINED_GRAMMAR T (RULES (RULE s (BLOCK (ALT e ';'))) (RULE e (BLOCK (ALT (BLOCK (ALT {} ('-' (ELEMENT_OPTIONS (= charIndex 68) (= line 6) (= charPos 4))) (e (ELEMENT_OPTIONS (= charIndex 72) (= line 6) (= charPos 8) (= p 2)))) (ALT (ID (ELEMENT_OPTIONS (= charIndex 78) (= line 7) (= charPos 4))))) (* (BLOCK (ALT ({precpred(_ctx, 5)}? (ELEMENT_OPTIONS (= p 5))) ('*' (ELEMENT_OPTIONS (= charIndex 29) (= line 3) (= charPos 6))) (= x (e (ELEMENT_OPTIONS (= charIndex 35) (= line 3) (= charPos 12) (= p 6))))) (ALT ({precpred(_ctx, 4)}? (ELEMENT_OPTIONS (= p 4))) ('+' (ELEMENT_OPTIONS (= charIndex 43) (= line 4) (= charPos 6))) (e (ELEMENT_OPTIONS (= charIndex 47) (= line 4) (= charPos 10) (= p 5)))) (ALT ({precpred(_ctx, 3)}? (ELEMENT_OPTIONS (= p 3))) ('.' (ELEMENT_OPTIONS (= charIndex 55) (= line 5) (= charPos 6))) (= y (ID (ELEMENT_OPTIONS (= charIndex 61) (= line 5) (= charPos 12))))))))))))";
        assertEquals(expectedTree, g.ast.toStringTree());

        String expectedElementTokens =
                "[@5,11:11='s',<56>,2:0]\n" +
                "[@9,15:15='e',<56>,2:4]\n" +
                "[@11,17:19='';'',<61>,2:6]\n" +
                "[@15,23:23='e',<56>,3:0]\n" +
                "[@8,68:70=''-'',<61>,6:4]\n" +
                "[@23,72:72='e',<56>,6:8]\n" +
                "[@44,78:79='ID',<65>,7:4]\n" +
                "[@70,29:31=''*'',<61>,3:6]\n" +
                "[@87,35:35='e',<56>,3:12]\n" +
                "[@115,43:45=''+'',<61>,4:6]\n" +
                "[@130,47:47='e',<56>,4:10]\n" +
                "[@158,55:57=''.'',<61>,5:6]\n" +
                "[@175,61:62='ID',<65>,5:12]";

        IntervalSet types =
                new IntervalSet(ANTLRParser.TOKEN_REF,
                ANTLRParser.STRING_LITERAL,
                ANTLRParser.RULE_REF);
        List<GrammarAST> nodes = g.ast.getNodesWithTypePreorderDFS(types);
        List<Token> tokens = new ArrayList<Token>();
        for (GrammarAST node : nodes) {
            tokens.add(node.getToken());
        }
        assertEquals(expectedElementTokens, Utils.join(tokens.toArray(), "\n"));
    }

    @Test public void testLeftRecursionWithSet() throws Exception {
        Grammar g = new Grammar(
                "grammar T;\n" +
                "s : e ';' ;\n" +
                "e : e op=('*'|'/') e\n" +
                "  | e '+' e\n" +
                "  | e '.' ID\n" +
                "  | '-' e\n" +
                "  | ID\n" +
                "  ;\n" +
                "ID : [a-z]+ ;\n"
        );

        String expectedTree =
                "(COMBINED_GRAMMAR T (RULES (RULE s (BLOCK (ALT e ';'))) (RULE e (BLOCK (ALT (BLOCK (ALT {} ('-' (ELEMENT_OPTIONS (= charIndex 73) (= line 6) (= charPos 4))) (e (ELEMENT_OPTIONS (= charIndex 77) (= line 6) (= charPos 8) (= p 2)))) (ALT (ID (ELEMENT_OPTIONS (= charIndex 83) (= line 7) (= charPos 4))))) (* (BLOCK (ALT ({precpred(_ctx, 5)}? (ELEMENT_OPTIONS (= p 5))) (= op (SET ('*' (ELEMENT_OPTIONS (= charIndex 33) (= line 3) (= charPos 10))) ('/' (ELEMENT_OPTIONS (= charIndex 37) (= line 3) (= charPos 14))))) (e (ELEMENT_OPTIONS (= charIndex 42) (= line 3) (= charPos 19) (= p 6)))) (ALT ({precpred(_ctx, 4)}? (ELEMENT_OPTIONS (= p 4))) ('+' (ELEMENT_OPTIONS (= charIndex 50) (= line 4) (= charPos 6))) (e (ELEMENT_OPTIONS (= charIndex 54) (= line 4) (= charPos 10) (= p 5)))) (ALT ({precpred(_ctx, 3)}? (ELEMENT_OPTIONS (= p 3))) ('.' (ELEMENT_OPTIONS (= charIndex 62) (= line 5) (= charPos 6))) (ID (ELEMENT_OPTIONS (= charIndex 66) (= line 5) (= charPos 10)))))))))))";
        assertEquals(expectedTree, g.ast.toStringTree());

        String expectedElementTokens =
                "[@5,11:11='s',<56>,2:0]\n" +
                "[@9,15:15='e',<56>,2:4]\n" +
                "[@11,17:19='';'',<61>,2:6]\n" +
                "[@15,23:23='e',<56>,3:0]\n" +
                "[@8,73:75=''-'',<61>,6:4]\n" +
                "[@23,77:77='e',<56>,6:8]\n" +
                "[@44,83:84='ID',<65>,7:4]\n" +
                "[@73,33:35=''*'',<61>,3:10]\n" +
                "[@88,37:39=''/'',<61>,3:14]\n" +
                "[@104,42:42='e',<56>,3:19]\n" +
                "[@132,50:52=''+'',<61>,4:6]\n" +
                "[@147,54:54='e',<56>,4:10]\n" +
                "[@175,62:64=''.'',<61>,5:6]\n" +
                "[@190,66:67='ID',<65>,5:10]";

        IntervalSet types =
                new IntervalSet(ANTLRParser.TOKEN_REF,
                ANTLRParser.STRING_LITERAL,
                ANTLRParser.RULE_REF);
        List<GrammarAST> nodes = g.ast.getNodesWithTypePreorderDFS(types);
        List<Token> tokens = new ArrayList<Token>();
        for (GrammarAST node : nodes) {
            tokens.add(node.getToken());
        }
        assertEquals(expectedElementTokens, Utils.join(tokens.toArray(), "\n"));
    }

}
