/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.tool;

import org.antlr.runtime.Token;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTokenPositionOptions {
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
				"(COMBINED_GRAMMAR T (RULES (RULE s (BLOCK (ALT e ';'))) (RULE e (BLOCK (ALT (BLOCK (ALT {} ('-' (ELEMENT_OPTIONS (= tokenIndex 43))) (e (ELEMENT_OPTIONS (= tokenIndex 45) (= p 2)))) (ALT (ID (ELEMENT_OPTIONS (= tokenIndex 49))))) (* (BLOCK (ALT ({precpred(_ctx, 5)}? (ELEMENT_OPTIONS (= p 5))) ('*' (ELEMENT_OPTIONS (= tokenIndex 21))) (e (ELEMENT_OPTIONS (= tokenIndex 23) (= p 6)))) (ALT ({precpred(_ctx, 4)}? (ELEMENT_OPTIONS (= p 4))) ('+' (ELEMENT_OPTIONS (= tokenIndex 29))) (e (ELEMENT_OPTIONS (= tokenIndex 31) (= p 5)))) (ALT ({precpred(_ctx, 3)}? (ELEMENT_OPTIONS (= p 3))) ('.' (ELEMENT_OPTIONS (= tokenIndex 37))) (ID (ELEMENT_OPTIONS (= tokenIndex 39)))))))))))";
		assertEquals(expectedTree, g.ast.toStringTree());

		String expectedElementTokens =
				"[@5,11:11='s',<54>,2:0]\n" +
				"[@9,15:15='e',<54>,2:4]\n" +
				"[@11,17:19='';'',<59>,2:6]\n" +
				"[@15,23:23='e',<54>,3:0]\n" +
				"[@43,64:66=''-'',<59>,6:4]\n" +
				"[@45,68:68='e',<54>,6:8]\n" +
				"[@49,74:75='ID',<62>,7:4]\n" +
				"[@21,29:31=''*'',<59>,3:6]\n" +
				"[@23,33:33='e',<54>,3:10]\n" +
				"[@29,41:43=''+'',<59>,4:6]\n" +
				"[@31,45:45='e',<54>,4:10]\n" +
				"[@37,53:55=''.'',<59>,5:6]\n" +
				"[@39,57:58='ID',<62>,5:10]";

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
				"(COMBINED_GRAMMAR T (RULES (RULE s (BLOCK (ALT e ';'))) (RULE e (BLOCK (ALT (BLOCK (ALT {} ('-' (ELEMENT_OPTIONS (= tokenIndex 47))) (e (ELEMENT_OPTIONS (= tokenIndex 49) (= p 2)))) (ALT (ID (ELEMENT_OPTIONS (= tokenIndex 53))))) (* (BLOCK (ALT ({precpred(_ctx, 5)}? (ELEMENT_OPTIONS (= p 5))) ('*' (ELEMENT_OPTIONS (= tokenIndex 21))) (= x (e (ELEMENT_OPTIONS (= tokenIndex 25) (= p 6))))) (ALT ({precpred(_ctx, 4)}? (ELEMENT_OPTIONS (= p 4))) ('+' (ELEMENT_OPTIONS (= tokenIndex 31))) (e (ELEMENT_OPTIONS (= tokenIndex 33) (= p 5)))) (ALT ({precpred(_ctx, 3)}? (ELEMENT_OPTIONS (= p 3))) ('.' (ELEMENT_OPTIONS (= tokenIndex 39))) (= y (ID (ELEMENT_OPTIONS (= tokenIndex 43))))))))))))";
		assertEquals(expectedTree, g.ast.toStringTree());

		String expectedElementTokens =
				"[@5,11:11='s',<54>,2:0]\n" +
				"[@9,15:15='e',<54>,2:4]\n" +
				"[@11,17:19='';'',<59>,2:6]\n" +
				"[@15,23:23='e',<54>,3:0]\n" +
				"[@47,68:70=''-'',<59>,6:4]\n" +
				"[@49,72:72='e',<54>,6:8]\n" +
				"[@53,78:79='ID',<62>,7:4]\n" +
				"[@21,29:31=''*'',<59>,3:6]\n" +
				"[@25,35:35='e',<54>,3:12]\n" +
				"[@31,43:45=''+'',<59>,4:6]\n" +
				"[@33,47:47='e',<54>,4:10]\n" +
				"[@39,55:57=''.'',<59>,5:6]\n" +
				"[@43,61:62='ID',<62>,5:12]";

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
				"(COMBINED_GRAMMAR T (RULES (RULE s (BLOCK (ALT e ';'))) (RULE e (BLOCK (ALT (BLOCK (ALT {} ('-' (ELEMENT_OPTIONS (= tokenIndex 49))) (e (ELEMENT_OPTIONS (= tokenIndex 51) (= p 2)))) (ALT (ID (ELEMENT_OPTIONS (= tokenIndex 55))))) (* (BLOCK (ALT ({precpred(_ctx, 5)}? (ELEMENT_OPTIONS (= p 5))) (= op (SET ('*' (ELEMENT_OPTIONS (= tokenIndex 24))) ('/' (ELEMENT_OPTIONS (= tokenIndex 26))))) (e (ELEMENT_OPTIONS (= tokenIndex 29) (= p 6)))) (ALT ({precpred(_ctx, 4)}? (ELEMENT_OPTIONS (= p 4))) ('+' (ELEMENT_OPTIONS (= tokenIndex 35))) (e (ELEMENT_OPTIONS (= tokenIndex 37) (= p 5)))) (ALT ({precpred(_ctx, 3)}? (ELEMENT_OPTIONS (= p 3))) ('.' (ELEMENT_OPTIONS (= tokenIndex 43))) (ID (ELEMENT_OPTIONS (= tokenIndex 45)))))))))))";
		assertEquals(expectedTree, g.ast.toStringTree());

		String expectedElementTokens =
				"[@5,11:11='s',<54>,2:0]\n" +
				"[@9,15:15='e',<54>,2:4]\n" +
				"[@11,17:19='';'',<59>,2:6]\n" +
				"[@15,23:23='e',<54>,3:0]\n" +
				"[@49,73:75=''-'',<59>,6:4]\n" +
				"[@51,77:77='e',<54>,6:8]\n" +
				"[@55,83:84='ID',<62>,7:4]\n" +
				"[@24,33:35=''*'',<59>,3:10]\n" +
				"[@26,37:39=''/'',<59>,3:14]\n" +
				"[@29,42:42='e',<54>,3:19]\n" +
				"[@35,50:52=''+'',<59>,4:6]\n" +
				"[@37,54:54='e',<54>,4:10]\n" +
				"[@43,62:64=''.'',<59>,5:6]\n" +
				"[@45,66:67='ID',<62>,5:10]";

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
