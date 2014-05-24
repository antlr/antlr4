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
                "(COMBINED_GRAMMAR T (RULES (RULE s (BLOCK (ALT e ';'))) (RULE e (BLOCK (ALT (BLOCK (ALT {} ('-' (ELEMENT_OPTIONS (= tokenIndex 43) (= charIndex 64) (= line 6) (= charPos 4))) (e (ELEMENT_OPTIONS (= tokenIndex 45) (= charIndex 68) (= line 6) (= charPos 8) (= p 2)))) (ALT (ID (ELEMENT_OPTIONS (= tokenIndex 49) (= charIndex 74) (= line 7) (= charPos 4))))) (* (BLOCK (ALT ({precpred(_ctx, 5)}? (ELEMENT_OPTIONS (= p 5))) ('*' (ELEMENT_OPTIONS (= tokenIndex 21) (= charIndex 29) (= line 3) (= charPos 6))) (e (ELEMENT_OPTIONS (= tokenIndex 23) (= charIndex 33) (= line 3) (= charPos 10) (= p 6)))) (ALT ({precpred(_ctx, 4)}? (ELEMENT_OPTIONS (= p 4))) ('+' (ELEMENT_OPTIONS (= tokenIndex 29) (= charIndex 41) (= line 4) (= charPos 6))) (e (ELEMENT_OPTIONS (= tokenIndex 31) (= charIndex 45) (= line 4) (= charPos 10) (= p 5)))) (ALT ({precpred(_ctx, 3)}? (ELEMENT_OPTIONS (= p 3))) ('.' (ELEMENT_OPTIONS (= tokenIndex 37) (= charIndex 53) (= line 5) (= charPos 6))) (ID (ELEMENT_OPTIONS (= tokenIndex 39) (= charIndex 57) (= line 5) (= charPos 10)))))))))))";
        assertEquals(expectedTree, g.ast.toStringTree());

        String expectedElementTokens =
                "[@5,11:11='s',<56>,2:0]\n" +
                "[@9,15:15='e',<56>,2:4]\n" +
                "[@11,17:19='';'',<61>,2:6]\n" +
                "[@15,23:23='e',<56>,3:0]\n" +
                "[@43,64:66=''-'',<61>,6:4]\n" +
                "[@45,68:68='e',<56>,6:8]\n" +
                "[@49,74:75='ID',<65>,7:4]\n" +
                "[@21,29:31=''*'',<61>,3:6]\n" +
                "[@23,33:33='e',<56>,3:10]\n" +
                "[@29,41:43=''+'',<61>,4:6]\n" +
                "[@31,45:45='e',<56>,4:10]\n" +
                "[@37,53:55=''.'',<61>,5:6]\n" +
                "[@39,57:58='ID',<65>,5:10]";

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
                "(COMBINED_GRAMMAR T (RULES (RULE s (BLOCK (ALT e ';'))) (RULE e (BLOCK (ALT (BLOCK (ALT {} ('-' (ELEMENT_OPTIONS (= tokenIndex 47) (= charIndex 68) (= line 6) (= charPos 4))) (e (ELEMENT_OPTIONS (= tokenIndex 49) (= charIndex 72) (= line 6) (= charPos 8) (= p 2)))) (ALT (ID (ELEMENT_OPTIONS (= tokenIndex 53) (= charIndex 78) (= line 7) (= charPos 4))))) (* (BLOCK (ALT ({precpred(_ctx, 5)}? (ELEMENT_OPTIONS (= p 5))) ('*' (ELEMENT_OPTIONS (= tokenIndex 21) (= charIndex 29) (= line 3) (= charPos 6))) (= x (e (ELEMENT_OPTIONS (= tokenIndex 25) (= charIndex 35) (= line 3) (= charPos 12) (= p 6))))) (ALT ({precpred(_ctx, 4)}? (ELEMENT_OPTIONS (= p 4))) ('+' (ELEMENT_OPTIONS (= tokenIndex 31) (= charIndex 43) (= line 4) (= charPos 6))) (e (ELEMENT_OPTIONS (= tokenIndex 33) (= charIndex 47) (= line 4) (= charPos 10) (= p 5)))) (ALT ({precpred(_ctx, 3)}? (ELEMENT_OPTIONS (= p 3))) ('.' (ELEMENT_OPTIONS (= tokenIndex 39) (= charIndex 55) (= line 5) (= charPos 6))) (= y (ID (ELEMENT_OPTIONS (= tokenIndex 43) (= charIndex 61) (= line 5) (= charPos 12))))))))))))";
        assertEquals(expectedTree, g.ast.toStringTree());

        String expectedElementTokens =
                "[@5,11:11='s',<56>,2:0]\n" +
                "[@9,15:15='e',<56>,2:4]\n" +
                "[@11,17:19='';'',<61>,2:6]\n" +
                "[@15,23:23='e',<56>,3:0]\n" +
                "[@47,68:70=''-'',<61>,6:4]\n" +
                "[@49,72:72='e',<56>,6:8]\n" +
                "[@53,78:79='ID',<65>,7:4]\n" +
                "[@21,29:31=''*'',<61>,3:6]\n" +
                "[@25,35:35='e',<56>,3:12]\n" +
                "[@31,43:45=''+'',<61>,4:6]\n" +
                "[@33,47:47='e',<56>,4:10]\n" +
                "[@39,55:57=''.'',<61>,5:6]\n" +
                "[@43,61:62='ID',<65>,5:12]";

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
                "(COMBINED_GRAMMAR T (RULES (RULE s (BLOCK (ALT e ';'))) (RULE e (BLOCK (ALT (BLOCK (ALT {} ('-' (ELEMENT_OPTIONS (= tokenIndex 49) (= charIndex 73) (= line 6) (= charPos 4))) (e (ELEMENT_OPTIONS (= tokenIndex 51) (= charIndex 77) (= line 6) (= charPos 8) (= p 2)))) (ALT (ID (ELEMENT_OPTIONS (= tokenIndex 55) (= charIndex 83) (= line 7) (= charPos 4))))) (* (BLOCK (ALT ({precpred(_ctx, 5)}? (ELEMENT_OPTIONS (= p 5))) (= op (SET ('*' (ELEMENT_OPTIONS (= tokenIndex 24) (= charIndex 33) (= line 3) (= charPos 10))) ('/' (ELEMENT_OPTIONS (= tokenIndex 26) (= charIndex 37) (= line 3) (= charPos 14))))) (e (ELEMENT_OPTIONS (= tokenIndex 29) (= charIndex 42) (= line 3) (= charPos 19) (= p 6)))) (ALT ({precpred(_ctx, 4)}? (ELEMENT_OPTIONS (= p 4))) ('+' (ELEMENT_OPTIONS (= tokenIndex 35) (= charIndex 50) (= line 4) (= charPos 6))) (e (ELEMENT_OPTIONS (= tokenIndex 37) (= charIndex 54) (= line 4) (= charPos 10) (= p 5)))) (ALT ({precpred(_ctx, 3)}? (ELEMENT_OPTIONS (= p 3))) ('.' (ELEMENT_OPTIONS (= tokenIndex 43) (= charIndex 62) (= line 5) (= charPos 6))) (ID (ELEMENT_OPTIONS (= tokenIndex 45) (= charIndex 66) (= line 5) (= charPos 10)))))))))))";
        assertEquals(expectedTree, g.ast.toStringTree());

        String expectedElementTokens =
                "[@5,11:11='s',<56>,2:0]\n" +
                "[@9,15:15='e',<56>,2:4]\n" +
                "[@11,17:19='';'',<61>,2:6]\n" +
                "[@15,23:23='e',<56>,3:0]\n" +
                "[@49,73:75=''-'',<61>,6:4]\n" +
                "[@51,77:77='e',<56>,6:8]\n" +
                "[@55,83:84='ID',<65>,7:4]\n" +
                "[@24,33:35=''*'',<61>,3:10]\n" +
                "[@26,37:39=''/'',<61>,3:14]\n" +
                "[@29,42:42='e',<56>,3:19]\n" +
                "[@35,50:52=''+'',<61>,4:6]\n" +
                "[@37,54:54='e',<56>,4:10]\n" +
                "[@43,62:64=''.'',<61>,5:6]\n" +
                "[@45,66:67='ID',<65>,5:10]";

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
